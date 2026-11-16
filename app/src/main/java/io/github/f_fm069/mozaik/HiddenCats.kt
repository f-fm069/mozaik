package io.github.f_fm069.mozaik

import android.content.Context

/**
 * Ausgeblendete Live-TV-Kategorien (je Profil). Sie verschwinden überall (Kategorienliste, Player, Alle Sender, Suche)
 * und lassen sich über „Ausgeblendet“ oder in den Einstellungen zurückholen. Übernommen aus IPVIO Stream (GPL-3.0).
 *
 * Keine eigene Speicherung mehr: Alles liegt im gemeinsamen Speicher von [IgnoredGroups] als "g:LIVE|Kategorie".
 * Die frühere eigene Datei wird dort beim Einlesen übernommen (siehe [IgnoredGroups.init]).
 */
object HiddenCats {
    @Volatile private var cacheVer = -1
    @Volatile private var cache: List<String> = emptyList()

    /** Ausgeblendete Live-Kategorien, alphabetisch. Liest [IgnoredGroups.version], wird also bei Änderungen neu gezeichnet. */
    val list: List<String>
        get() {
            val v = IgnoredGroups.version
            if (v != cacheVer) { cache = IgnoredGroups.hiddenGroups(Kind.LIVE); cacheVer = v }
            return cache
        }

    fun init(c: Context) = IgnoredGroups.init(c)

    fun hide(c: Context, name: String) = IgnoredGroups.set(c, IgnoredGroups.groupKey(Kind.LIVE, name), true)
    fun show(c: Context, name: String) = IgnoredGroups.set(c, IgnoredGroups.groupKey(Kind.LIVE, name), false)
    fun showAll(c: Context) = IgnoredGroups.setMany(c, list.map { IgnoredGroups.groupKey(Kind.LIVE, it) }, false)
}
