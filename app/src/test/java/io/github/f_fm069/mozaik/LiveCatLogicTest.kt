package io.github.f_fm069.mozaik

import org.junit.Assert.assertEquals
import org.junit.Test

class LiveCatLogicTest {
    private val provider = listOf("LA| GENERAL", "DE| NEWS", "HR| HORSE RACING", "DE| SPORT", "TS| TENNIS LIVE")

    @Test fun nothingHidden_keepsAll() {
        val r = LiveCatLogic.visible(provider, emptyList(), emptyList())
        assertEquals(emptyList<String>(), r.favs)
        assertEquals(provider, r.rest)
    }

    @Test fun hidden_areRemoved() {
        val r = LiveCatLogic.visible(provider, emptyList(), listOf("LA| GENERAL", "HR| HORSE RACING", "TS| TENNIS LIVE"))
        assertEquals(listOf("DE| NEWS", "DE| SPORT"), r.all)
    }

    @Test fun keepsGivenSortOrder() {
        val sorted = CatOrderLogic.apply(provider, CatOrderLogic.AZ, emptyList())
        val r = LiveCatLogic.visible(sorted, emptyList(), listOf("HR| HORSE RACING"))
        assertEquals(listOf("DE| NEWS", "DE| SPORT", "LA| GENERAL", "TS| TENNIS LIVE"), r.rest)
    }

    @Test fun favs_firstInFavOrder_andNotRepeated() {
        val r = LiveCatLogic.visible(provider, listOf("DE| SPORT", "DE| NEWS"), emptyList())
        assertEquals(listOf("DE| SPORT", "DE| NEWS"), r.favs)
        assertEquals(listOf("LA| GENERAL", "HR| HORSE RACING", "TS| TENNIS LIVE"), r.rest)
    }

    @Test fun hiddenFav_isRemoved() {
        val r = LiveCatLogic.visible(provider, listOf("DE| SPORT", "TS| TENNIS LIVE"), listOf("TS| TENNIS LIVE"))
        assertEquals(listOf("DE| SPORT"), r.favs)
        assertEquals(false, "TS| TENNIS LIVE" in r.all)
    }

    @Test fun unknownNames_areIgnored() {
        val r = LiveCatLogic.visible(provider, listOf("GONE"), listOf("ALSO GONE"))
        assertEquals(emptyList<String>(), r.favs)
        assertEquals(provider, r.rest)
    }
}
