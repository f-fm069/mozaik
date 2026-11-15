package io.github.f_fm069.mozaik

import org.junit.Assert.assertEquals
import org.junit.Test

/** Gelernter Puffer: wächst bei Ruckeln, sinkt nach ruhiger Wiedergabe, beginnt beim App-Start beim Standard. Schneller Kanalwechsel. */
class BufferRelaxTest {
    private val big = Mem.bufferPlan(2)   // starkes Gerät: Start 2,5 s, Nachpuffern 5 s

    @Test fun start_beim_standard() {
        assertEquals(0, BufferLogic.LEARNED_AT_START)
        val lvl = BufferLogic.level(0, BufferLogic.LEARNED_AT_START)
        assertEquals(2500, BufferLogic.startMs(big[2], lvl))
        assertEquals(5000, BufferLogic.rebufferMs(big[3], lvl))
    }

    @Test fun waechst_bei_ruckeln_bis_obergrenze() {
        var l = BufferLogic.LEARNED_AT_START
        l = BufferLogic.bump(0, l); assertEquals(4500, BufferLogic.startMs(big[2], l))
        l = BufferLogic.bump(0, l); assertEquals(6500, BufferLogic.startMs(big[2], l))
        // Obergrenze bleibt 6,5 s
        l = BufferLogic.bump(0, l); assertEquals(2, l); assertEquals(6500, BufferLogic.startMs(big[2], l))
        assertEquals(13_000, BufferLogic.rebufferMs(big[3], l))
        // Feste Modi lernen nicht
        assertEquals(0, BufferLogic.bump(1, 0))
    }

    @Test fun sinkt_schrittweise_nach_ruhiger_wiedergabe() {
        assertEquals(2, BufferLogic.relax(2, BufferLogic.RELAX_MS - 1))
        assertEquals(1, BufferLogic.relax(2, BufferLogic.RELAX_MS))
        assertEquals(0, BufferLogic.relax(1, BufferLogic.RELAX_MS))
        assertEquals(0, BufferLogic.relax(0, BufferLogic.RELAX_MS * 5))
        // Ablauf wie im Player: alle 5 s Wiedergabe, ein Ruckler setzt die ruhige Zeit zurück
        var level = 2; var quiet = 0L
        fun play(ms: Long) { var t = 0L; while (t < ms) { quiet += 5000; t += 5000; val n = BufferLogic.relax(level, quiet); if (n != level) { level = n; quiet = 0 } } }
        play(9 * 60_000L); assertEquals(2, level)
        quiet = 0L                     // Ruckler
        play(9 * 60_000L); assertEquals(2, level)
        play(60_000L); assertEquals(1, level)
        play(10 * 60_000L); assertEquals(0, level)
    }

    @Test fun scale_bleibt_wie_bisher() {
        val s = BufferLogic.scale(big, 1)
        assertEquals(4500, s[2]); assertEquals(9000, s[3]); assertEquals(60_000, s[0])
        assertEquals(big.toList(), BufferLogic.scale(big, 0).toList())
    }

    @Test fun schneller_kanalwechsel_nur_startpuffer() {
        assertEquals(1000, BufferLogic.startMs(big[2], 0, fastLive = true))
        assertEquals(1000, BufferLogic.startMs(Mem.bufferPlan(0)[2], 0, fastLive = true))
        // Ruckeln schützt weiterhin
        assertEquals(3000, BufferLogic.startMs(big[2], 1, fastLive = true))
        assertEquals(5000, BufferLogic.startMs(big[2], 2, fastLive = true))
        // Nachpuffern bleibt unverändert
        assertEquals(5000, BufferLogic.rebufferMs(big[3], 0))
    }
}
