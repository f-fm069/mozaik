package io.github.f_fm069.mozaik

/** Eine Zeile im Kategorie-Filter: Name der Kategorie, Anzahl Sender/Titel, eingeblendet ja/nein. */
data class CatToggle(val name: String, val count: Int, val on: Boolean)

/**
 * Kategorien einzeln ein- und ausblenden (Live TV, Filme, Serien), ohne Android, testbar.
 *
 * Es gibt nur einen Speicher: den von [IgnoredGroups] mit Schlüsseln "g:ART|Kategorie". Live TV, Player, Filme,
 * Serien und die Einstellungen lesen und schreiben alle dort. Der Sprachfilter wirkt vorher: Kategorien ausgeblendeter
 * Sprachen werden gar nicht geladen und tauchen deshalb hier nicht auf.
 */
object CatFilterLogic {
    fun key(kind: Kind, name: String): String = "g:" + kind.name + "|" + name

    private fun prefix(kind: Kind) = "g:" + kind.name + "|"

    /** Ausgeblendete Kategorien einer Art, alphabetisch. */
    fun hidden(keys: Collection<String>, kind: Kind): List<String> {
        val p = prefix(kind)
        return keys.filter { it.startsWith(p) }.map { it.substring(p.length) }.sortedBy { it.lowercase() }
    }

    /** Alte Liste ausgeblendeter Live-Kategorien (früher eigene Datei, je Zeile ein Name). */
    fun decodeLegacy(s: String?): List<String> =
        if (s.isNullOrBlank()) emptyList() else s.split('\n').filter { it.isNotBlank() }

    /**
     * Migration: alte ausgeblendete Live-Kategorien als "g:LIVE|Name" zu [keys] hinzufügen. Nichts geht verloren,
     * vorhandene Schlüssel bleiben; mehrfaches Ausführen ändert nichts mehr.
     */
    fun migrate(legacy: String?, keys: Set<String>): Set<String> {
        val old = decodeLegacy(legacy)
        if (old.isEmpty()) return keys
        return keys + old.map { key(Kind.LIVE, it) }
    }

    /** Anzahl Einträge je Kategorie einer Art, alphabetisch; Einträge ohne Kategorie zählen nicht. */
    fun counts(items: List<Item>, kind: Kind): List<Pair<String, Int>> {
        val m = HashMap<String, Int>()
        for (i in items) if (i.kind == kind && i.group.isNotBlank()) m[i.group] = (m[i.group] ?: 0) + 1
        return m.entries.sortedBy { it.key.lowercase() }.map { it.key to it.value }
    }

    /** Zeilen für die Anzeige: nur Treffer der Suche [query] (leer = alle), mit Schalterstand aus [keys]. */
    fun rows(counts: List<Pair<String, Int>>, keys: Set<String>, kind: Kind, query: String): List<CatToggle> =
        GroupSearch.hits(counts, query) { it.first }.map { (n, c) -> CatToggle(n, c, key(kind, n) !in keys) }

    /** Eine Kategorie ein- (true) oder ausblenden (false). */
    fun set(keys: Set<String>, kind: Kind, name: String, on: Boolean): Set<String> =
        if (on) keys - key(kind, name) else keys + key(kind, name)

    /** „Alle an“ / „Alle aus“ für [names] (z. B. alle Treffer der Suche); andere Arten und Kennungen bleiben unberührt. */
    fun setAll(keys: Set<String>, kind: Kind, names: Collection<String>, on: Boolean): Set<String> {
        val k = names.map { key(kind, it) }
        return if (on) keys - k.toSet() else keys + k
    }

    /** Wie viele von [names] eingeblendet sind. */
    fun onCount(keys: Set<String>, kind: Kind, names: Collection<String>): Int = names.count { key(kind, it) !in keys }
}
