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

package dev.sfg.veyra.mobile.ui.modifiers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.mobile.ui.theme.CanopyColors

/**
 * Modificadores neumórficos táctiles para Veyra Music Mobile.
 * Proyectan luces biseladas y sombras ocluidas con alta eficiencia de GPU.
 */

/**
 * Aplica una superficie neumórfica con relieve tridimensional y borde hairline.
 */
fun Modifier.neumorphicSurface(
    shape: Shape = RoundedCornerShape(14.dp),
    backgroundColor: Color = CanopyColors.Surface,
    lightColor: Color = Color.White.copy(alpha = 0.08f),
    darkShadowColor: Color = Color.Black.copy(alpha = 0.65f),
    borderHighlight: Color = Color.White.copy(alpha = 0.10f),
    isPressed: Boolean = false,
): Modifier = this
    .clip(shape)
    .drawBehind {
        val width = size.width
        val height = size.height

        if (isPressed) {
            // Bajorrelieve cóncavo (pulsado)
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(darkShadowColor, backgroundColor),
                    center = Offset(width * 0.5f, height * 0.5f),
                    radius = maxOf(width, height) * 0.7f
                )
            )
            // Sombra interior en bordes superiores
            drawLine(
                color = Color.Black.copy(alpha = 0.5f),
                start = Offset(0f, 0f),
                end = Offset(width, 0f),
                strokeWidth = 2.dp.toPx()
            )
        } else {
            // Relieve convexo (reposo)
            drawRect(color = backgroundColor)

            // Luz suave superior izquierda
            drawLine(
                color = lightColor,
                start = Offset(0f, 0f),
                end = Offset(width, 0f),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = lightColor,
                start = Offset(0f, 0f),
                end = Offset(0f, height),
                strokeWidth = 1.dp.toPx()
            )

            // Borde hairline perimetral
            drawRect(
                color = borderHighlight,
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }

/**
 * Canal tallado en bajorrelieve (chiseled slot) para sliders y barras de avance.
 */
fun Modifier.chiseledSlot(
    shape: Shape = RoundedCornerShape(999.dp),
    slotColor: Color = Color(0xFF060910),
    innerShadowColor: Color = Color.Black.copy(alpha = 0.75f)
): Modifier = this
    .clip(shape)
    .drawBehind {
        drawRect(color = slotColor)
        // Micro-sombra superior del canal
        drawLine(
            color = innerShadowColor,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 1.5.dp.toPx()
        )
    }

/**
 * Microinteracción táctil con compresión elástica y vibración háptica al pulsar.
 */
@Composable
fun Modifier.tactilePress(
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    scaleDown: Float = 0.95f,
    enabled: Boolean = true
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 600f),
        label = "tactilePressScale"
    )
    val haptic = LocalHapticFeedback.current

    return this
        .scale(animatedScale)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled
        ) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        }
}
