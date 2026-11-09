package io.github.f_fm069.viewon

/**
 * Farben der Schnellzugriff-Kacheln auf der Startseite (ohne Android, durch Unit-Tests abgedeckt).
 * Die Kacheln sehen aus wie die Einstellungs-Kacheln: dunkle Fläche, nur der runde Symbolkreis trägt die Bereichsfarbe
 * (Kreis mit [SettingsHubLogic.CIRCLE_ALPHA] Deckkraft, Symbol voll).
 */
object HomeTilesLogic {
    /** Bereich: Symbol-Nummer (MenuIcon), Farbe des Kreises und Farbe des Symbols (meist gleich), jeweils 0xRRGGBB. */
    class TileColor(val icon: Int, val ring: Int, val symbol: Int = ring)

    val LIVE = TileColor(3, 0xE0B05A)
    // Symbol im helleren Blau, wie bei „Wiedergabe“ in den Einstellungen
    val MOVIES = TileColor(1, 0x3D8BFF, symbol = 0x7DB8FF)
    val SERIES = TileColor(2, 0xA98BFF)
    val FAVS = TileColor(MenuLogic.FAVS, 0xFF8FB1)
    val MYLIST = TileColor(MenuLogic.MYLIST, 0x4FD1B5)

    val ALL = listOf(LIVE, MOVIES, SERIES, FAVS, MYLIST)
}
