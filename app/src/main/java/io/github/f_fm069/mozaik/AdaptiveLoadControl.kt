package io.github.f_fm069.mozaik

import android.text.TextUtils
import androidx.media3.common.C
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.analytics.PlayerId
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.TrackGroupArray
import androidx.media3.exoplayer.trackselection.ExoTrackSelection
import androidx.media3.exoplayer.upstream.Allocator

/**
 * Puffersteuerung des Players: alles wie [DefaultLoadControl] (Größe des Vorrats, Speichergrenze), nur die Frage
 * „Ist genug geladen, um loszuspielen?“ wird bei jedem Start und nach jedem Ruckler neu beantwortet:
 * - mit der aktuell gelernten Puffer-Stufe (wächst bei Ruckeln, sinkt nach ruhiger Wiedergabe, siehe [BufferLogic]),
 * - mit „Schneller Kanalwechsel“ bei Live TV mit kürzerem Startpuffer ([live] setzt der Player je Titel).
 * Ohne Ruckler und ohne die Einstellung ergeben sich genau die bisherigen Werte.
 * Dateien auf dem Gerät (Aufnahmen) laufen unverändert über [DefaultLoadControl].
 */
@UnstableApi
class AdaptiveLoadControl(
    private val inner: DefaultLoadControl,
    private val baseStartMs: Int,
    private val baseRebufferMs: Int,
    private val byteLimit: Int
) : LoadControl {
    /** Läuft gerade ein Live-Sender (nicht Archiv)? */
    @Volatile var live = false

    private val window = Timeline.Window()
    private val period = Timeline.Period()

    override fun onPrepared(playerId: PlayerId) = inner.onPrepared(playerId)
    override fun onTracksSelected(parameters: LoadControl.Parameters, trackGroups: TrackGroupArray, trackSelections: Array<ExoTrackSelection?>) =
        inner.onTracksSelected(parameters, trackGroups, trackSelections)
    override fun onStopped(playerId: PlayerId) = inner.onStopped(playerId)
    override fun onReleased(playerId: PlayerId) = inner.onReleased(playerId)
    override fun getAllocator(playerId: PlayerId): Allocator = inner.getAllocator(playerId)
    override fun getBackBufferDurationUs(playerId: PlayerId): Long = inner.getBackBufferDurationUs(playerId)
    override fun retainBackBufferFromKeyframe(playerId: PlayerId): Boolean = inner.retainBackBufferFromKeyframe(playerId)
    override fun shouldContinueLoading(parameters: LoadControl.Parameters): Boolean = inner.shouldContinueLoading(parameters)
    override fun shouldContinuePreloading(playerId: PlayerId, timeline: Timeline, mediaPeriodId: MediaSource.MediaPeriodId, bufferedDurationUs: Long): Boolean =
        inner.shouldContinuePreloading(playerId, timeline, mediaPeriodId, bufferedDurationUs)

    override fun shouldStartPlayback(parameters: LoadControl.Parameters): Boolean {
        if (isLocal(parameters)) return inner.shouldStartPlayback(parameters)
        val level = BufferLogic.level(AppSettings.bufferMode, AppSettings.bufferLearned)
        val ms = if (parameters.rebuffering) BufferLogic.rebufferMs(baseRebufferMs, level)
        else BufferLogic.startMs(baseStartMs, level, live && AppSettings.fastZap)
        // Gleiche Regeln wie DefaultLoadControl: bei HLS-Live höchstens der halbe Live-Abstand, volle Speichergrenze startet auch
        var minUs = ms * 1000L
        if (parameters.targetLiveOffsetUs != C.TIME_UNSET) minUs = minOf(parameters.targetLiveOffsetUs / 2, minUs)
        val bufferedUs = Util.getPlayoutDurationForMediaDuration(parameters.bufferedDurationUs, parameters.playbackSpeed)
        return minUs <= 0 || bufferedUs >= minUs ||
            (byteLimit > 0 && inner.getAllocator(parameters.playerId).totalBytesAllocated >= byteLimit)
    }

    /** Wie in DefaultLoadControl: Datei, Inhalt, Asset usw. gelten als „auf dem Gerät“. */
    private fun isLocal(p: LoadControl.Parameters): Boolean = runCatching {
        val wi = p.timeline.getPeriodByUid(p.mediaPeriodId.periodUid, period).windowIndex
        val uri = p.timeline.getWindow(wi, window).mediaItem.localConfiguration?.uri ?: return false
        val s = uri.scheme
        TextUtils.isEmpty(s) || s in DefaultLoadControl.LOCAL_PLAYBACK_SCHEMES
    }.getOrDefault(false)
}
