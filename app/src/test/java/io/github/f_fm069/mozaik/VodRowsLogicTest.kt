package io.github.f_fm069.mozaik

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Kategorienansicht von Filme und Serien: wann sie aufgeht und wie viele Kategorien aufgebaut werden. */
class VodRowsLogicTest {
    @Test fun nur_filme_und_serien_mit_eingeschalteter_einstellung() {
        assertEquals(1, VodRowsLogic.areaFor(1, true))
        assertEquals(2, VodRowsLogic.areaFor(2, true))
        assertEquals(-1, VodRowsLogic.areaFor(0, true))
        assertEquals(-1, VodRowsLogic.areaFor(3, true))
        assertEquals(-1, VodRowsLogic.areaFor(MenuLogic.SETTINGS, true))
        assertEquals(-1, VodRowsLogic.areaFor(1, false))
        assertEquals(-1, VodRowsLogic.areaFor(2, false))
    }

    @Test fun anfangs_hoechstens_ein_schritt() {
        assertEquals(0, VodRowsLogic.initial(0))
        assertEquals(7, VodRowsLogic.initial(7))
        assertEquals(VodRowsLogic.STEP, VodRowsLogic.initial(800))
    }

    @Test fun nachladen_erst_kurz_vor_dem_ende() {
        val step = VodRowsLogic.STEP
        // 15 Kategorien aufgebaut, dazu Kopfzeile und Weiterschauen: 17 Einträge
        assertEquals(step, VodRowsLogic.nextShown(step, 500, 5, 17))
        assertEquals(step * 2, VodRowsLogic.nextShown(step, 500, 17 - 1 - VodRowsLogic.AHEAD, 17))
        assertEquals(step * 2, VodRowsLogic.nextShown(step, 500, 16, 17))
    }

    @Test fun nie_mehr_als_alle_kategorien() {
        assertEquals(20, VodRowsLogic.nextShown(15, 20, 16, 17))
        assertEquals(20, VodRowsLogic.nextShown(20, 20, 21, 22))
        assertEquals(20, VodRowsLogic.nextShown(40, 20, 21, 22))
    }

    @Test fun fokus_nach_dem_ausblenden() {
        val l = listOf("A", "B", "C")
        assertEquals("B", VodRowsLogic.focusAfterHide(l, "A"))
        assertEquals("C", VodRowsLogic.focusAfterHide(l, "B"))
        assertEquals("B", VodRowsLogic.focusAfterHide(l, "C"))
        assertEquals(null, VodRowsLogic.focusAfterHide(listOf("A"), "A"))
        assertEquals(null, VodRowsLogic.focusAfterHide(l, "X"))
    }

    @Test fun ohne_sichtbare_eintraege_nichts_aendern() {
        assertEquals(15, VodRowsLogic.nextShown(15, 500, -1, 0))
    }

    @Test fun texte_in_allen_sprachen() {
        I18nCore.load(DictA, DictB, DictC, DictD, DictE, DictF, DictG, DictH, DictI, DictJ, DictK, DictL)
        val nl = I18nCore.CODES.indexOf("nl")
        I18nCore.loadColumn(nl, DictNl1, DictNl2, DictNl3, DictNl4, DictNl5, DictNl6, DictNl7)
        val title = "Filme und Serien direkt zu den Kategorien"
        assertEquals("Movies and series straight to categories", I18nCore.translate(title, 1))
        assertEquals("Films en series direct naar de categorieën", I18nCore.translate(title, nl))
        for (li in 1 until I18nCore.CODES.size) {
            val code = I18nCore.CODES[li]
            assertNotEquals("fehlt: $code", title, I18nCore.translate(title, li))
            val hid = I18nCore.translate("Ausgeblendete Kategorien (3)", li)
            assertTrue("Zahl fehlt: $code", hid.contains("3"))
            assertNotEquals("fehlt: $code", "Ausgeblendete Kategorien (3)", hid)
        }
        assertEquals("Hidden categories (12)", I18nCore.translate("Ausgeblendete Kategorien (12)", 1))
    }
}
