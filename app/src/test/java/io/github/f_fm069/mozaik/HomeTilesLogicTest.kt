package io.github.f_fm069.mozaik

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Startseiten-Kacheln: Symbole und Lesbarkeit des Akzent-Symbols auf seinem Kreis. */
class HomeTilesLogicTest {

    @Test fun symboleDerBereiche() {
        assertEquals(listOf(3, 1, 2, MenuLogic.FAVS, MenuLogic.MYLIST), HomeTilesLogic.ALL.map { it.icon })
    }

    @Test fun symbolAufSeinemKreisGutLesbar() {
        // Indigo-Hervorhebung auf dem Kreis (dieselbe Farbe 16 % über der Kachelfläche) mindestens 3:1 (Symbol, kein Text), auf jedem Hintergrund
        val hi = Look.BRAND_ACCENT_HI
        for (b in Look.BGS) {
            val circle = SettingsHubLogic.circleRgb(hi, b.surf)
            assertTrue("Symbol auf ${b.name}", ThemeMath.contrast(hi, circle) >= 3.0)
        }
    }
}
