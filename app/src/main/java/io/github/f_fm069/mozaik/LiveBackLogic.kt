package io.github.f_fm069.mozaik

/** Was „Zurück“ in Live TV tut (ohne Android, testbar). */
object LiveBackLogic {
    enum class Action {
        /** Verschiebe-Modus: Kategorie absetzen. */
        DropMoved,
        /** „Sender hinzufügen“ beenden. */
        FinishAdd,
        /** Aus der Senderliste zurück zur gewählten Kategorie. */
        ToCategories,
        /** Nichts Eigenes: die App entscheidet (Seitenleiste, Startseite). */
        Default
    }

    /** Verschiebe-Modus und „Sender hinzufügen“ haben Vorrang; die Senderliste führt erst danach zur Kategorie. */
    fun action(moving: Boolean, adding: Boolean, inChannels: Boolean): Action = when {
        moving -> Action.DropMoved
        adding -> Action.FinishAdd
        inChannels -> Action.ToCategories
        else -> Action.Default
    }

    /**
     * Welche Kategoriezeile den Fokus bekommt: die gewählte [sel], wenn es die Zeile gibt und sie eine echte Kategorie ist.
     * Sonst „Alle Sender“ (z. B. wenn die gewählte Kategorie gerade ausgeblendet wurde), sonst die erste echte Zeile.
     */
    fun focusRow(names: List<String>, sel: String): String? {
        fun real(n: String) = n !in SPECIAL && n != HIDDEN_CAT
        if (sel in names && real(sel)) return sel
        if ("ALL" in names) return "ALL"
        return names.firstOrNull { real(it) }
    }

    /** Zeilen, die keine Kategorie auswählen (Playlist, Guide, neue Liste). */
    private val SPECIAL = setOf("PL", "GUIDE", "NEW", "SRC")

    /** Welcher Sender beim Zurückkommen aus dem Player markiert wird: der zuletzt gesehene, sonst der erste der Liste. */
    fun restoreIndex(ids: List<String>, lastId: String?): Int {
        if (ids.isEmpty()) return -1
        val i = if (lastId == null) -1 else ids.indexOf(lastId)
        return if (i >= 0) i else 0
    }
}
