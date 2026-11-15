package io.github.f_fm069.mozaik

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull

/**
 * „Beim Umschalten letztes Bild halten“: Beim Senderwechsel bleibt das letzte Bild des alten Senders stehen,
 * bis das erste Bild des neuen gezeigt wird (statt Schwarz).
 *
 * Warum ein kopiertes Bild: Beim Laden des neuen Senders sind die Spuren kurz leer, dann legt PlayerView seinen
 * schwarzen Vorhang (Shutter) über das Video, und der Player gibt den Video-Decoder frei – auf manchen Geräten wird
 * die Bildfläche dabei schwarz. Deshalb wird das aktuelle Bild vorher verkleinert kopiert (PixelCopy, nur im Speicher)
 * und als Bild über die Videofläche gelegt. Keine Netzwerkverbindung, kein zweiter Stream.
 */
class FrameHold {
    private var view: PlayerView? = null
    private var img: ImageView? = null
    private var bmp: Bitmap? = null
    private var pending: CompletableDeferred<Unit>? = null
    private val main = Handler(Looper.getMainLooper())

    /** Wird gerade ein Standbild gezeigt? */
    var active by mutableStateOf(false)
        private set
    /** Ändert sich bei jedem neuen Halten (Zeitgrenze startet neu). */
    var token by mutableIntStateOf(0)
        private set

    fun attach(v: PlayerView) { view = v }

    /** Aktuelles Videobild kopieren und darüberlegen. Läuft schon ein Standbild, bleibt es (nur die Zeitgrenze startet neu). */
    fun capture() {
        token++
        if (active) return
        val sv = view?.videoSurfaceView as? SurfaceView ?: return
        val w = sv.width; val h = sv.height
        if (w <= 0 || h <= 0 || !sv.holder.surface.isValid) return
        // Verkleinert (höchstens 1280 × 720): spart Speicher, als Standbild für wenige Sekunden reicht das
        val s = minOf(1f, 1280f / w, 720f / h)
        val bw = (w * s).toInt().coerceAtLeast(1); val bh = (h * s).toInt().coerceAtLeast(1)
        val b = bmp?.takeIf { it.width == bw && it.height == bh && !it.isRecycled }
            ?: runCatching { Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888) }.getOrNull() ?: return
        bmp = b
        val d = CompletableDeferred<Unit>()
        pending = d
        val ok = runCatching {
            PixelCopy.request(sv, b, { result ->
                if (result == PixelCopy.SUCCESS && pending === d) show(b)
                d.complete(Unit)
            }, main)
        }.isSuccess
        if (!ok) d.complete(Unit)
    }

    /** Kurz warten, bis die Kopie fertig ist (bevor der neue Sender geladen wird), höchstens [maxMs]. */
    suspend fun awaitCapture(maxMs: Long = 150) {
        val d = pending ?: return
        withTimeoutOrNull(maxMs) { d.await() }
    }

    private fun show(b: Bitmap) {
        val v = view ?: return
        val frame = v.findViewById<ViewGroup>(androidx.media3.ui.R.id.exo_content_frame) ?: return
        val iv = img ?: ImageView(v.context).also {
            it.scaleType = ImageView.ScaleType.FIT_XY
            frame.addView(it, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            img = it
        }
        iv.setImageBitmap(b)
        iv.visibility = View.VISIBLE
        iv.bringToFront()
        active = true
    }

    /** Standbild entfernen: erstes neues Bild, Fehler, Zeitgrenze, Player zu. */
    fun clear() {
        pending = null
        img?.let { it.setImageDrawable(null); it.visibility = View.GONE }
        active = false
    }
}
