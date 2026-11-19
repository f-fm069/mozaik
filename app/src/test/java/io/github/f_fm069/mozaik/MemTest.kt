package io.github.f_fm069.mozaik

import org.junit.Assert.assertEquals
import org.junit.Test

class MemTest {
    @Test fun schwacheGeraeteDekodierenHoechstensZweiBilder() {
        assertEquals(2, Mem.imageDecoders(0))
        assertEquals(2, Mem.imageDecoders(1))
    }

    @Test fun starkeGeraeteBleibenBeiVier() {
        assertEquals(4, Mem.imageDecoders(2))
    }

    @Test fun passtZuDenSpeicherstufen() {
        // 1 GB, Low-RAM-Flag und 2 GB: schwach; 4 GB: stark
        assertEquals(2, Mem.imageDecoders(Mem.tierFor(1024, false)))
        assertEquals(2, Mem.imageDecoders(Mem.tierFor(4096, true)))
        assertEquals(2, Mem.imageDecoders(Mem.tierFor(2048, false)))
        assertEquals(2, Mem.imageDecoders(Mem.tierFor(3072, false)))
        assertEquals(4, Mem.imageDecoders(Mem.tierFor(4096, false)))
        // Unbekannter Speicher (0) zählt wie bisher als stark
        assertEquals(4, Mem.imageDecoders(Mem.tierFor(0, false)))
    }
}
