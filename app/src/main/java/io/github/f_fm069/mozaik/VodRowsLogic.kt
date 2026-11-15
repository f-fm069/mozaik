package io.github.f_fm069.mozaik

/**
 * Regeln der Kategorienansicht von Filme und Serien (Posterreihen mit „Weiterschauen“ oben), ohne Android-Teile,
 * damit sie sich testen lassen. Die Idee „Filme und Serien direkt zu den Kategorien“ stammt aus IPVIO Stream
 * (W-002, GPL-3.0); die Reihenansicht ist eine Mozaik-Erweiterung.
 */
object VodRowsLogic {
    /** Kategorien, die beim Öffnen aufgebaut werden; beim Weiterscrollen kommen jeweils so viele dazu. */
    const val STEP = 15
    /** So viele Einträge vor dem Ende der Liste wird nachgeladen (bevor man unten ankommt). */
    const val AHEAD = 4

    /** Bereich, dessen Kategorienansicht ein Menüpunkt öffnet: 1 = Filme, 2 = Serien, -1 = keiner (Einstellung aus oder anderer Tab). */
    fun areaFor(tab: Int, enabled: Boolean): Int = if (enabled && (tab == 1 || tab == 2)) tab else -1

    /**
     * Neue Anzahl aufgebauter Kategorien. [shown] ist die bisherige Anzahl, [total] die Zahl aller Kategorien,
     * [lastVisible] der Index des untersten sichtbaren Eintrags und [itemCount] die Zahl aller Einträge der Liste
     * (Kopfzeile, „Weiterschauen“, Kategorien und Abschluss). Liegt der unterste sichtbare Eintrag nahe am Ende,
     * kommen [STEP] Kategorien dazu, nie mehr als [total].
     */
    fun nextShown(shown: Int, total: Int, lastVisible: Int, itemCount: Int): Int {
        val cur = shown.coerceIn(0, total)
        if (cur >= total || lastVisible < 0) return cur
        return if (lastVisible >= itemCount - 1 - AHEAD) minOf(total, cur + STEP) else cur
    }

    /** Reihe, die nach dem Ausblenden von [hidden] den Fokus bekommt: die nächste, am Ende die vorige, sonst keine. */
    fun focusAfterHide(names: List<String>, hidden: String): String? {
        val i = names.indexOf(hidden)
        if (i < 0) return null
        return names.getOrNull(i + 1) ?: names.getOrNull(i - 1)
    }

    /** Anzahl der Kategorien beim ersten Aufbau. */
    fun initial(total: Int): Int = minOf(total, STEP)
}
