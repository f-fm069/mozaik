package io.github.f_fm069.mozaik

import android.content.Context
import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Tracks
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.hls.HlsManifest

/**
 * Misst die Zeit vom Umschalten in Live TV bis zum ersten Bild (Einstellungen > Diagnose).
 * Nur Beobachtung: ändert nichts an der Wiedergabe. Gespeichert werden nur Zeiten und der Streamtyp (lokal, [ZapLogic]).
 * Die Adresse des Senders wird nur als Hash im Speicher gehalten, um die Meldungen dem richtigen Stream zuzuordnen.
 */
object ZapProbe {
    private const val PREFS = "mozaik_zap"
    private const val TIMEOUT = 30_000L

    private var t0 = 0L
    private var active = false
    private val marks = LongArray(ZapLogic.N) { -1 }
    private var urlHash = 0
    private var guess = -1
    private var conn: Any? = null
    private var inited = false

    /** Letzte Messungen, neueste zuerst. */
    var list by mutableStateOf<List<ZapLogic.Rec>>(emptyList())
        private set

    private fun prefs(c: Context) = c.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun init(c: Context) {
        if (inited) return
        inited = true
        list = prefs(c).getString("list", null)?.lines()?.mapNotNull { ZapLogic.decode(it) }.orEmpty()
    }

    fun reset(c: Context) {
        list = emptyList()
        prefs(c).edit().remove("list").apply()
    }

    private fun now() = SystemClock.elapsedRealtime()

    /** Taste gedrückt: neuer Sender (aus der Liste oder im Player). Eine laufende Messung wird verworfen. */
    @Synchronized fun begin() {
        t0 = now(); marks.fill(-1); active = true; urlHash = 0; guess = -1; conn = null
    }

    /** Kein Live-Sender (Film, Archiv) oder nicht neu geöffnet: nichts messen. */
    @Synchronized fun cancel() { active = false; conn = null }

    /** Der Stream wird jetzt geöffnet ([url] bleibt nur als Hash im Speicher). */
    @Synchronized fun opened(url: String) {
        if (!active) return
        if (now() - t0 > 10_000) { cancel(); return }
        urlHash = url.hashCode()
        guess = ZapLogic.guessFromUrl(url)
        mark(ZapLogic.OPEN, now())
    }

    @Synchronized private fun mark(i: Int, at: Long) {
        if (!active || marks[i] >= 0) return
        if (i != ZapLogic.OPEN && marks[ZapLogic.OPEN] < 0) return
        if (at - t0 > TIMEOUT) { cancel(); return }
        marks[i] = (at - t0).coerceAtLeast(0)
    }

    @Synchronized private fun isOpenedUrl(url: String): Boolean = active && urlHash != 0 && url.hashCode() == urlHash

    /** Live-Puffer (Zurückspulen) verbindet selbst: Server hat geantwortet. */
    fun tapConnected(url: String) { if (isOpenedUrl(url)) mark(ZapLogic.CONNECT, now()) }

    /** Live-Puffer: erste Daten vom Server. */
    fun tapData(url: String) { if (isOpenedUrl(url)) mark(ZapLogic.DATA, now()) }

    /** Für die HTTP-Quelle des Players: Antwort des Servers (inkl. Weiterleitungen) und erste Daten. */
    val transfer = object : TransferListener {
        override fun onTransferInitializing(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {}
        override fun onTransferStart(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {
            if (!isNetwork) return
            synchronized(this@ZapProbe) {
                if (conn == null && isOpenedUrl(dataSpec.uri.toString())) { conn = source; mark(ZapLogic.CONNECT, now()) }
            }
        }
        override fun onBytesTransferred(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean, bytesTransferred: Int) {
            if (bytesTransferred <= 0) return
            synchronized(this@ZapProbe) { if (conn === source) mark(ZapLogic.DATA, now()) }
        }
        override fun onTransferEnd(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {}
    }

    /** Meldungen des Players (Zeitpunkte direkt vom Player, nicht erst bei der Zustellung). */
    class Analytics(private val ctx: Context, private val player: ExoPlayer) : AnalyticsListener {
        override fun onTracksChanged(eventTime: AnalyticsListener.EventTime, tracks: Tracks) {
            if (!tracks.isEmpty) mark(ZapLogic.FORMAT, eventTime.realtimeMs)
        }
        override fun onVideoDecoderInitialized(eventTime: AnalyticsListener.EventTime, decoderName: String, initializedTimestampMs: Long, initializationDurationMs: Long) {
            mark(ZapLogic.DECODER, eventTime.realtimeMs)
        }
        override fun onRenderedFirstFrame(eventTime: AnalyticsListener.EventTime, output: Any, renderTimeMs: Long) {
            mark(ZapLogic.FRAME, eventTime.realtimeMs)
            tryFinish()
        }
        override fun onIsPlayingChanged(eventTime: AnalyticsListener.EventTime, isPlaying: Boolean) {
            if (isPlaying) { mark(ZapLogic.PLAY, eventTime.realtimeMs); tryFinish() }
        }
        override fun onPlayerError(eventTime: AnalyticsListener.EventTime, error: PlaybackException) { cancel() }

        private fun tryFinish() {
            val rec: ZapLogic.Rec
            synchronized(ZapProbe) {
                if (!active || marks[ZapLogic.FORMAT] < 0 || marks[ZapLogic.PLAY] < 0) return
                val hasVideo = player.currentTracks.containsType(C.TRACK_TYPE_VIDEO)
                if (hasVideo && marks[ZapLogic.FRAME] < 0) return
                val container = player.videoFormat?.containerMimeType ?: player.audioFormat?.containerMimeType
                rec = ZapLogic.Rec(ZapLogic.typeOf(player.currentManifest is HlsManifest, container, guess), marks.copyOf())
                active = false; conn = null
            }
            init(ctx)
            list = ZapLogic.add(list, rec)
            prefs(ctx).edit().putString("list", list.joinToString("\n") { ZapLogic.encode(it) }).apply()
        }
    }
}
