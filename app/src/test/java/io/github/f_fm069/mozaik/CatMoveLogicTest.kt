package io.github.f_fm069.mozaik

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CatMoveLogicTest {
    private val l = listOf("A", "B", "C", "D")

    @Test fun step_upAndDown() {
        assertEquals(listOf("A", "C", "B", "D"), CatMoveLogic.step(l, "C", -1))
        assertEquals(listOf("A", "B", "D", "C"), CatMoveLogic.step(l, "C", 1))
    }

    @Test fun step_stopsAtEdges() {
        assertEquals(l, CatMoveLogic.step(l, "A", -1))
        assertEquals(l, CatMoveLogic.step(l, "D", 1))
    }

    @Test fun step_unknownNameOrEmptyList() {
        assertEquals(l, CatMoveLogic.step(l, "X", 1))
        assertEquals(emptyList<String>(), CatMoveLogic.step(emptyList(), "A", 1))
    }

    @Test fun toEnd_movesToLastPlace() {
        assertEquals(listOf("B", "C", "D", "A"), CatMoveLogic.toEnd(l, "A"))
        assertEquals(listOf("A", "B", "D", "C"), CatMoveLogic.toEnd(l, "C"))
    }

    @Test fun toEnd_alreadyLastOrUnknownOrSingle() {
        assertEquals(l, CatMoveLogic.toEnd(l, "D"))
        assertEquals(l, CatMoveLogic.toEnd(l, "X"))
        assertEquals(listOf("A"), CatMoveLogic.toEnd(listOf("A"), "A"))
        assertEquals(emptyList<String>(), CatMoveLogic.toEnd(emptyList(), "A"))
    }

    @Test fun toStart_movesToFirstPlace() =
        assertEquals(listOf("C", "A", "B", "D"), CatMoveLogic.toStart(l, "C"))

    @Test fun canStep_edges() {
        assertFalse(CatMoveLogic.canStep(l, "A", -1))
        assertTrue(CatMoveLogic.canStep(l, "A", 1))
        assertFalse(CatMoveLogic.canStep(l, "D", 1))
        assertTrue(CatMoveLogic.canStep(l, "D", -1))
        assertFalse(CatMoveLogic.canStep(l, "X", 1))
        assertFalse(CatMoveLogic.canStep(l, "A", 0))
    }

    @Test fun merge_keepsPlacesOfHiddenAndFavorites() {
        // H = ausgeblendet, F = Favorit: nur A, B, C, D tauschen ihre Plätze
        val full = listOf("A", "H", "B", "F", "C", "D")
        val merged = CatMoveLogic.merge(full, listOf("A", "B", "C", "D"), listOf("B", "A", "D", "C"))
        assertEquals(listOf("B", "H", "A", "F", "D", "C"), merged)
    }

    @Test fun merge_mismatchKeepsFull() {
        val full = listOf("A", "H", "B")
        assertEquals(full, CatMoveLogic.merge(full, listOf("A", "B"), listOf("B")))
        assertEquals(full, CatMoveLogic.merge(full, listOf("A", "B"), listOf("A", "X")))
    }

    @Test fun stepThenMerge_movesPastHiddenNeighbour() {
        val full = listOf("A", "H", "B")
        val shown = listOf("A", "B")
        val merged = CatMoveLogic.merge(full, shown, CatMoveLogic.step(shown, "B", -1))
        assertEquals(listOf("B", "H", "A"), merged)
    }
}
