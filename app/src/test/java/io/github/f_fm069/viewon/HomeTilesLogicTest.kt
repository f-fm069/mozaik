package io.github.f_fm069.viewon

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Startseiten-Kacheln: Bereichsfarben und Lesbarkeit des Symbols auf seinem Kreis. */
class HomeTilesLogicTest {

    @Test fun farbenDerBereiche() {
        val t = HomeTilesLogic.ALL
        assertEquals(listOf(0xE0B05A, 0x3D8BFF, 0xA98BFF, 0xFF8FB1, 0x4FD1B5), t.map { it.ring })
        // Nur das Symbol von „Filme“ ist heller, der Kreis behält #3D8BFF
        assertEquals(listOf(0xE0B05A, 0x7DB8FF, 0xA98BFF, 0xFF8FB1, 0x4FD1B5), t.map { it.symbol })
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
