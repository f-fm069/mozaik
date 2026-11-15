package io.github.f_fm069.mozaik

import io.github.f_fm069.mozaik.SettingsHubLogic.Dir
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Einstellungs-Startseite: eine Liste in 3 Spalten wie im Original, feste Seitennummern, Farben und Pfeiltasten. */
class SettingsHubLogicTest {

    @Test fun jedeSeiteGenauEinmalInDerBisherigenReihenfolge() {
        assertEquals(SettingsHubLogic.PAGE_COUNT, CATS.size)
        // Anzeige-Reihenfolge = Reihenfolge in CATS (keine Gruppen mehr)
        assertEquals((0 until CATS.size).toList(), SettingsHubLogic.ORDER)
        assertTrue(SettingsHubLogic.ROWS.all { it.size <= SettingsHubLogic.COLUMNS })
        assertEquals(6, SettingsHubLogic.ROWS.size)
    }

    @Test fun festeNummernBleiben() {
        // Direktsprünge hängen an diesen Nummern: Profil-Kreis (10) und „Genres & Kategorien“ → Playlists (9)
        assertEquals("Profile", CATS[SettingsNav.PROFILES].title)
        assertEquals("Playlists", CATS[9].title)
        assertEquals("Allgemein", CATS[0].title)
        assertEquals("Genres & Kategorien", CATS[7].title)
        assertEquals("Diagnose", CATS[14].title)
        assertEquals("Über Mozaik", CATS[15].title)
        // Eine Kachel „Spende“ gibt es in Mozaik nicht
        assertTrue(CATS.none { it.title.contains("Spende") })
    }

    @Test fun rechtsLaeuftDurchAlleKacheln() {
        var p = SettingsHubLogic.ORDER.first()
        val seen = mutableListOf(p)
        while (true) {
            val n = SettingsHubLogic.move(p, Dir.RIGHT)!!
            if (n == p) break
            seen += n; p = n
        }
        assertEquals(SettingsHubLogic.ORDER, seen)
        // Auf der letzten Kachel bleibt der Fokus stehen (springt nicht zurück nach oben)
        assertEquals(15, SettingsHubLogic.move(15, Dir.RIGHT))
    }

    @Test fun runterUndHochUeberspringenKeineReihe() {
        val rows = SettingsHubLogic.ROWS
        for ((r, row) in rows.withIndex()) for ((c, p) in row.withIndex()) {
            val down = SettingsHubLogic.move(p, Dir.DOWN)
            if (r == rows.lastIndex) assertEquals(p, down) else {
                assertEquals(rows[r + 1][minOf(c, rows[r + 1].lastIndex)], down)
            }
            val up = SettingsHubLogic.move(p, Dir.UP)
            if (r == 0) assertNull(up) else assertEquals(rows[r - 1][minOf(c, rows[r - 1].lastIndex)], up)
        }
        // Gleiche Spalte eine Reihe tiefer: „Allgemein“ (0) → „Design“ (3)
        assertEquals(3, SettingsHubLogic.move(0, Dir.DOWN))
        // Letzte Reihe ist [15]: von 12, 13, 14 geht es runter zu 15
        assertEquals(15, SettingsHubLogic.move(14, Dir.DOWN))
    }

    @Test fun linksAusErsterSpalteGehtInDieSeitenleiste() {
        for (row in SettingsHubLogic.ROWS) assertNull(SettingsHubLogic.move(row.first(), Dir.LEFT))
        assertEquals(0, SettingsHubLogic.move(1, Dir.LEFT))
    }

    @Test fun regenbogenNachPosition() {
        val n = CATS.size
        // Farbton läuft gleichmäßig von Rot (0°) bis Rosa (330°)
        assertEquals(0f, ThemeMath.rgbToHsv(SettingsHubLogic.circleColor(0, n))[0], 2f)
        assertEquals(330f, ThemeMath.rgbToHsv(SettingsHubLogic.circleColor(n - 1, n))[0], 2f)
        // Alle Farben verschieden
        assertEquals(n, (0 until n).map { SettingsHubLogic.circleColor(it, n) }.toSet().size)
    }

    @Test fun dunklesSymbolAufFarbigemKreisGutLesbar() {
        // Dunkles Symbol auf dem voll farbigen Kreis mindestens 3:1 (Symbol, kein Text)
        for (i in CATS.indices) {
            val c = SettingsHubLogic.circleColor(i, CATS.size)
            assertTrue("Kachel $i " + ThemeMath.hexOf(c), ThemeMath.contrast(SettingsHubLogic.ICON_INK, c) >= 3.0)
        }
    }

    @Test fun fokusRahmenUndUntertitelGutLesbar() {
        // Fokus-Rahmen in Indigo hebt sich von der Kachel ab (mindestens 3:1 für Rahmen)
        for (b in Look.BGS) assertTrue(b.name, ThemeMath.contrast(Look.BRAND_ACCENT_HI, b.surf) >= 3.0)
        // Grauer Untertitel (Tok.TextLow #A0A6B8) auf der Kachelfläche mindestens 4,5:1
        for (b in Look.BGS) assertTrue(b.name, ThemeMath.contrast(0xA0A6B8, b.surf) >= 4.5)
    }
}
