package io.github.f_fm069.mozaik

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest

/** Prüfsumme, Länge und https beim Update-Download (reine Regeln in UpdateLogic). */
class UpdateChecksumTest {
    private val data = "mozaik".toByteArray()
    private val sha = UpdateLogic.hex(MessageDigest.getInstance("SHA-256").digest(data))
    private val len = data.size.toLong()

    @Test fun passt() {
        assertEquals(UpdateLogic.Download.OK, UpdateLogic.verifyDownload(sha, sha, len, len))
        // Groß-/Kleinschreibung und Leerzeichen sind egal
        assertEquals(UpdateLogic.Download.OK, UpdateLogic.verifyDownload(" " + sha.uppercase() + "\n", sha, len, len))
    }

    @Test fun passtNicht() {
        val other = "0".repeat(64)
        assertEquals(UpdateLogic.Download.MISMATCH, UpdateLogic.verifyDownload(other, sha, len, len))
    }

    @Test fun dateiUnvollstaendig() {
        assertEquals(UpdateLogic.Download.INCOMPLETE, UpdateLogic.verifyDownload(sha, sha, len, len - 1))
        // Unvollständig schlägt auch ohne Prüfsumme an
        assertEquals(UpdateLogic.Download.INCOMPLETE, UpdateLogic.verifyDownload(null, sha, len, len - 1))
        // Unbekannte Länge (-1): keine Längenprüfung
        assertEquals(UpdateLogic.Download.OK, UpdateLogic.verifyDownload(sha, sha, -1, len))
    }

    @Test fun pruefsummeFehlt() {
        assertEquals(UpdateLogic.Download.OK_NO_CHECKSUM, UpdateLogic.verifyDownload(null, sha, len, len))
        assertEquals(UpdateLogic.Download.OK_NO_CHECKSUM, UpdateLogic.verifyDownload("", sha, len, len))
        assertEquals(UpdateLogic.Download.OK_NO_CHECKSUM, UpdateLogic.verifyDownload("kein hash", sha, len, len))
    }

    @Test fun shaDateiLesen() {
        assertEquals(sha, UpdateLogic.parseShaFile(sha))
        assertEquals(sha, UpdateLogic.parseShaFile("$sha  Mozaik.apk\n"))
        assertEquals(sha, UpdateLogic.parseShaFile(sha.uppercase() + "\r\n"))
        assertNull(UpdateLogic.parseShaFile(""))
        assertNull(UpdateLogic.parseShaFile(null))
        assertNull(UpdateLogic.parseShaFile("<html>Not Found</html>"))
        assertNull(UpdateLogic.parseShaFile("abc123"))
    }

    @Test fun nurHttps() {
        assertTrue(UpdateLogic.isHttps("https://github.com/x/Mozaik.apk"))
        assertFalse(UpdateLogic.isHttps("http://github.com/x/Mozaik.apk"))
        assertFalse(UpdateLogic.isHttps("ftp://x/Mozaik.apk"))
        assertFalse(UpdateLogic.isHttps(""))
    }

    @Test fun pruefsummenAssetWaehlen() {
        val a = listOf(
            "Mozaik.apk" to "https://x/Mozaik.apk",
            "Mozaik-1.2.apk.sha256" to "https://x/Mozaik-1.2.apk.sha256",
            "Mozaik.apk.sha256" to "https://x/Mozaik.apk.sha256"
        )
        assertEquals("https://x/Mozaik.apk.sha256", UpdateLogic.pickSha(a))
        assertNull(UpdateLogic.pickSha(a.take(2)))
        assertNull(UpdateLogic.pickSha(listOf("Mozaik.apk.sha256" to "http://x/Mozaik.apk.sha256")))
        assertEquals("https://x/Mozaik.apk", UpdateLogic.pickApk(a))
    }
}
