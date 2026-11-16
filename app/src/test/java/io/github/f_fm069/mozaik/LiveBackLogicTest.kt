package io.github.f_fm069.mozaik

import io.github.f_fm069.mozaik.LiveBackLogic.Action
import org.junit.Assert.assertEquals
import org.junit.Test

class LiveBackLogicTest {
    @Test fun channelsGoBackToCategories() =
        assertEquals(Action.ToCategories, LiveBackLogic.action(moving = false, adding = false, inChannels = true))

    @Test fun categoriesKeepDefault() =
        assertEquals(Action.Default, LiveBackLogic.action(moving = false, adding = false, inChannels = false))

    @Test fun movingHasPriority() {
        assertEquals(Action.DropMoved, LiveBackLogic.action(moving = true, adding = false, inChannels = false))
        assertEquals(Action.DropMoved, LiveBackLogic.action(moving = true, adding = true, inChannels = true))
    }

    @Test fun addingHasPriorityOverChannels() =
        assertEquals(Action.FinishAdd, LiveBackLogic.action(moving = false, adding = true, inChannels = true))

    private val names = listOf("PL", "GUIDE", FAV_CAT, "L:1", "NEW", "ALL", "Sport", "Film", HIDDEN_CAT)

    @Test fun focusRow_selectedCategory() {
        assertEquals("Sport", LiveBackLogic.focusRow(names, "Sport"))
        assertEquals(FAV_CAT, LiveBackLogic.focusRow(names, FAV_CAT))
        assertEquals("L:1", LiveBackLogic.focusRow(names, "L:1"))
    }

    @Test fun focusRow_hiddenOrMissingCategoryFallsBackToAll() {
        assertEquals("ALL", LiveBackLogic.focusRow(names, "Doku"))
        assertEquals("ALL", LiveBackLogic.focusRow(names, HIDDEN_CAT))
        assertEquals("ALL", LiveBackLogic.focusRow(names, "GUIDE"))
    }

    @Test fun focusRow_noAllRow() {
        assertEquals("Film", LiveBackLogic.focusRow(listOf("PL", "GUIDE", "NEW", "Film"), "X"))
        assertEquals(null, LiveBackLogic.focusRow(listOf("GUIDE", "NEW"), "X"))
    }

    @Test fun restoreIndex() {
        val ids = listOf("a", "b", "c")
        assertEquals(1, LiveBackLogic.restoreIndex(ids, "b"))
        assertEquals(0, LiveBackLogic.restoreIndex(ids, "zz"))
        assertEquals(0, LiveBackLogic.restoreIndex(ids, null))
        assertEquals(-1, LiveBackLogic.restoreIndex(emptyList(), "a"))
    }
}
