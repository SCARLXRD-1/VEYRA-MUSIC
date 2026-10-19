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
        - **Rediseño Neumórfico Industrial en Ajustes**: Integraciones de Spotify Canvas, ListenBrainz, Qobuz, Last.fm, Discord y Cuenta en tarjetas squircle Obsidian Slate con bordes hairline y campos de entrada diseñados con foco en Azul Eléctrico.
        - **Micro-interacciones y Animaciones Táctiles**: Compresión elástica con físicas spring, respuesta háptica, auras de elevación activas en selectores segmentados y avance suave de indicadores chevron.
        - **Efecto Cristal Esmerilado Universal**: Renderizado neo-glassmórfico optimizado en Ajustes, Dispositivos, Biblioteca y Búsqueda.
        - **Modo TV y Experiencia Ampliada**: Integración y refinamiento de navegación por foco D-Pad para pantallas grandes y dispositivos de sala.
        - **Chromecast más estable**: Mantiene la sesión durante reconexiones breves, prioriza la red Wi‑Fi local y evita cortar streams de audio largos por tiempos de espera HTTP.

        ### Cambios
        - **Paridad Estética Desktop y Mobile**: Unificación del sistema de diseño Enterprise Slate & Obsidian Industrial en diálogos, formularios y componentes de control.
        - **Fluidez y Rendimiento de Renderizado**: Optimización de transiciones y capas gráficas aceleradas por hardware en Compose.
        - **Gestión Avanzada de Cola**: Sincronización continua de cola y protección de autoplay.

        ### Correcciones
        - **Preservación Total de Metadatos en Cola**: Resuelto el problema donde las pistas encoladas o del historial aparecían como «Artista desconocido» o títulos vacíos mediante caché en memoria y persistencia protegida contra el truncado IPC de Jetpack Media3.

        ### Mantenimiento
        - **Arquitectura de Audio y DSP**: Sincronización continua de motores WSOLA y modelos INT8 de seguimiento de ritmo.
        - **Localización Bilingüe**: Textos y notas de versión completamente adaptados en Español e Inglés.
        """
            .trimIndent()

    val RELEASE_NOTES_EN =
        """
        ### Added
        - **Industrial Neumorphic Redesign in Settings**: Spotify Canvas, ListenBrainz, Qobuz, Last.fm, Discord, and Account actions redesigned as Obsidian Slate squircle cards with hairline borders, branded icon badges, and Electric Blue focused input fields.
        - **Tactile Micro-interactions & Animations**: Spring physics tactile compression, haptic feedback, active elevation glow on segmented selectors, and smooth spring forward chevrons.
        - **Universal Frosted Glass Effect**: Optimized neo-glassmorphic rendering across Settings, Devices, Library, and Search.
        - **TV Mode & Living Room Experience**: Refined D-Pad focus navigation and responsive layouts for big screens.
        - **More Stable Chromecast**: Preserves the Cast session through brief reconnects, prioritizes the local Wi-Fi network, and prevents long audio streams from timing out.

        ### Changed
        - **Desktop & Mobile Aesthetic Parity**: Seamless alignment with the Enterprise Slate & Obsidian Industrial design system across dialogs, forms, and control panels.
        - **Rendering Fluidity & Performance**: Hardware-accelerated graphicsLayer optimizations for silky smooth Compose animations.
        - **Advanced Queue Management**: Continuous queue synchronization and autoplay protection.

        ### Fixed
        - **Full Queue Metadata Preservation**: Completely resolved the issue causing queued and history tracks to lose their titles or display "Unknown Artist" with in-memory caching and persistence guarded against Jetpack Media3 IPC binder truncation.

        ### Maintenance
        - **Audio Architecture & DSP**: Seamless synchronization of WSOLA time-stretch engines and INT8 beat tracking models.
        - **Bilingual Localization**: Changelogs and interface elements dynamically adapted for English and Spanish.
        """
            .trimIndent()

    /** Retorna las notas de versión según el idioma seleccionado por el usuario. */
    fun getReleaseNotes(isSpanish: Boolean): String =
        if (isSpanish) RELEASE_NOTES_ES else RELEASE_NOTES_EN

    val CURRENT_RELEASE_NOTES: String
        get() = getReleaseNotes(AppLocale.isSpanish())
}
