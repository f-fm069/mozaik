package io.github.f_fm069.mozaik

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Schritte einer Messung als Text: „Vorbereitung 12 · Verbindung 340 · …“ (unbekannte Schritte fehlen). */
private fun stepText(steps: LongArray): String =
    steps.indices.filter { steps[it] >= 0 }.joinToString(" · ") { Lang.tr(ZapLogic.STEP_NAMES[it]) + " " + steps[it] }

/** Zusammenfassung je Streamtyp: (Typ, „n Wechsel · Median … · Durchschnitt …“, Median je Schritt). */
private fun zapStats(list: List<ZapLogic.Rec>): List<Triple<String, String, String>> =
    (ZapLogic.TS..ZapLogic.OTHER).mapNotNull { t ->
        val l = list.filter { it.type == t && it.total >= 0 }
        if (l.isEmpty()) return@mapNotNull null
        val tot = l.map { it.total }
        Triple(
            Lang.tr(ZapLogic.TYPE_NAMES[t]),
            Lang.tr("${l.size} Wechsel · Median ${ZapLogic.median(tot)} ms · Durchschnitt ${ZapLogic.mean(tot)} ms"),
            stepText(ZapLogic.stepMedians(l))
        )
    }

/** Für „Bericht kopieren“: nur Zahlen und Streamtyp. */
fun zapReport(): String {
    val s = zapStats(ZapProbe.list)
    if (s.isEmpty()) return ""
    return "\n" + Lang.tr("Kanalwechsel (Messung)") + ":" + s.joinToString("") { "\n  " + it.first + ": " + it.second + "\n    " + it.third }
}

/** Einstellungen > Diagnose: Zeit vom Umschalten bis zum ersten Bild, letzte Wechsel, Zurücksetzen. */
@Composable
internal fun ZapSection(ctx: Context) {
    ZapProbe.init(ctx)
    val list = ZapProbe.list
    SectionTitle("Kanalwechsel (Messung)")
    Hint("Zeit vom Umschalten in Live TV bis zum ersten Bild, in Millisekunden und aufgeteilt in Schritte. Gespeichert werden nur Zeiten und der Streamtyp, nur auf diesem Gerät.")
    if (list.isEmpty()) {
        Text("Noch keine Messung. Schalte in Live TV ein paar Sender um.", color = Color.White, fontSize = 16.sp)
        return
    }
    zapStats(list).forEach { (type, sum, steps) ->
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(type, color = Muted, fontSize = 16.sp, modifier = Modifier.width(170.dp), raw = true)
            Text(sum + "\n" + Lang.tr("Median je Schritt") + ": " + steps, color = Color.White, fontSize = 16.sp, modifier = Modifier.widthIn(max = 640.dp), raw = true)
        }
    }
    Text("Letzte Wechsel", color = Muted, fontSize = 16.sp)
    list.take(ZapLogic.KEEP).forEach { r ->
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(Lang.tr(ZapLogic.TYPE_NAMES[r.type]) + "  " + r.total + " ms", color = Color.White, fontSize = 15.sp, modifier = Modifier.width(170.dp), raw = true)
            Text(stepText(ZapLogic.steps(r.marks)), color = Muted, fontSize = 15.sp, modifier = Modifier.widthIn(max = 640.dp), raw = true)
        }
    }
    PillButton("Messwerte zurücksetzen", light = true, icon = AIcon.Refresh) { ZapProbe.reset(ctx) }
}
