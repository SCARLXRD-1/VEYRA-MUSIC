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

package dev.sfg.veyra.mobile.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

/**
 * La palabra Veyra Music se muestra mientras dos pistas se solapan.
 *
 * Shifting es la operación de injerto que esto realmente es: dos tallos separados unidos en
 * un punto elegido para que continúen como una sola planta. Además mantiene la lectura en el
 * vocabulario propio de Veyra Music en lugar de tomar prestado el de nadie más.
 */
const val TRANSITION_LABEL = "Shifting"

/** The hues the glow cycles through. Doubled ends so a mirrored tile joins seamlessly. */
private val RainbowStops =
        listOf(
                Color(0xFFFF3B5C),
                Color(0xFFFF9E2C),
                Color(0xFFFFE44D),
                Color(0xFF49E06B),
                Color(0xFF35D6E8),
                Color(0xFF4C7BFF),
                Color(0xFFB44CFF),
                Color(0xFFFF3B5C),
        )

/**
 * Eases the raw progress so the glow arrives and leaves smoothly instead of snapping on at the
 * transition boundary, and holds at full strength through the middle of the mix.
 */
@Composable
fun rememberTransitionGlow(progress: Float): Float {
    val target =
            when {
                progress <= 0f -> 0f
                progress < 0.25f -> progress / 0.25f
                else -> 1f
            }
    val eased by
            animateFloatAsState(
                    targetValue = target,
                    animationSpec = tween(420),
                    label = "TransitionGlow",
            )
    return eased
}

/**
 * A rainbow that slides sideways forever. Mirrored tiling means the span is independent of the
 * element it paints, so the same brush reads identically on a scrubber or a label.
 */
@Composable
fun rememberRainbowBrush(spanPx: Float = 520f, periodMs: Int = 3_200): Brush {
    val transition = rememberInfiniteTransition(label = "RainbowSweep")
    val phase by
            transition.animateFloat(
                    initialValue = 0f,
                    targetValue = spanPx * 2f,
                    animationSpec =
                            infiniteRepeatable(
                                    tween(periodMs, easing = LinearEasing),
                                    RepeatMode.Restart
                            ),
                    label = "RainbowSweepPhase",
            )
    return Brush.linearGradient(
            colors = RainbowStops,
            start = Offset(phase - spanPx, 0f),
            end = Offset(phase, 0f),
            tileMode = TileMode.Mirror,
    )
}
