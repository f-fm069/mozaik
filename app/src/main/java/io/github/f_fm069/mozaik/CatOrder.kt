package io.github.f_fm069.mozaik

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue

/**
 * Sortierung der Anbieter-Kategorien je Bereich ([LIVE], [MOVIE], [SERIES]) und je Profil, siehe [CatOrderLogic].
 * Speicher "ipvio_catorder": "m_BEREICH" = Modus, "o_BEREICH" = eigene Reihenfolge (ein Name pro Zeile).
 * Übernommen aus IPVIO Stream (GPL-3.0).
 */
object CatOrder {
    const val LIVE = "live"
    const val MOVIE = "movie"
    const val SERIES = "series"

    private val modes = mutableStateMapOf<String, Int>()
    private val own = mutableStateMapOf<String, List<String>>()
    private var inited = false
    /** Ändert sich mit jedem neuen Modus oder jeder neuen Reihenfolge: Schlüssel für gemerkte, sortierte Listen. */
    var version by mutableIntStateOf(0)
        private set
    private fun prefs(c: Context) = Profiles.sp(c, "ipvio_catorder")

    fun init(c: Context) {
        if (inited) return
        inited = true
        val p = prefs(c)
        for (a in listOf(LIVE, MOVIE, SERIES)) {
            modes[a] = p.getInt("m_$a", CatOrderLogic.PROVIDER).coerceIn(0, 2)
            own[a] = CatOrderLogic.decode(p.getString("o_$a", ""))
        }
    }

    /** Nach einem Profilwechsel oder dem Zurücksetzen neu einlesen. */
    fun reload(c: Context) {
        inited = false
        init(c)
        version++
    }

    fun mode(area: String): Int = modes[area] ?: CatOrderLogic.PROVIDER

    /** [names] in der Reihenfolge des Anbieters → Reihenfolge zum Anzeigen. */
    fun sorted(area: String, names: List<String>): List<String> = CatOrderLogic.apply(names, mode(area), own[area].orEmpty())

    fun setMode(c: Context, area: String, m: Int) {
        init(c)
        modes[area] = m.coerceIn(0, 2)
        prefs(c).edit().putInt("m_$area", modes[area] ?: 0).apply()
        version++
    }

    /** Verschiebt [name] um [delta] Plätze innerhalb von [names]; schaltet auf die eigene Reihenfolge um. */
    fun move(c: Context, area: String, names: List<String>, name: String, delta: Int) =
        save(c, area, CatOrderLogic.move(sorted(area, names), name, delta))

    fun toTop(c: Context, area: String, names: List<String>, name: String) =
        save(c, area, CatOrderLogic.toTop(sorted(area, names), name))

    /** Speichert eine fertige Reihenfolge (z. B. nach dem Verschieben mit Hoch/Runter); schaltet auf die eigene Reihenfolge um. */
    fun setOrder(c: Context, area: String, order: List<String>) = save(c, area, order)

    private fun save(c: Context, area: String, order: List<String>) {
        init(c)
        // Plätze von Kategorien behalten, die gerade nicht in der Liste sind (z. B. andere Playlist aktiv)
        val inOrder = order.toHashSet()
        val keep = own[area].orEmpty().filter { it !in inOrder }
        own[area] = order + keep
        modes[area] = CatOrderLogic.OWN
        prefs(c).edit().putInt("m_$area", CatOrderLogic.OWN).putString("o_$area", CatOrderLogic.encode(own[area].orEmpty())).apply()
        version++
    }
}
