package io.github.f_fm069.mozaik

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Ozean als Standard-Farbthema für neue Installationen (look_ver 8). */
class OceanDefaultTest {
    @Test fun standardIstOzean() {
        assertEquals("Ozean", Look.ACCENTS[Look.DEFAULT_ACCENT].name)
        assertEquals(Look.OCEAN_ACCENT, Look.DEFAULT_ACCENT)
        // Das bisherige Standard-Thema „Mozaik“ (Indigo) bleibt an Stelle 0 erhalten
        assertEquals("Mozaik", Look.ACCENTS[Look.MOZAIK_ACCENT].name)
        assertEquals(Look.BRAND_ACCENT, Look.ACCENTS[Look.MOZAIK_ACCENT].fill)
    }

    @Test fun nurNeuinstallationBekommtOzean() {
        assertTrue(Look.isFreshLook(hasLookVer = false, hasAccent = false))
        // Bestehende Installationen (look_ver oder ein gespeichertes Thema vorhanden) behalten ihr Thema
        assertFalse(Look.isFreshLook(hasLookVer = true, hasAccent = false))
        assertFalse(Look.isFreshLook(hasLookVer = false, hasAccent = true))
        assertFalse(Look.isFreshLook(hasLookVer = true, hasAccent = true))
    }

    @Test fun ozeanIstAufAllenGruendenLesbar() {
        val o = Look.ACCENTS[Look.DEFAULT_ACCENT]
        // Schrift auf der Ozean-Fläche: Weiß mit mindestens 4,5 : 1
        assertEquals(0xFFFFFF, ThemeMath.onAccent(o.fill))
        assertTrue(ThemeMath.contrast(0xFFFFFF, o.fill) >= 4.5)
        // Hervorhebung (Schrift, Symbole, Fokusrahmen) auf Grund und beiden Flächen jedes Hintergrunds
        for (b in Look.BGS) for (grund in listOf(b.bg, b.surf, b.surf2)) {
            assertTrue("Hervorhebung auf ${b.name} " + ThemeMath.hexOf(grund), ThemeMath.contrast(o.highlight, grund) >= 4.5)
        }
    }
}
