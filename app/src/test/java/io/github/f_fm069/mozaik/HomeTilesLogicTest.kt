package io.github.f_fm069.mozaik

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Startseiten-Kacheln: Bereichsfarben und Lesbarkeit des Symbols auf seinem Kreis. */
class HomeTilesLogicTest {

    @Test fun farbenDerBereiche() {
        val t = HomeTilesLogic.ALL
        assertEquals(listOf(0xF2CF4D, 0x4F9BFF, 0xA98BFF, 0xFF8FB1, 0x4FD1B5), t.map { it.ring })
        // Symbol in derselben Farbe wie der Kreis
        assertEquals(t.map { it.ring }, t.map { it.symbol })
        assertEquals(listOf(3, 1, 2, MenuLogic.FAVS, MenuLogic.MYLIST), t.map { it.icon })
    }

    @Test fun symboleAufIhremKreisGutLesbar() {
        // Symbol auf dem Kreis (Bereichsfarbe 16 % über der Kachelfläche) mindestens 4,5:1, auf jedem Hintergrund
        for (t in HomeTilesLogic.ALL) for (b in Look.BGS) {
            val circle = SettingsHubLogic.circleRgb(t.ring, b.surf)
            assertTrue("Symbol ${t.icon} auf ${b.name}", ThemeMath.contrast(t.symbol, circle) >= 4.5)
        }
    }
}
