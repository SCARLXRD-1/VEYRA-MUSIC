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

package dev.sfg.veyra.mobile.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta de tema de Veyra Music Mobile: Enterprise Slate & Obsidian Industrial.
 * Superficies profundas anti-fatiga, acentos Azul Eléctrico (#1E6CEB), destello Sky Flare (#38BDF8)
 * y micro-bordes hairline.
 */
object CanopyColors {
    /** Main app background (Obsidian Deep). */
    val Chrome = Color(0xFF0B0F19)

    /** Secondary background / surface container low. */
    val Canvas = Color(0xFF0F1420)

    /** Card / container background (Slate Surface & Variant). */
    val Surface = Color(0xFF131B2B)
    val SurfaceHover = Color(0xFF1E293B)
    val ReadoutBg = Color(0xFF06080E)

    /** Borders and dividers (Hairline). */
    val Rule = Color(0xFFFFFFFF).copy(alpha = 0.08f)
    val RuleStrong = Color(0xFFFFFFFF).copy(alpha = 0.14f)

    /** Muted labels and subtitles. */
    val Eyebrow = Color(0xFF94A3B8)

    /** Primary brand accent (Azul Eléctrico Veyra). */
    val Accent = Color(0xFF1E6CEB)
    val AccentDeep = Color(0xFF124393)

    /** Secondary brand accent (Sky Flare). */
    val SecondaryAccent = Color(0xFF38BDF8)

    /** Text colors. */
    val Text = Color(0xFFF8FAFC)
    val Muted = Color(0xFF94A3B8)
    val MutedStrong = Color(0xFFCBD5E1)

    /** Semantic colors. */
    val Favorite = Color(0xFFFF4081)
    val LyricActive = Color(0xFF38BDF8)
    val ShimmerBackground = Color(0x33FFFFFF)
    val ShimmerLine = Color(0x66FFFFFF)

    /** Overlays and backdrop colors. */
    val Overlay = Color(0x66000000)
    val PlayerBackdrop = Color(0xFF0B0F19)

    /**
     * Frosted panes. [Glass] is the film laid over a pane's blurred backdrop.
     * [GlassChrome] is the darker film under bars.
     */
    val Glass = Color(0xFFE7ECF8)
    val GlassSecondary = Color(0xFFDCE4F0)
    val GlassChrome = Color(0xFF131B2B)
    val GlassOverlay = Color(0xFF0B0F19)

    /** Subtle architectural glass perimeter borders. */
    val GlassBorder = Color(0xFFFFFFFF).copy(alpha = 0.08f)
    val GlassBorderHighlight = Color(0xFFFFFFFF).copy(alpha = 0.18f)

    val Warning = Color(0xFFFFC46B)
    val Danger = Color(0xFFFF746D)
}

