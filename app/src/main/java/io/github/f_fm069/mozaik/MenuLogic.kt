package io.github.f_fm069.mozaik

/**
 * Reine Regeln für das Navigationsmenü (ohne Android), damit sie getestet werden können.
 * Garantien: Jeder Menüpunkt kommt genau einmal vor, "Start" (0) und "Einstellungen" (5) können nie ausgeblendet werden,
 * "Einstellungen" steht immer ganz unten. EPG, Favoriten und Meine Liste sind Reiter von "Verlauf" und nie im Menü.
 */
object MenuLogic {
    const val COUNT = 11
    const val HOME = 0
    const val SEARCH = 4
    const val SETTINGS = 5
    /** TV-Guide, Favoriten (Sender), Meine Liste (Filme/Serien). Seit Release 1.2 Reiter in „Verlauf“. */
    const val EPG = 6
    const val FAVS = 7
    const val MYLIST = 8
    /** Runde 15 (Nachtrag 17): Verlauf – zuletzt Angesehenes, Filme und Serien getrennt. */
    const val VERLAUF = 9
    /** Runde 20: Sport – laufende und kommende Sport-Sendungen, Sportsender. */
    const val SPORT = 10

    /** Version der gespeicherten Menü-Einstellung (menu_ver). 3 = neues Menü von Release 1.2. */
    const val MENU_VERSION = 3
    /** Release 1.2: keine eigenen Menüpunkte mehr, sondern Reiter in „Verlauf“. */
    val SUB_TABS = listOf(EPG, FAVS, MYLIST)
    /** Alle Nummern, die in der Seitenleiste und unter Einstellungen > Menü vorkommen dürfen. */
    val MENU_IDS = (0 until COUNT).filter { it !in SUB_TABS }
    /** Release 1.2: Home, Live TV, Filme, Serien, Sport, Verlauf, (Suche), Einstellungen ganz unten. */
    val DEFAULT_ORDER = listOf(HOME, 3, 1, 2, SPORT, VERLAUF, SEARCH, SETTINGS)
    /**
     * Suche steht oben im Kopf, nicht in der Seitenleiste: standardmäßig ausgeblendet.
     * Lässt sich unter Einstellungen > Menü wieder einblenden.
     */
    const val DEFAULT_HIDDEN = "4"

    /** Welcher Menüpunkt der Seitenleiste markiert ist: die Reiter von „Verlauf“ zählen zu „Verlauf“. */
    fun sidebarId(tab: Int): Int = if (tab in SUB_TABS) VERLAUF else tab

    /** Einstellungen steht immer ganz am Ende; alle anderen Punkte kommen genau einmal vor. */
    fun parseOrder(raw: String?): List<Int> {
        val order = (raw ?: DEFAULT_ORDER.joinToString(",")).split(",").mapNotNull { it.trim().toIntOrNull() }
            .filter { it in MENU_IDS && it != SETTINGS }.distinct().toMutableList()
        // Fehlende Punkte hinten anfügen, aber vor „Einstellungen“
        for (id in MENU_IDS) if (id != SETTINGS && id !in order) order.add(id)
        order.add(SETTINGS)
        return order
    }

    fun parseHidden(raw: String?): List<Int> =
        (raw ?: "").split(",").mapNotNull { it.trim().toIntOrNull() }
            .filter { it in MENU_IDS && it != HOME && it != SETTINGS }.distinct()

    fun move(order: List<Int>, id: Int, delta: Int): List<Int> {
        if (id == SETTINGS) return order
        val i = order.indexOf(id)
        // „Einstellungen“ bleibt ganz unten: nichts lässt sich dahinter schieben
        val last = if (order.lastOrNull() == SETTINGS) order.size - 2 else order.size - 1
        val j = i + delta
        if (i < 0 || j < 0 || j > last) return order
        val m = order.toMutableList()
        m.removeAt(i); m.add(j, id)
        return m
    }

    fun toggle(hidden: List<Int>, id: Int): List<Int> {
        if (id == HOME || id == SETTINGS || id !in MENU_IDS) return hidden
        return if (id in hidden) hidden - id else hidden + id
    }

    fun visible(order: List<Int>, hidden: List<Int>): List<Int> = order.filter { it !in hidden }
}
