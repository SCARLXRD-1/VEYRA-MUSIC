/*
 * Copyright (C) 2026 SFG545 / Veyra Music (AKASHI DEV)
 * AGPL-3.0-or-later
 */

package dev.sfg.veyra.mobile.ui.tv

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

/**
 * CompositionLocal que indica si la interfaz se está renderizando en modo Smart TV (10-Foot UI).
 */
val LocalIsTv = compositionLocalOf { false }

object TvDetection {
    /**
     * Determina si el dispositivo físico actual es un televisor o dongle HDMI (Android TV / Google TV / Fire TV).
     */
    fun isTvDevice(context: Context): Boolean {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
        if (uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            return true
        }

        val pm = context.packageManager
        if (pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK) ||
            pm.hasSystemFeature("android.hardware.type.television")
        ) {
            return true
        }

        // Dispositivos sin pantalla táctil en orientación horizontal
        val isLandscape = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val hasTouch = pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)
        return !hasTouch && isLandscape
    }
}

/**
 * Hook composable para determinar si el modo TV está activo (por hardware o configuración forzada).
 */
@Composable
fun isTvMode(forceTvSetting: Boolean = false): Boolean {
    if (forceTvSetting) return true
    val context = LocalContext.current
    val config = LocalConfiguration.current
    return remember(context, config.orientation) {
        TvDetection.isTvDevice(context)
    }
}
