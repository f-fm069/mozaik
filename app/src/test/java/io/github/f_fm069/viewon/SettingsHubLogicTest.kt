package io.github.f_fm069.viewon

import io.github.f_fm069.viewon.SettingsHubLogic.Dir
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Einstellungs-Startseite: feste Seitennummern, Gruppen und Pfeiltasten. */
class SettingsHubLogicTest {

    private fun titles(pages: List<Int>) = pages.map { CATS[it].title }

    @Test fun jedeSeiteGenauEinmal() {
        assertEquals(16, CATS.size)
        assertEquals((0 until CATS.size).toList(), SettingsHubLogic.ORDER.sorted())
    }

    @Test fun festeNummernBleiben() {
        // Direktsprünge hängen an diesen Nummern: Profil-Kreis (10) und „Genres & Kategorien“ → Playlists (9)
        assertEquals("Profile", CATS[SettingsNav.PROFILES].title)
        assertEquals("Playlists", CATS[9].title)
        assertEquals("Allgemein", CATS[0].title)
        assertEquals("Über Viewon", CATS[15].title)
    }

    @Test fun gruppenUndReihenfolge() {
        val g = SettingsHubLogic.GROUPS
        assertEquals(listOf("Darstellung", "Wiedergabe", "Inhalte", "Konto & System"), g.map { it.title })
        assertEquals(listOf("Allgemein", "Sprache & Region", "Ansicht", "Design", "Menü"), titles(g[0].pages))
        assertEquals(listOf("Player", "Ton & Untertitel", "Live-TV & Guide"), titles(g[1].pages))
        assertEquals(listOf("Genres & Kategorien", "Playlists", "API-Schlüssel"), titles(g[2].pages))
        assertEquals(listOf("Profile", "Jugendschutz", "Speicher", "Diagnose", "Über Viewon"), titles(g[3].pages))
        assertEquals(listOf(0xE0B05A, 0x3D8BFF, 0xA98BFF, 0xFF8FB1), g.map { it.color })
        // Nur das Symbol von „Wiedergabe“ ist heller, Kreis und Überschrift behalten #3D8BFF
        assertEquals(listOf(0xE0B05A, 0x7DB8FF, 0xA98BFF, 0xFF8FB1), g.map { it.iconColor })
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
        // Beispiel über eine Gruppengrenze: von „Menü“ (zweite Reihe, Spalte 2) runter zu „Ton & Untertitel“
        assertEquals(6, SettingsHubLogic.move(4, Dir.DOWN))
        // Aus der dritten Spalte runter in eine kurze Reihe: „Ansicht“ → „Menü“
        assertEquals(4, SettingsHubLogic.move(2, Dir.DOWN))
    }

    @Test fun linksAusErsterSpalteGehtInDieSeitenleiste() {
        for (row in SettingsHubLogic.ROWS) assertNull(SettingsHubLogic.move(row.first(), Dir.LEFT))
        assertEquals(0, SettingsHubLogic.move(1, Dir.LEFT))
    }

    @Test fun ueberschriftenGutLesbar() {
        // Gruppenfarbe als Schrift auf jedem Hintergrund mindestens 4,5:1
        for (g in SettingsHubLogic.GROUPS) for (b in Look.BGS) {
            assertTrue("${g.title} auf ${b.name}", ThemeMath.contrast(g.color, b.bg) >= 4.5)
        }
    }

    @Test fun symboleAufIhremKreisGutLesbar() {
        // Symbol auf dem Kreis (Gruppenfarbe 16 % über der Kachelfläche) mindestens 4,5:1, auf jedem Hintergrund
        for (g in SettingsHubLogic.GROUPS) for (b in Look.BGS) {
            val circle = SettingsHubLogic.circleRgb(g.color, b.surf)
            assertTrue("${g.title} auf ${b.name}", ThemeMath.contrast(g.iconColor, circle) >= 4.5)
        }
    }
}
