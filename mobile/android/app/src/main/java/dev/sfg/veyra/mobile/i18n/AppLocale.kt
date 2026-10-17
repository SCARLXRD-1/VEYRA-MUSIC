/*
 * Copyright (C) 2026 AKASHI DEV
 *
 * This file is part of Veyra Music.
 *
 * Veyra Music es un fork de Orchard (https://github.com/SFG5453/Orchard),
 * mantenido únicamente para consumo personal y sin afiliación con el
 * proyecto original.
 *
 * Veyra Music is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Veyra Music is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Veyra Music. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.sfg.veyra.mobile.i18n

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Languages the interface can be forced into, independent of the system locale. */
enum class AppLanguage(val tag: String) {
    SYSTEM(""),
    ENGLISH("en"),
    SPANISH("es"),
}

/**
 * Persists and applies the in-app language. Stored in [android.content.SharedPreferences] because
 * the value must be resolvable synchronously while a context is being created.
 *
 * Switching language never recreates the activity: [language] exposes the current choice so the
 * Compose layer can re-provide a wrapped [Context] and swap the strings in place, avoiding the
 * blank-frame flash an activity recreation would show.
 */
object AppLocale {
    private const val PREFS = "veyra_locale"
    private const val KEY = "app_language"

    private val _language = MutableStateFlow(AppLanguage.SYSTEM)

    /** Current in-app language as observable state for the Compose layer. */
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    /** Loads the persisted choice into [language]; call once before composing. */
    fun seed(context: Context) {
        _language.value = read(context)
    }

    /** The persisted choice, resolved synchronously from storage. */
    fun current(context: Context): AppLanguage = read(context)

    private fun read(context: Context): AppLanguage {
        val stored = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, null)
        return AppLanguage.entries.firstOrNull { it.tag == stored } ?: AppLanguage.SYSTEM
    }

    /** Wraps [base] so its resources resolve to the persisted language. */
    fun wrap(base: Context): Context = localizedContext(base, read(base))

    /**
     * A context whose resources resolve to [language]. [base] may already carry a forced locale,
     * so the requested [language] always wins: [AppLanguage.SYSTEM] explicitly falls back to the
     * device locales rather than leaking a previously applied one.
     */
    fun localizedContext(base: Context, language: AppLanguage): Context {
        val config = Configuration(base.resources.configuration)
        config.setLocales(language.localeList())
        val localized = base.createConfigurationContext(config)
        return object : android.content.ContextWrapper(base) {
            override fun getResources(): android.content.res.Resources = localized.resources
            override fun getAssets(): android.content.res.AssetManager = localized.assets
        }
    }

    private fun AppLanguage.localeList(): LocaleList =
        if (this == AppLanguage.SYSTEM) {
            LocaleList.getDefault()
        } else {
            LocaleList(Locale(tag))
        }

    /** Persists [language] and publishes it so the UI updates without recreating the activity. */
    fun apply(context: Context, language: AppLanguage) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, language.tag)
            .apply()
        _language.value = language
    }
}
