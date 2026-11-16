package io.github.f_fm069.mozaik

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

/**
 * „Ignorieren“: Titel und Sender, die du nicht brauchst, verschwinden überall (Startseite, Filme, Serien, Live TV,
 * Suche), ohne die Playlist neu zu laden. Gruppiert wird nach der Kennung des Anbieters – „TR |“, „[EN]“, „DE:“ –
 * zuerst aus dem Kategorienamen, sonst aus dem Titel. Ergänzt den Sprachfilter, wo dessen Erkennung nicht reicht.
 * Schlüssel: Gruppe = "t:ART|KENNUNG" (ART = LIVE, MOVIE, SERIES), einzelne Kategorie = "g:ART|KATEGORIE".
 * Übernommen aus IPVIO Stream (GPL-3.0).
 */
object IgnoredGroups {
    private const val PREFS = "ipvio_ignored"
    @Volatile private var keys: Set<String> = emptySet()

    /** [keys] nach Art aufgeteilt, damit das Filtern pro Titel keine Texte zusammensetzen muss. */
    private class Index(val groups: Map<Kind, Set<String>>, val tags: Map<Kind, Set<String>>)
    @Volatile private var idx = Index(emptyMap(), emptyMap())

    private fun indexOf(s: Set<String>): Index {
        val g = HashMap<Kind, MutableSet<String>>()
        val t = HashMap<Kind, MutableSet<String>>()
        for (k in s) {
            val bar = k.indexOf('|')
            if (bar < 3 || k[1] != ':') continue
            val kind = runCatching { Kind.valueOf(k.substring(2, bar)) }.getOrNull() ?: continue
            val v = k.substring(bar + 1)
            when (k[0]) {
                'g' -> g.getOrPut(kind) { HashSet() }.add(v)
                't' -> t.getOrPut(kind) { HashSet() }.add(v)
            }
        }
        return Index(g, t)
    }

    /** Ändert sich bei jedem Umschalten (Auslöser, um die angezeigte Liste neu aufzubauen). */
    var version by mutableIntStateOf(0)
        private set

    val isEmpty: Boolean get() = keys.isEmpty()
    val size: Int get() = keys.size
    fun has(key: String) = key in keys

    /** Frühere eigene Datei für ausgeblendete Live-Kategorien; wird beim Einlesen hierher übernommen. */
    private const val LEGACY_LIVE = "ipvio_hidecats"

    fun init(c: Context) {
        var s = (Profiles.sp(c, PREFS).getString("k", "") ?: "").split("\n").filter { it.isNotBlank() }.toSet()
        // Migration: ausgeblendete Live-Kategorien aus der alten Datei übernehmen (auch nach dem Einspielen einer alten
        // Sicherung oder einem Profilwechsel). Erst neu speichern, dann die alte Liste leeren, damit nichts verloren geht.
        val legacy = Profiles.sp(c, LEGACY_LIVE)
        val old = legacy.getString("k", "")
        if (!old.isNullOrBlank()) {
            s = CatFilterLogic.migrate(old, s)
            Profiles.sp(c, PREFS).edit().putString("k", s.joinToString("\n")).commit()
            legacy.edit().putString("k", "").apply()
        }
        if (s != keys) { keys = s; idx = indexOf(s); version++ }
    }

    private fun save(c: Context, s: Set<String>) {
        keys = s; idx = indexOf(s)
        Profiles.sp(c, PREFS).edit().putString("k", s.joinToString("\n")).apply()
        version++
    }

    fun set(c: Context, key: String, ignored: Boolean) = save(c, if (ignored) keys + key else keys - key)
    fun setMany(c: Context, add: Collection<String>, ignored: Boolean) = save(c, if (ignored) keys + add else keys - add.toSet())
    fun clear(c: Context) = save(c, emptySet())

    /** Aktuelle Schlüssel (unveränderliche Kopie), z. B. als Startpunkt eines Entwurfs in den Einstellungen. */
    fun snapshot(): Set<String> = keys

    /** Übernimmt einen Entwurf in einem Schritt: ein Speichern und eine Versionsänderung, egal wie viele Schalter. */
    fun applyDraft(c: Context, add: Set<String>, remove: Set<String>) {
        if (add.isEmpty() && remove.isEmpty()) return
        save(c, keys - remove + add)
    }

    private val START = Regex("""^\W*([A-Za-z]{2,5})\s*(?:[|:\]\)]|[-–]\s)""")
    private val END = Regex("""[\[(|]\s*([A-Za-z]{2,4})\s*[\])|]?\s*$""")

    /** Kennung wie „TR“ oder „EN“ aus einem Text (Anfang, sonst Ende) oder null. */
    fun tagIn(s: String): String? =
        START.find(s)?.groupValues?.get(1)?.uppercase() ?: END.find(s)?.groupValues?.get(1)?.uppercase()

    /** Kennung eines Eintrags: zuerst die Kategorie, sonst der Titel. Leer = keine Kennung. */
    fun tagOf(i: Item): String = tagIn(i.group) ?: tagIn(i.title) ?: ""

    fun bundleKey(kind: Kind, tag: String) = "t:" + kind.name + "|" + tag
    fun groupKey(kind: Kind, group: String) = "g:" + kind.name + "|" + group

    /** Kategorienamen einer Art, die einzeln ausgeblendet sind (für „Ausgeblendet“ in Filme und Serien). */
    fun hiddenGroups(kind: Kind): List<String> = CatFilterLogic.hidden(keys, kind)

    fun isIgnored(i: Item): Boolean {
        if (keys.isEmpty()) return false
        val x = idx
        if (x.groups[i.kind]?.contains(i.group) == true) return true
        val tags = x.tags[i.kind] ?: return false
        val t = tagOf(i)
        return t.isNotEmpty() && t in tags
    }

    /**
     * Gleiches Ergebnis wie das Filtern mit [isIgnored], aber schneller bei großen Playlists: Der Ausdruck für die
     * Kennung läuft nur für Arten mit ignorierten Gruppen, und einmal pro Kategorie statt einmal pro Titel.
     */
    fun filter(l: List<Item>): List<Item> {
        if (keys.isEmpty()) return l
        val x = idx
        val groupTag = HashMap<String, String>()
        return l.filter { i ->
            if (x.groups[i.kind]?.contains(i.group) == true) return@filter false
            val tags = x.tags[i.kind] ?: return@filter true
            val t = groupTag.getOrPut(i.group) { tagIn(i.group) ?: "" }.ifEmpty { tagIn(i.title) ?: "" }
            !(t.isNotEmpty() && t in tags)
        }
    }
}
