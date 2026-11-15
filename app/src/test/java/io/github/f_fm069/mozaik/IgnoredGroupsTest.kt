package io.github.f_fm069.mozaik

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Erkennung der Anbieter-Kennung, nach der „Gruppen ignorieren“ bündelt, und Aufbau der Schlüssel. */
class IgnoredGroupsTest {
    @Test fun kennungAmAnfang() {
        assertEquals("TR", IgnoredGroups.tagIn("TR | Haber"))
        assertEquals("EN", IgnoredGroups.tagIn("[EN] Movies"))
        assertEquals("DE", IgnoredGroups.tagIn("DE: Kino"))
        assertEquals("AR", IgnoredGroups.tagIn("AR - Sport"))
    }

    @Test fun kennungAmEnde() {
        assertEquals("EN", IgnoredGroups.tagIn("Movies [EN]"))
        assertEquals("FR", IgnoredGroups.tagIn("Cinéma (FR)"))
    }

    @Test fun ohneKennung() {
        assertNull(IgnoredGroups.tagIn("Dokumentationen"))
        assertNull(IgnoredGroups.tagIn(""))
    }

    @Test fun schluessel() {
        assertEquals("g:MOVIE|Action", IgnoredGroups.groupKey(Kind.MOVIE, "Action"))
        assertEquals("t:LIVE|TR", IgnoredGroups.bundleKey(Kind.LIVE, "TR"))
    }
}
