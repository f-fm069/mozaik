package io.github.f_fm069.mozaik

/**
 * Schnellzugriff-Kacheln auf der Startseite (ohne Android, durch Unit-Tests abgedeckt).
 * Die Kacheln sehen aus wie die Einstellungs-Kacheln: dunkle Fläche, Symbol und Fokus-Rahmen in der Akzentfarbe
 * (Kreis mit [SettingsHubLogic.CIRCLE_ALPHA] Deckkraft, Symbol voll). Bereiche haben keine eigene Farbe.
 */
object HomeTilesLogic {
    /** Bereich: Symbol-Nummer (MenuIcon). */
    class TileColor(val icon: Int)

    val LIVE = TileColor(3)
    val MOVIES = TileColor(1)
    val SERIES = TileColor(2)
    val FAVS = TileColor(MenuLogic.FAVS)
    val MYLIST = TileColor(MenuLogic.MYLIST)

    val ALL = listOf(LIVE, MOVIES, SERIES, FAVS, MYLIST)
}
