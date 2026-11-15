package io.github.f_fm069.mozaik

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Messung des Kanalwechsels: Speichern, Schritte, Median/Durchschnitt, Streamtyp. */
class ZapLogicTest {
    private fun rec(type: Int, vararg m: Long) = ZapLogic.Rec(type, m)

    @Test fun kodieren_und_lesen() {
        val r = rec(ZapLogic.HLS, 10, 300, 350, 700, 760, 900, 2400)
        val back = ZapLogic.decode(ZapLogic.encode(r))!!
        assertEquals(ZapLogic.HLS, back.type)
        assertArrayEquals(r.marks, back.marks)
        assertNull(ZapLogic.decode("x;1,2"))
        assertNull(ZapLogic.decode("0;1,2,3"))
        assertNull(ZapLogic.decode("7;1,2,3,4,5,6,7"))
        assertNull(ZapLogic.decode(null))
    }

    @Test fun gesamt_bis_erstes_bild_sonst_wiedergabe() {
        assertEquals(900, rec(0, 10, 300, 350, 700, 760, 900, 2400).total)
        // Radio ohne Bild
        assertEquals(1200, rec(0, 10, 300, 350, 700, -1, -1, 1200).total)
    }

    @Test fun schritte_mit_luecken_und_vertauschter_reihenfolge() {
        assertArrayEquals(longArrayOf(10, 290, 50, 350, 60, 140, 1500), ZapLogic.steps(longArrayOf(10, 300, 350, 700, 760, 900, 2400)))
        // Verbindung unbekannt: erste Daten zählen ab der Vorbereitung
        assertArrayEquals(longArrayOf(10, -1, 340, 350, 60, 140, 1500), ZapLogic.steps(longArrayOf(10, -1, 350, 700, 760, 900, 2400)))
        // Decoder vor „Format erkannt“ gemeldet: kein negativer Schritt
        assertArrayEquals(longArrayOf(10, 290, 50, 350, 0, 200, 1500), ZapLogic.steps(longArrayOf(10, 300, 350, 700, 600, 900, 2400)))
    }

    @Test fun median_und_durchschnitt() {
        assertEquals(-1, ZapLogic.median(emptyList()))
        assertEquals(5, ZapLogic.median(listOf(9, 1, 5)))
        assertEquals(4, ZapLogic.median(listOf(1, 3, 5, 100)))
        assertEquals(27, ZapLogic.mean(listOf(1, 3, 5, 100)))
        assertEquals(-1, ZapLogic.mean(emptyList()))
    }

    @Test fun je_typ_hoechstens_zwanzig_neueste_zuerst() {
        var l = emptyList<ZapLogic.Rec>()
        repeat(25) { i -> l = ZapLogic.add(l, rec(ZapLogic.TS, i.toLong(), -1, -1, -1, -1, -1, -1)) }
        l = ZapLogic.add(l, rec(ZapLogic.HLS, 99, -1, -1, -1, -1, -1, -1))
        assertEquals(21, l.size)
        assertEquals(ZapLogic.HLS, l[0].type)
        assertEquals(24L, l[1].marks[0])
        assertEquals(20, l.count { it.type == ZapLogic.TS })
    }

    @Test fun streamtyp() {
        assertEquals(ZapLogic.HLS, ZapLogic.guessFromUrl("http://host/live/a/b/1.m3u8?token=x"))
        assertEquals(ZapLogic.TS, ZapLogic.guessFromUrl("http://host/live/a/b/1.ts"))
        assertEquals(ZapLogic.TS, ZapLogic.guessFromUrl("tap://3/0.ts"))
        assertEquals(-1, ZapLogic.guessFromUrl("http://host/live/a/b/1"))
        assertEquals(ZapLogic.HLS, ZapLogic.typeOf(true, "video/mp2t", -1))
        assertEquals(ZapLogic.TS, ZapLogic.typeOf(false, "video/mp2t", -1))
        assertEquals(ZapLogic.OTHER, ZapLogic.typeOf(false, "video/mp4", ZapLogic.TS))
        assertEquals(ZapLogic.TS, ZapLogic.typeOf(false, null, ZapLogic.TS))
        assertEquals(ZapLogic.OTHER, ZapLogic.typeOf(false, null, -1))
    }

    @Test fun letztes_bild_halten() {
        assertEquals(true, HoldLogic.shouldHold(true, "b", "a", 1920, false))
        assertEquals(false, HoldLogic.shouldHold(false, "b", "a", 1920, false))   // Einstellung aus
        assertEquals(false, HoldLogic.shouldHold(true, "a", "a", 1920, false))    // gleicher Sender
        assertEquals(false, HoldLogic.shouldHold(true, "b", "a", 0, false))       // kein Bild (Radio, noch nichts da)
        assertEquals(false, HoldLogic.shouldHold(true, "b", "a", 1920, true))     // alter Sender im Fehler
        assertEquals(true, HoldLogic.shouldMute(true, "b", "a"))
        assertEquals(false, HoldLogic.shouldMute(true, "a", "a"))
        assertEquals(false, HoldLogic.shouldMute(false, "b", "a"))
        assertEquals(true, HoldLogic.TIMEOUT_MS in 4000L..5000L)
    }

    @Test fun uebersetzung_der_zusammenfassung() {
        I18nCore.load(DictA, DictB, DictC, DictD, DictE, DictF, DictG, DictH, DictI, DictJ, DictK, DictL)
        assertEquals("12 switches · median 1830 ms · average 1950 ms", I18nCore.translate("12 Wechsel · Median 1830 ms · Durchschnitt 1950 ms", 1))
        assertEquals("First picture", I18nCore.translate("Erstes Bild", 1))
    }
}
