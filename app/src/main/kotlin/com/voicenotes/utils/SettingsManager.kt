package com.voicenotes.utils

import android.content.Context

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun getUrl(default: String): String = prefs.getString(KEY_URL, default) ?: default
    fun setUrl(url: String) { prefs.edit().putString(KEY_URL, url).apply() }

    fun getMode(): String = prefs.getString(KEY_MODE, MODE_ONLINE) ?: MODE_ONLINE
    fun setMode(mode: String) { prefs.edit().putString(KEY_MODE, mode).apply() }

    fun getLang(): String = prefs.getString(KEY_LANG, LANG_RU) ?: LANG_RU
    fun setLang(lang: String) { prefs.edit().putString(KEY_LANG, lang).apply() }

    fun isModelInstalled(base: String?, lang: String): Boolean {
        if (base == null) return false
        return java.io.File(base, "vosk/$lang").exists()
    }

    companion object {
        const val KEY_URL = "transcribe_url"
        const val KEY_MODE = "default_mode"
        const val KEY_LANG = "default_lang"
        const val MODE_ONLINE = "ONLINE"
        const val MODE_OFFLINE = "OFFLINE"
        const val LANG_RU = "ru"
        const val LANG_EN = "en"
    }
}