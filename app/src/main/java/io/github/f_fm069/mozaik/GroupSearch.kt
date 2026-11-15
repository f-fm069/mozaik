package io.github.f_fm069.mozaik

/**
 * Textsuche über Kategorienamen im Filter-Editor (reine Logik, ohne Android).
 *
 * Groß-/Kleinschreibung und Trennzeichen zählen nicht, „EU | DE“ findet also auch „EU|DE“, „eu - de“ oder
 * „[EU] DE: Cinema“. Die Wörter der Suche müssen in dieser Reihenfolge vorkommen und jeweils ein Wort im Namen
 * beginnen. Innere Wörter müssen ganz passen; das letzte darf der Anfang eines längeren Worts sein („EU | Spo“
 * findet „EU | Sports“), außer bei Kürzeln bis zwei Buchstaben („DE“, „EN“), damit „EU | DE“ nicht „EU | DEPORTES“ findet.
 * Übernommen aus IPVIO Stream (GPL-3.0).
 */
object GroupSearch {
    /** Kleinbuchstaben, jede Folge von Nicht-Buchstaben/Nicht-Ziffern wird ein Leerzeichen, ohne Rand-Leerzeichen. */
    fun normalize(s: String): String {
        val b = StringBuilder(s.length)
        var sep = true
        for (ch in s) {
            if (ch.isLetterOrDigit()) { b.append(ch.lowercaseChar()); sep = false }
            else if (!sep) { b.append(' '); sep = true }
        }
        if (b.isNotEmpty() && b[b.length - 1] == ' ') b.setLength(b.length - 1)
        return b.toString()
    }

    /** Prüffunktion für [query], oder null bei leerer Suche (keine Suche aktiv). */
    fun matcher(query: String): ((String) -> Boolean)? {
        val q = normalize(query)
        if (q.isEmpty()) return null
        val last = q.substringAfterLast(' ')
        val needle = if (last.length <= 2) " $q " else " $q"
        return { name -> (" " + normalize(name) + " ").contains(needle) }
    }

    /** Einträge aus [list], deren Name zu [query] passt; die ganze Liste bei leerer Suche. */
    fun <T> hits(list: List<T>, query: String, name: (T) -> String): List<T> {
        val m = matcher(query) ?: return list
        return list.filter { m(name(it)) }
    }
}
