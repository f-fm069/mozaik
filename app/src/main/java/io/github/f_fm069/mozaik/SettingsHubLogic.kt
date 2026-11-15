package io.github.f_fm069.mozaik

/**
 * Kacheln, Farben und Pfeiltasten-Navigation der Einstellungs-Startseite (ohne Android, durch Unit-Tests abgedeckt).
 *
 * Wie im Original IPVIO Stream: eine einzige Liste in [COLUMNS] Spalten, ohne Gruppenüberschriften, in der Reihenfolge
 * von CATS (SettingsHub.kt). Die Position ist zugleich die feste Seitennummer (Öffnen, Überschrift der Unterseite,
 * Direktsprünge wie SettingsNav.PROFILES). Die Kacheln sind dunkel; nur der runde Symbolkreis trägt eine Farbe,
 * die wie im Original der Position folgt (Regenbogen, siehe [circleColor]).
 */
object SettingsHubLogic {
    const val COLUMNS = 3

    /** Anzahl der Kacheln (= Größe von CATS, durch einen Test abgesichert). */
    const val PAGE_COUNT = 16

    /** Dunkle Farbe des Symbols im farbigen Kreis (wie DS.FocusInk). */
    const val ICON_INK = 0x0B0F19

    /** Deckkraft des runden Symbolfelds der Startseiten-Kacheln (HomeParts) in der Akzentfarbe. */
    const val CIRCLE_ALPHA = 0.16f

    /**
     * Farbe des Symbolkreises der Kachel an Position [index] von [count]: Farbton gleichmäßig von 0° (rot) bis 330° (rosa).
     * Der Kreis ist hell genug, dass das dunkle Symbol [ICON_INK] darauf mindestens 3 : 1 Kontrast hat
     * (bei zu dunklen Tönen, etwa Blau, wird er heller und etwas weniger gesättigt).
     */
    fun circleColor(index: Int, count: Int = PAGE_COUNT): Int {
        val hue = if (count <= 1) 0f else index * 330f / (count - 1)
        var s = 0.62f
        var c = ThemeMath.hsv(hue, s, 0.96f)
        while (ThemeMath.contrast(ICON_INK, c) < 3.0 && s > 0f) { s = (s - 0.02f).coerceAtLeast(0f); c = ThemeMath.hsv(hue, s, 0.96f) }
        return c
    }

    /** Farbe des Kreises: Akzentfarbe [color] mit [CIRCLE_ALPHA] über der Kachelfläche [surf] (für die Kontrastprüfung). */
    fun circleRgb(color: Int, surf: Int): Int {
        fun ch(shift: Int): Int {
            val a = (surf shr shift) and 0xFF
            val b = (color shr shift) and 0xFF
            return Math.round(a * (1 - CIRCLE_ALPHA) + b * CIRCLE_ALPHA) shl shift
        }
        return ch(16) or ch(8) or ch(0)
    }

    /** Alle Kachel-Reihen von oben nach unten (je [COLUMNS] Kacheln, die letzte Reihe darf kürzer sein). */
    val ROWS: List<List<Int>> = (0 until PAGE_COUNT).toList().chunked(COLUMNS)

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
