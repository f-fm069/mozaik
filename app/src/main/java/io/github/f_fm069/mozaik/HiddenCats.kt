package io.github.f_fm069.mozaik

import android.content.Context
import androidx.compose.runtime.mutableStateListOf

/**
 * Ausgeblendete Live-TV-Kategorien (je Profil). Sie verschwinden aus der Kategorienliste und lassen sich über
 * „Ausgeblendet“ zurückholen. Übernommen aus IPVIO Stream (GPL-3.0).
 */
object HiddenCats {
    val list = mutableStateListOf<String>()
    private var inited = false
    private fun prefs(c: Context) = Profiles.sp(c, "ipvio_hidecats")

    fun init(c: Context) {
        if (inited) return
        inited = true
        list.addAll((prefs(c).getString("k", "") ?: "").split("\n").filter { it.isNotBlank() })
    }

    /** Nach einem Profilwechsel oder dem Zurücksetzen neu einlesen. */
    fun reload(c: Context) {
        inited = false
        list.clear()
        init(c)
    }

    private fun save(c: Context) = prefs(c).edit().putString("k", list.joinToString("\n")).apply()

    fun hide(c: Context, name: String) { init(c); if (name !in list) list.add(name); save(c) }
    fun show(c: Context, name: String) { init(c); list.remove(name); save(c) }
    fun showAll(c: Context) { init(c); list.clear(); save(c) }
}
