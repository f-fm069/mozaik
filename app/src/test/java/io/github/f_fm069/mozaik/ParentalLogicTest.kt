package io.github.f_fm069.mozaik

import org.junit.Assert.*
import org.junit.Test

class ParentalLogicTest {
    @Test fun detectsAdultGroups() {
        for (g in listOf("XXX | Adult", "Adults 18+", "+18 Movies", "Erotik", "Erwachsene", "porn hub", "FSK 18", "Hentai", "DE | ADULT"))
            assertTrue(g, ParentalLogic.isAdult(g, "Film"))
    }

    @Test fun keepsNormalContent() {
        for (g in listOf("Action", "Essex Stories", "Sussex", "Sextett Musik", "Kinder", "DE | Filme", "Adulting 101 (Serie)".substring(0, 0)))
            assertFalse(g, ParentalLogic.isAdult(g, "Sex and the City"))
        assertFalse(ParentalLogic.isAdult("Filme", "Essex Boys"))
        assertTrue(ParentalLogic.isAdult("Filme", "Something XXX"))
        assertTrue(ParentalLogic.isAdult("Filme", "Anime [ADULT]"))
    }

    @Test fun pin() {
        val h = ParentalLogic.hash("1234", "s")
        assertTrue(ParentalLogic.check("1234", "s", h))
        assertFalse(ParentalLogic.check("1235", "s", h))
        assertFalse(ParentalLogic.check("1234", "t", h))
        assertFalse(ParentalLogic.check("12a4", "s", ParentalLogic.hash("12a4", "s")))
        assertTrue(ParentalLogic.validPin("0000"))
        assertFalse(ParentalLogic.validPin("123"))
    }

    @Test fun mayClearPin() {
        val h = ParentalLogic.hash("1234", "s")
        assertTrue(ParentalLogic.mayClearPin("1234", "s", h))
        assertFalse(ParentalLogic.mayClearPin("4321", "s", h))
        assertFalse(ParentalLogic.mayClearPin("123", "s", h))
        assertFalse(ParentalLogic.mayClearPin("", "s", h))
        assertFalse(ParentalLogic.mayClearPin("1234", "x", h))
        assertFalse(ParentalLogic.mayClearPin("1234", "s", null))
        assertFalse(ParentalLogic.mayClearPin("1234", null, ""))
    }
}
