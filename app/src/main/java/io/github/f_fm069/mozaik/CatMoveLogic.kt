package io.github.f_fm069.mozaik

/**
 * Verschieben einer Kategorie in der Liste (Live TV: „Mit Hoch/Runter verschieben“, „Ans Ende verschieben“).
 * Ohne Android, testbar. Gespeichert wird weiterhin über [CatOrder] / [CatOrderLogic].
 */
object CatMoveLogic {
    /** Ein Platz nach oben (delta = -1) oder unten (delta = 1). Am Rand und bei unbekanntem Namen bleibt die Liste gleich. */
    fun step(list: List<String>, name: String, delta: Int): List<String> = CatOrderLogic.move(list, name, delta)

    /** Verschiebt [name] an den letzten Platz. */
    fun toEnd(list: List<String>, name: String): List<String> = CatOrderLogic.move(list, name, list.size)

    /** Verschiebt [name] an den ersten Platz. */
    fun toStart(list: List<String>, name: String): List<String> = CatOrderLogic.toTop(list, name)

    /** Gibt es in Richtung [delta] noch Platz? (false am Rand oder bei unbekanntem Namen) */
    fun canStep(list: List<String>, name: String, delta: Int): Boolean {
        val i = list.indexOf(name)
        return i >= 0 && delta != 0 && (i + delta) in list.indices
    }

    /**
     * [full] = ganze Reihenfolge (mit Favoriten und ausgeblendeten), [oldShown] = die gezeigten Kategorien darin,
     * [newShown] = dieselben Kategorien in neuer Reihenfolge. Die gezeigten Kategorien tauschen nur ihre Plätze
     * untereinander, alle anderen behalten ihren Platz. Passen die Mengen nicht zusammen, bleibt [full] unverändert.
     */
    fun merge(full: List<String>, oldShown: List<String>, newShown: List<String>): List<String> {
        val set = oldShown.toHashSet()
        if (newShown.size != oldShown.size || newShown.toHashSet() != set) return full
        val it = newShown.iterator()
        return full.map { n -> if (n in set && it.hasNext()) it.next() else n }
    }
}
