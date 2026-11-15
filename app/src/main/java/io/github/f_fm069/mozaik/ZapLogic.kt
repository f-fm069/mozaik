package io.github.f_fm069.mozaik

/**
 * Messung des Kanalwechsels in Live TV (ohne Android, testbar).
 * Eine Messung besteht aus dem Streamtyp und Zeitpunkten in ms seit dem Umschalten (-1 = unbekannt).
 * Gespeichert werden nur diese Zahlen: keine Adressen, keine Sendernamen.
 */
object ZapLogic {
    const val TS = 0
    const val HLS = 1
    const val OTHER = 2
    val TYPE_NAMES = listOf("TS", "HLS", "Sonstige")

    /** Zeitpunkte: Stream wird geöffnet, Server antwortet, erste Daten, Format erkannt, Decoder bereit, erstes Bild, Wiedergabe läuft. */
    const val OPEN = 0
    const val CONNECT = 1
    const val DATA = 2
    const val FORMAT = 3
    const val DECODER = 4
    const val FRAME = 5
    const val PLAY = 6
    const val N = 7
    val STEP_NAMES = listOf("Vorbereitung", "Verbindung", "Erste Daten", "Format erkannt", "Decoder bereit", "Erstes Bild", "Wiedergabe läuft")

    /** Je Streamtyp so viele Messungen behalten. */
    const val KEEP = 20

    class Rec(val type: Int, val marks: LongArray) {
        /** Gesamtzeit bis zum ersten Bild (ohne Bild, z. B. Radio: bis die Wiedergabe läuft). */
        val total: Long get() = if (marks[FRAME] >= 0) marks[FRAME] else marks[PLAY]
    }

    fun encode(r: Rec): String = r.type.toString() + ";" + r.marks.joinToString(",")

    fun decode(s: String?): Rec? {
        val p = s?.split(';') ?: return null
        if (p.size != 2) return null
        val type = p[0].toIntOrNull()?.takeIf { it in TS..OTHER } ?: return null
        val m = p[1].split(',').map { it.toLongOrNull() ?: return null }
        if (m.size != N) return null
        return Rec(type, m.toLongArray())
    }

    /** Neueste zuerst; je Typ höchstens [KEEP]. */
    fun add(list: List<Rec>, r: Rec): List<Rec> {
        val out = ArrayList<Rec>()
        val count = IntArray(3)
        for (x in listOf(r) + list) {
            if (count[x.type] >= KEEP) continue
            count[x.type]++
            out.add(x)
        }
        return out
    }

    /**
     * Dauer jedes Schritts in ms (-1 = unbekannt): Abstand zum letzten bekannten früheren Zeitpunkt.
     * Kommen Meldungen in anderer Reihenfolge, zählt der Schritt 0 statt negativ.
     */
    fun steps(marks: LongArray): LongArray {
        val out = LongArray(N) { -1 }
        var last = 0L
        for (i in 0 until N) {
            val m = marks[i]
            if (m < 0) continue
            out[i] = (m - last).coerceAtLeast(0)
            last = maxOf(last, m)
        }
        return out
    }

    fun median(xs: List<Long>): Long {
        if (xs.isEmpty()) return -1
        val s = xs.sorted()
        val n = s.size
        return if (n % 2 == 1) s[n / 2] else (s[n / 2 - 1] + s[n / 2]) / 2
    }

    fun mean(xs: List<Long>): Long = if (xs.isEmpty()) -1 else Math.round(xs.average())

    /** Median je Schritt über mehrere Messungen (nur bekannte Werte). */
    fun stepMedians(list: List<Rec>): LongArray {
        val st = list.map { steps(it.marks) }
        return LongArray(N) { i -> median(st.map { it[i] }.filter { it >= 0 }) }
    }

    /** Vermutung aus der Adresse (nur im Speicher, nie gespeichert): HLS, TS oder unbekannt (-1). */
    fun guessFromUrl(url: String): Int {
        val u = url.lowercase().substringBefore('?')
        return when {
            u.endsWith(".m3u8") || u.endsWith(".m3u") -> HLS
            u.endsWith(".ts") || u.startsWith("tap://") -> TS
            else -> -1
        }
    }

    /** Streamtyp: HLS laut Player, sonst TS laut Containerformat oder Adresse, sonst Sonstige. */
    fun typeOf(hls: Boolean, container: String?, guess: Int): Int = when {
        hls -> HLS
        container == "video/mp2t" -> TS
        container != null -> OTHER
        guess == TS || guess == HLS -> guess
        else -> OTHER
    }
}

/** „Beim Umschalten letztes Bild halten“: Regeln ohne Android (testbar). */
object HoldLogic {
    /** Höchstens so lange bleibt das alte Bild stehen, danach erscheint die normale Ladeanzeige. */
    const val TIMEOUT_MS = 4500L

    /** Bild halten? Nur wenn eingeschaltet, wirklich ein anderer Stream kommt und gerade Video ohne Fehler läuft. */
    fun shouldHold(enabled: Boolean, newUrl: String, curUrl: String, videoWidth: Int, hasError: Boolean): Boolean =
        enabled && newUrl != curUrl && videoWidth > 0 && !hasError

    /** Alten Ton sofort stumm? Bei jedem echten Wechsel (auch ohne Bild, z. B. Radio), wenn eingeschaltet. */
    fun shouldMute(enabled: Boolean, newUrl: String, curUrl: String): Boolean = enabled && newUrl != curUrl
}
