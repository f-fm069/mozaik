package io.github.f_fm069.viewon

/**
 * Gruppen und Pfeiltasten-Navigation der Einstellungs-Startseite (ohne Android, durch Unit-Tests abgedeckt).
 *
 * Jede Kachel behält ihre feste Seitennummer (Position in CATS in SettingsHub.kt). Nur die Anzeige wird nach
 * Gruppen sortiert; Öffnen, Überschrift der Unterseite und Direktsprünge (z. B. SettingsNav.PROFILES) laufen
 * weiter über diese Nummer.
 */
object SettingsHubLogic {
    /** Eine Gruppe: Überschrift (deutscher Schlüssel, wird übersetzt), Farbe 0xRRGGBB und Seitennummern in Anzeige-Reihenfolge. */
    class Group(val title: String, val color: Int, val pages: List<Int>)

    const val COLUMNS = 3

    val GROUPS = listOf(
        Group("Darstellung", 0xE0B05A, listOf(0, 1, 2, 3, 4)),        // Allgemein, Sprache & Region, Ansicht, Design, Menü
        Group("Wiedergabe", 0x3D8BFF, listOf(5, 6, 8)),               // Player, Ton & Untertitel, Live-TV & Guide
        Group("Inhalte", 0xA98BFF, listOf(7, 9, 11)),                 // Genres & Kategorien, Playlists, API-Schlüssel
        Group("Konto & System", 0xFF8FB1, listOf(10, 12, 13, 14, 15)) // Profile, Jugendschutz, Speicher, Diagnose, Über Viewon
    )

    /** Alle Kachel-Reihen von oben nach unten (je Gruppe in Reihen zu je [COLUMNS]). */
    val ROWS: List<List<Int>> = GROUPS.flatMap { it.pages.chunked(COLUMNS) }

    /** Seitennummern in Anzeige-Reihenfolge. */
    val ORDER: List<Int> = ROWS.flatten()

    enum class Dir { UP, DOWN, LEFT, RIGHT }

    /**
     * Ziel der Pfeiltaste von Kachel [page] aus, oder null = die App entscheidet wie bisher
     * (links aus der ersten Spalte in die Seitenleiste, hoch aus der ersten Reihe in die Kopfzeile).
     * Ist das Ziel [page] selbst, bleibt der Fokus stehen (am Ende der Kacheln, statt zurück nach oben zu springen).
     * - Rechts: nächste Kachel; am Reihenende die erste Kachel der nächsten Reihe (auch in der nächsten Gruppe).
     * - Links: vorige Kachel in der Reihe.
     * - Runter/Hoch: gleiche Spalte in der nächsten/vorigen Reihe; ist die Reihe kürzer, deren letzte Kachel.
     * - Rechts auf der letzten Kachel und Runter in der letzten Reihe: bleibt stehen.
     * So wird keine Kachel übersprungen, auch nicht bei kurzen Reihen am Ende einer Gruppe.
     */
    fun move(page: Int, dir: Dir): Int? {
        val r = ROWS.indexOfFirst { page in it }
        if (r < 0) return null
        val row = ROWS[r]
        val c = row.indexOf(page)
        return when (dir) {
            Dir.RIGHT -> if (c < row.lastIndex) row[c + 1] else ROWS.getOrNull(r + 1)?.first() ?: page
            Dir.LEFT -> if (c > 0) row[c - 1] else null
            Dir.DOWN -> ROWS.getOrNull(r + 1)?.let { it[minOf(c, it.lastIndex)] } ?: page
            Dir.UP -> ROWS.getOrNull(r - 1)?.let { it[minOf(c, it.lastIndex)] }
        }
    }
}
