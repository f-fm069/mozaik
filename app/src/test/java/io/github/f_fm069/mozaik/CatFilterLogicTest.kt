package io.github.f_fm069.mozaik

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class CatFilterLogicTest {
    private fun item(k: Kind, g: String) = Item(g + k, k, "t", null, "u", null, g)

    @Test fun schluesselWieIgnoredGroups() {
        assertEquals(IgnoredGroups.groupKey(Kind.LIVE, "DE| NEWS"), CatFilterLogic.key(Kind.LIVE, "DE| NEWS"))
        assertEquals(IgnoredGroups.groupKey(Kind.MOVIE, "Action"), CatFilterLogic.key(Kind.MOVIE, "Action"))
    }

    @Test fun migration_uebernimmtAlteLiveKategorien() {
        val keys = setOf("g:MOVIE|Action", "t:LIVE|TR")
        val r = CatFilterLogic.migrate("DE| NEWS\nHR| HORSE RACING\n", keys)
        assertEquals(setOf("g:MOVIE|Action", "t:LIVE|TR", "g:LIVE|DE| NEWS", "g:LIVE|HR| HORSE RACING"), r)
    }

    @Test fun migration_zweimal_aendertNichts() {
        val once = CatFilterLogic.migrate("DE| NEWS", emptySet())
        assertEquals(once, CatFilterLogic.migrate("DE| NEWS", once))
    }

    @Test fun migration_leer_laesstAllesStehen() {
        val keys = setOf("g:LIVE|X")
        assertSame(keys, CatFilterLogic.migrate(null, keys))
        assertSame(keys, CatFilterLogic.migrate("", keys))
        assertSame(keys, CatFilterLogic.migrate("\n \n", keys))
    }

    @Test fun ausgeblendete_jeArt_alphabetisch() {
        val keys = setOf("g:LIVE|b", "g:LIVE|A", "g:MOVIE|C", "t:LIVE|TR")
        assertEquals(listOf("A", "b"), CatFilterLogic.hidden(keys, Kind.LIVE))
        assertEquals(listOf("C"), CatFilterLogic.hidden(keys, Kind.MOVIE))
        assertEquals(emptyList<String>(), CatFilterLogic.hidden(keys, Kind.SERIES))
    }

    @Test fun zaehlen_jeArt_ohneLeereKategorie() {
        val items = listOf(item(Kind.LIVE, "b"), item(Kind.LIVE, "A"), item(Kind.LIVE, "b"), item(Kind.MOVIE, "A"), item(Kind.LIVE, " "))
        assertEquals(listOf("A" to 1, "b" to 2), CatFilterLogic.counts(items, Kind.LIVE))
        assertEquals(listOf("A" to 1), CatFilterLogic.counts(items, Kind.MOVIE))
    }

    @Test fun zeilen_mitSucheUndSchalterstand() {
        val counts = listOf("DE| NEWS" to 3, "DE| SPORT" to 5, "EU | DE Cinema" to 2, "EU | DEPORTES" to 1)
        val keys = setOf("g:LIVE|DE| SPORT", "g:MOVIE|DE| NEWS")
        val all = CatFilterLogic.rows(counts, keys, Kind.LIVE, "")
        assertEquals(listOf(true, false, true, true), all.map { it.on })
        assertEquals(listOf(3, 5, 2, 1), all.map { it.count })
        assertEquals(listOf("EU | DE Cinema"), CatFilterLogic.rows(counts, keys, Kind.LIVE, "eu de").map { it.name })
    }

    @Test fun schalten_einzeln() {
        val off = CatFilterLogic.set(emptySet(), Kind.SERIES, "Krimi", false)
        assertEquals(setOf("g:SERIES|Krimi"), off)
        assertEquals(emptySet<String>(), CatFilterLogic.set(off, Kind.SERIES, "Krimi", true))
    }

    @Test fun alleAnUndAus_nurDieseArt() {
        val keys = setOf("g:MOVIE|A", "t:LIVE|TR")
        val off = CatFilterLogic.setAll(keys, Kind.LIVE, listOf("A", "B"), false)
        assertEquals(setOf("g:MOVIE|A", "t:LIVE|TR", "g:LIVE|A", "g:LIVE|B"), off)
        assertEquals(0, CatFilterLogic.onCount(off, Kind.LIVE, listOf("A", "B")))
        val on = CatFilterLogic.setAll(off, Kind.LIVE, listOf("A", "B"), true)
        assertEquals(keys, on)
        assertEquals(2, CatFilterLogic.onCount(on, Kind.LIVE, listOf("A", "B")))
    }

    @Test fun sprachfilterZuerst_dannEinzelschalter() {
        // Geladen ist nur, was der Sprachfilter durchlässt; Einzelschalter wirken auf diese Liste
        val loaded = listOf(item(Kind.LIVE, "DE| NEWS"), item(Kind.LIVE, "DE| SPORT"))
        val keys = CatFilterLogic.set(emptySet(), Kind.LIVE, "DE| SPORT", false)
        val rows = CatFilterLogic.rows(CatFilterLogic.counts(loaded, Kind.LIVE), keys, Kind.LIVE, "")
        assertEquals(listOf("DE| NEWS"), rows.filter { it.on }.map { it.name })
        assertTrue(rows.none { it.name.startsWith("TR") })
    }
}
