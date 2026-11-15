package io.github.f_fm069.mozaik

/**
 * Reihenfolge der Anbieter-Kategorien (Live TV, Filme, Serien): Anbieter-Reihenfolge, A–Z oder eine eigene
 * Reihenfolge, die durch Verschieben entsteht. Ohne Android, testbar. Gespeichert von [CatOrder].
 * Übernommen aus IPVIO Stream (GPL-3.0).
 */
object CatOrderLogic {
    const val PROVIDER = 0
    const val AZ = 1
    const val OWN = 2

    private val az = Comparator<String> { a, b -> a.lowercase().compareTo(b.lowercase()) }

    /**
     * [names] in der Reihenfolge des Anbieters. OWN: Kategorien aus [own] zuerst (in dieser Reihenfolge), noch nicht
     * eingeordnete Kategorien (neu beim Anbieter) behalten dahinter ihre Anbieter-Reihenfolge.
     */
    fun apply(names: List<String>, mode: Int, own: List<String>): List<String> = when (mode) {
        AZ -> names.sortedWith(az)
        OWN -> {
            val present = names.toHashSet()
            val placed = own.filter { it in present }.distinct()
            val placedSet = placed.toHashSet()
            placed + names.filter { it !in placedSet }
        }
        else -> names
    }

    /** [shown] = Reihenfolge auf dem Bildschirm. Verschiebt [name] um [delta] Plätze (negativ = nach oben). */
    fun move(shown: List<String>, name: String, delta: Int): List<String> {
        val i = shown.indexOf(name)
        if (i < 0 || delta == 0) return shown
        val j = (i + delta).coerceIn(0, shown.lastIndex)
        if (j == i) return shown
        val out = shown.toMutableList()
        out.removeAt(i)
        out.add(j, name)
        return out
    }

    /** Verschiebt [name] an den ersten Platz. */
    fun toTop(shown: List<String>, name: String): List<String> = move(shown, name, -shown.size)

    /** Speicherform: ein Name pro Zeile (Kategorienamen enthalten nie einen Zeilenumbruch). */
    fun encode(list: List<String>): String = list.joinToString("\n")

    fun decode(s: String?): List<String> = s.orEmpty().split('\n').filter { it.isNotBlank() }
}
