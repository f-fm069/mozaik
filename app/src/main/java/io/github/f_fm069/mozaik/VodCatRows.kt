package io.github.f_fm069.mozaik

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester

/**
 * Kategorienansicht von Filme und Serien als Posterreihen: oben „Weiterschauen“, darunter jede Kategorie als Reihe.
 * „Filme“ und „Serien“ in der Seitenleiste öffnen sie direkt (Einstellungen > Allgemein, [General.vodCats]),
 * Zurück führt zur Startseite des Bereichs. Die Idee „direkt zu den Kategorien“ stammt aus IPVIO Stream (W-002, GPL-3.0).
 */
object VodRows {
    /** Bereich, der gerade die Kategorienansicht zeigt: 1 = Filme, 2 = Serien, -1 = keiner (Startseite des Bereichs). */
    var tab by mutableIntStateOf(-1)
    /** Zählt bei jedem neuen Öffnen hoch: die Ansicht beginnt dann wieder oben und mit den ersten Kategorien. */
    var gen by mutableIntStateOf(0)
        private set
    /** Aufgebaute Kategorien je Bereich (der Rest kommt beim Weiterscrollen). */
    val shown = mutableStateMapOf<Int, Int>()
    private val lists = HashMap<Int, LazyListState>()

    /** Eigener Scroll-Zustand je Bereich, getrennt von der Startseite des Bereichs. */
    fun state(tab: Int): LazyListState = lists.getOrPut(tab) { LazyListState() }

    /** Menüpunkt gewählt: Kategorienansicht von [area] neu öffnen (-1 = schließen). */
    fun open(area: Int) {
        tab = area
        lists.clear(); shown.clear(); gen++
        menuFor = null; hidMenu = false; sortMenu = false; focusRow = null
    }

    // Menüs der Ansicht (langes OK auf einer Reihe, „Ausgeblendet“ am Ende, „Kategorien sortieren …“)
    var menuFor by mutableStateOf<String?>(null)
    var hidMenu by mutableStateOf(false)
    var sortMenu by mutableStateOf(false)
    /** Nach dem Ausblenden einer Reihe: diese Reihe bekommt den Fokus (sonst fiele er in die Seitenleiste). */
    var focusRow by mutableStateOf<String?>(null)
}

/** Schlüssel der Einträge, über denen die Infos des markierten Titels (Billboard) nicht erscheinen sollen. */
val VOD_ROWS_NO_BB = setOf("vtop1", "vtop2", "vres1", "vres2", "vhid1", "vhid2", "vnone1", "vnone2")

/**
 * Einträge der Kategorienansicht für die Liste des Bereichs. [groups] sind die Kategorien in der gewählten Sortierung
 * (ohne ausgeblendete und ignorierte), davon werden [shown] aufgebaut. [canEdit]: Anbieter-Kategorien (nicht Auto-Genres),
 * nur dann gibt es das Menü zum Ausblenden und Verschieben.
 */
internal fun LazyListScope.vodCatRows(
    tab: Int, noun: String,
    header: @Composable (Modifier) -> Unit,
    resume: List<Pair<Entry, Item>>,
    groups: List<Pair<String, List<Item>>>, shown: Int, hiddenCount: Int, canEdit: Boolean,
    cardW: Int, firstReq: FocusRequester,
    onOpen: (Item) -> Unit, onResume: (Entry, Item) -> Unit, onAll: (String, List<Item>) -> Unit
) {
    item(key = "vtop$tab") { header(Modifier) }
    // Weiterschauen oben; fehlt, solange nichts angefangen wurde. Sonst bekommt die erste Kategorie den Fokus.
    if (resume.isNotEmpty()) item(key = "vres$tab") { ResumeRow(resume, cardW, onResume, firstReq) }
    groups.take(shown).forEachIndexed { i, (g, l) ->
        item(key = "vg$tab|$g") {
            val menu: (() -> Unit)? = if (canEdit) ({ VodRows.menuFor = g }) else null
            val first = if (i == 0 && resume.isEmpty()) firstReq else null
            // Fokus nach dem Ausblenden der Reihe davor: hierher statt in die Seitenleiste
            val take = VodRows.focusRow == g
            val req = first ?: remember { FocusRequester() }
            if (take) LaunchedEffect(Unit) {
                repeat(10) {
                    delay(80)
                    if (runCatching { req.requestFocus() }.getOrDefault(false)) { VodRows.focusRow = null; return@LaunchedEffect }
                }
                VodRows.focusRow = null
            }
            ItemsRow(g, l, cardW, onOpen, firstReq = req, onMenu = menu) { onAll(g, l) }
        }
    }
    if (groups.isEmpty() && resume.isEmpty() && hiddenCount == 0) item(key = "vnone$tab") { EmptyHint("Keine $noun in dieser Playlist") }
    // Ausgeblendete Kategorien zurückholen (wie „Ausgeblendet“ in der Kategorienliste); erst, wenn alle Reihen aufgebaut sind
    if (canEdit && hiddenCount > 0 && shown >= groups.size) item(key = "vhid$tab") {
        Box(Modifier.fillMaxWidth().padding(start = RowPadX, top = dpx(28))) {
            PillButton("Ausgeblendete Kategorien ($hiddenCount)", icon = AIcon.Categories) { VodRows.hidMenu = true }
        }
    }
}

/** Menüs der Kategorienansicht (Ausblenden, Verschieben, Sortieren, Wiedereinblenden), wie in der Kategorienliste. */
@Composable
internal fun VodRowsMenus(ctx: Context, kind: Kind, providerNames: List<String>, shownNames: List<String>) {
    val area = if (kind == Kind.MOVIE) CatOrder.MOVIE else CatOrder.SERIES
    val cur = VodRows.menuFor
    if (cur != null) OptionPopup(
        listOf("Kategorie ausblenden", "Nach oben verschieben", "Nach unten verschieben", "An den Anfang verschieben", "Kategorien sortieren …"),
        -1, { VodRows.menuFor = null }
    ) { i ->
        VodRows.menuFor = null
        when (i) {
            0 -> {
                VodRows.focusRow = VodRowsLogic.focusAfterHide(shownNames, cur)
                IgnoredGroups.set(ctx, IgnoredGroups.groupKey(kind, cur), true)
            }
            1 -> CatOrder.move(ctx, area, providerNames, cur, -1)
            2 -> CatOrder.move(ctx, area, providerNames, cur, 1)
            3 -> CatOrder.toTop(ctx, area, providerNames, cur)
            else -> VodRows.sortMenu = true
        }
    }
    if (VodRows.hidMenu) {
        val hidden = IgnoredGroups.hiddenGroups(kind)
        OptionPopup(listOf("Alle wieder einblenden") + hidden, -1, { VodRows.hidMenu = false }) { i ->
            VodRows.hidMenu = false
            if (i == 0) IgnoredGroups.setMany(ctx, hidden.map { IgnoredGroups.groupKey(kind, it) }, false)
            else IgnoredGroups.set(ctx, IgnoredGroups.groupKey(kind, hidden[i - 1]), false)
        }
    }
    if (VodRows.sortMenu) OptionPopup(SORT_MODES, CatOrder.mode(area), { VodRows.sortMenu = false }) { i ->
        VodRows.sortMenu = false
        CatOrder.setMode(ctx, area, i)
    }
}
