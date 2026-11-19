package io.github.f_fm069.mozaik

// Reine Regeln aus Nachtrag 17 (ohne Android, damit getestet werden kann)

/** Reine Formatierung der Technik-Info (getestet). */
object TechText {
    fun kbps(bps: Int): String = if (bps <= 0) "?" else if (bps >= 1_000_000) "%.1f Mbit/s".format(bps / 1_000_000.0) else "${bps / 1000} kbit/s"
    fun fps(f: Float): String = if (f <= 0f) "?" else if (Math.abs(f - Math.round(f)) < 0.01f) "${Math.round(f)}" else "%.3f".format(f)
    fun secs(ms: Long): String = "%.1f s".format(ms.coerceAtLeast(0L) / 1000.0)
}


/** Reine Regeln der App-Aktualisierung (getestet). */
object UpdateLogic {
    fun isNewer(current: Long, remote: Long): Boolean = remote > current

    /** Zahlen einer Versionsangabe: „v1.2.3“ → [1, 2, 3], „1.1-beta“ → [1, 1]. */
    fun parts(v: String): List<Int> =
        v.trim().trimStart('v', 'V').split('.', '-', '_', ' ').map { it.takeWhile(Char::isDigit) }.takeWhile { it.isNotEmpty() }.map { it.toIntOrNull() ?: 0 }

    /** Ist [remote] (z. B. Tag „v1.2“ eines GitHub-Release) neuer als die installierte Version [current] („1.1“)? */
    fun isNewerName(current: String, remote: String): Boolean {
        val a = parts(current); val b = parts(remote)
        if (b.isEmpty()) return false
        for (i in 0 until maxOf(a.size, b.size)) {
            val x = a.getOrElse(i) { 0 }; val y = b.getOrElse(i) { 0 }
            if (y != x) return y > x
        }
        return false
    }

    /** APK eines GitHub-Release: (Name, Download-Adresse); bevorzugt „Mozaik.apk“, sonst die erste APK. */
    fun pickApk(assets: List<Pair<String, String>>): String? {
        val apks = assets.filter { it.first.endsWith(".apk", ignoreCase = true) && it.second.startsWith("https://") }
        return (apks.firstOrNull { it.first.equals("Mozaik.apk", true) } ?: apks.firstOrNull { it.first.contains("mozaik", true) } ?: apks.firstOrNull())?.second
    }

    /** Prüfsummen-Datei eines GitHub-Release: nur „Mozaik.apk.sha256“ (https), sonst null. */
    fun pickSha(assets: List<Pair<String, String>>): String? =
        assets.firstOrNull { it.first.equals("Mozaik.apk.sha256", true) && isHttps(it.second) }?.second

    /** Downloads nur über https. */
    fun isHttps(url: String): Boolean = url.startsWith("https://", ignoreCase = true)

    /** SHA-256 als 64 Hex-Zeichen in Kleinbuchstaben; Leerzeichen werden ignoriert. Null, wenn kein gültiger Hash. */
    fun normalizeSha(s: String?): String? {
        val t = s.orEmpty().filterNot { it.isWhitespace() }.lowercase()
        return if (t.length == 64 && t.all { it in '0'..'9' || it in 'a'..'f' }) t else null
    }

    /** Inhalt von „Mozaik.apk.sha256“: nur der Hash oder „<hash>  Mozaik.apk“ (sha256sum/shasum). Null, wenn nicht lesbar. */
    fun parseShaFile(text: String?): String? =
        text.orEmpty().split(Regex("\\s+")).firstNotNullOfOrNull { normalizeSha(it) }

    fun hex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it.toInt() and 0xff) }

    enum class Download { OK, OK_NO_CHECKSUM, INCOMPLETE, MISMATCH }

    /**
     * Prüft die geladene Datei. [expectedLen] = Content-Length (<= 0: unbekannt, dann keine Längenprüfung),
     * [expectedSha] = Inhalt der Prüfsummen-Datei (null/ungültig: keine Prüfsumme, Update wird nicht blockiert).
     */
    fun verifyDownload(expectedSha: String?, actualHex: String, expectedLen: Long, actualLen: Long): Download {
        if (expectedLen > 0L && actualLen != expectedLen) return Download.INCOMPLETE
        val e = normalizeSha(expectedSha) ?: return Download.OK_NO_CHECKSUM
        return if (e == normalizeSha(actualHex)) Download.OK else Download.MISMATCH
    }

    /** Einmal am Tag automatisch suchen. */
    fun due(lastCheck: Long, now: Long): Boolean = now - lastCheck >= 20 * 3_600_000L || now < lastCheck

    /** Die APK-Adresse darf relativ zur Update-Adresse angegeben sein. */
    fun resolve(base: String, apk: String): String =
        if (apk.startsWith("http://") || apk.startsWith("https://")) apk
        else base.substringBeforeLast('/', base) + "/" + apk.trimStart('/')
}


/** Puffer-Stufen für langsames Internet (Nachtrag 18). Stufe 0 = Normal, 1 = Groß, 2 = Sehr groß. */
object BufferLogic {
    /** Wirksame Stufe: Modus 0 (Automatisch) nutzt die gelernte Stufe, 1 Normal, 2 Groß, 3 Sehr groß. */
    fun level(mode: Int, learned: Int): Int = when (mode) { 0 -> learned.coerceIn(0, 2); 1 -> 0; 2 -> 1; else -> 2 }

    /** Nur die Automatik lernt; eine Stufe pro Aufruf, höchstens 2. */
    fun bump(mode: Int, learned: Int): Int = if (mode == 0) (learned + 1).coerceAtMost(2) else learned

    /** Gelernte Stufe beim Start der App: immer Normal (die Leitung von gestern sagt nichts über heute). */
    const val LEARNED_AT_START = 0

    /**
     * So lange muss ohne Ruckler abgespielt werden, bis die gelernte Stufe um eins sinkt: 10 Minuten.
     * Wachsen braucht 3 Ruckler in 2 Minuten ([StallLogic.WINDOW]); 10 Minuten sind fünf solche Fenster ohne einen einzigen
     * Ruckler. Kürzer (2–3 Minuten) würde bei wackligem WLAN hin- und herpendeln (kleiner → Ruckeln → größer),
     * länger würde den Kanalwechsel unnötig lange langsam halten. Von der höchsten Stufe zurück auf Normal: 20 Minuten.
     */
    const val RELAX_MS = 10 * 60_000L

    /** Stufe nach [quietMs] Wiedergabe ohne Ruckler: eine Stufe kleiner, sobald [RELAX_MS] erreicht ist. */
    fun relax(learned: Int, quietMs: Long): Int = if (learned > 0 && quietMs >= RELAX_MS) learned - 1 else learned

    /** Startpuffer für „Schneller Kanalwechsel“ (nur Live TV), entspricht dem Standard des Players. */
    const val FAST_LIVE_START_MS = 1000

    /**
     * Startpuffer in ms: Grundwert des Geräts, mit „Schneller Kanalwechsel“ bei Live höchstens [FAST_LIVE_START_MS];
     * je Stufe 2 s mehr (Ruckeln schützt also auch mit der schnellen Einstellung).
     */
    fun startMs(baseStart: Int, level: Int, fastLive: Boolean = false): Int =
        (if (fastLive) minOf(baseStart, FAST_LIVE_START_MS) else baseStart) + 2000 * level.coerceIn(0, 2)

    /** Puffer nach einem Ruckler (bevor es weitergeht) in ms: je Stufe 4 s mehr (höchste Stufe: + 8 s). */
    fun rebufferMs(baseRebuffer: Int, level: Int): Int = baseRebuffer + 4000 * level.coerceIn(0, 2)

    /** [base] = (min ms, max ms, Start ms, Nachpuffern ms, Byte-Grenze oder -1). */
    fun scale(base: IntArray, level: Int): IntArray {
        if (level <= 0) return base
        val m = level + 1
        val bytes = if (base[4] > 0) minOf(base[4].toLong() * m, 64L * 1024 * 1024).toInt() else base[4]
        return intArrayOf(base[0] * m, base[1] * m, startMs(base[2], level), rebufferMs(base[3], level), bytes)
    }
}
