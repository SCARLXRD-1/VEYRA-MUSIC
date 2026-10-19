/*
 * Copyright (C) 2026 SFG545
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

package dev.sfg.veyra.mobile

import dev.sfg.veyra.connect.BuildConfig
import dev.sfg.veyra.mobile.i18n.AppLocale

/** Changelog incluido y notas de la versión actual de Veyra Music Mobile (Español / Inglés). */
object MobileChangelog {
    const val CURRENT_VERSION = BuildConfig.VERSION_NAME
    const val CURRENT_CODENAME = BuildConfig.CODENAME

    val RELEASE_NOTES_ES =
        """
        ### Novedades
        - **Efecto Cristal Esmerilado Universal**: Compatibilidad total en toda la app desde Android 7.0 hasta Android 15, con renderizado neo-glassmórfico optimizado en Ajustes, Dispositivos, Biblioteca y Búsqueda.
        - **Chromecast con Identidad Veyra**: Animación de pulso elástico («Conectando con Veyra Music...»), isotipo oficial y píldora de estado de transmisión en vivo.
        - **Reproducción Secuencial de Descargas**: Al reproducir una canción descargada, la lista completa se encola en orden a partir de ella, con controles directos de «Reproducir todo» y «Aleatorio».
        - **Descarga Automática al Reproducir**: Nueva preferencia en Ajustes para descargar automáticamente al almacenamiento local cada pista reproducida.

        ### Cambios
        - **Experiencia Sonora y Visual**: Refinamiento estético en Obsidian Slate y Azul Eléctrico con indicador activo en canciones descargadas.
        - **Gestión de Cola Offline**: Protección de autoplay para no inyectar streaming de YouTube Music al reproducir descargas.
        - **Rendimiento de Renderizado**: Optimización de transiciones y degradados con aceleración por hardware.

        ### Correcciones
        - **Estabilidad de Glassmorphism**: Corrección de fallos en dispositivos sin soporte de shaders de desenfoque de hardware (Android 7.0–11).

        ### Mantenimiento
        - **Arquitectura de Audio**: Sincronización continua de motores WSOLA y modelos INT8 de seguimiento de ritmo.
        - **Localización Bilingüe**: Notas de versión y textos completamente adaptados en Español e Inglés.
        """
            .trimIndent()

    val RELEASE_NOTES_EN =
        """
        ### Added
        - **Universal Frosted Glass Effect**: Full compatibility across the app from Android 7.0 to Android 15, with optimized neo-glassmorphic rendering in Settings, Devices, Library, and Search.
        - **Veyra Identity Chromecast**: Smooth elastic pulse animation ("Connecting with Veyra Music..."), official glyph, and live streaming status pill.
        - **Sequential Offline Downloads Playback**: Tapping any downloaded track enqueues the complete downloaded songs list in order, with direct "Play All" and "Shuffle" controls.
        - **Auto-Download on Playback**: New setting in Audio preferences to automatically save streamed tracks to local storage.

        ### Changed
        - **Acoustic & Visual Experience**: Refined Obsidian Slate and Electric Blue styling with dynamic playing indicator on downloaded tracks.
        - **Offline Queue Management**: Autoplay guard prevents remote YouTube Music streams from interrupting offline playback.
        - **Rendering Performance**: Optimized pane transitions and gradients with hardware acceleration.

        ### Fixed
        - **Glassmorphism Stability**: Resolved blur shader compatibility on devices lacking hardware lens effects (Android 7.0–11).

        ### Maintenance
        - **Audio Architecture**: Seamless synchronization of WSOLA time-stretch engines and INT8 beat tracking models.
        - **Bilingual Localization**: Changelogs and interface elements dynamically adapted for English and Spanish.
        """
            .trimIndent()

    /** Retorna las notas de versión según el idioma seleccionado por el usuario. */
    fun getReleaseNotes(isSpanish: Boolean): String =
        if (isSpanish) RELEASE_NOTES_ES else RELEASE_NOTES_EN

    val CURRENT_RELEASE_NOTES: String
        get() = getReleaseNotes(AppLocale.isSpanish())
}
