/*
 * Copyright (C) 2026 SFG545
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

package dev.sfg.veyra.mobile.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Control rotatorio analógico háptico con diseño neumórfico.
 */
@Composable
fun NeumorphicKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    min: Float = -12f,
    max: Float = 6f,
    step: Float = 0.5f,
    defaultValue: Float = 0f,
    label: String = "",
    unit: String = "dB",
    size: Dp = 54.dp,
    disabled: Boolean = false,
) {
    val haptic = LocalHapticFeedback.current
    val range = (max - min).coerceAtLeast(0.001f)
    val clampedValue = value.coerceIn(min, max)
    val normalized = (clampedValue - min) / range

    // Recorrido de 270 grados (-135 a +135).
    val targetAngle = -135f + normalized * 270f
    val animatedAngle by animateFloatAsState(
        targetValue = targetAngle,
        animationSpec = tween(durationMillis = 60),
        label = "knob_rotation"
    )

    var accumulatedDelta by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Enchufe cóncavo exterior.
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color(0xFF090E18))
                .border(1.dp, Color.White.copy(alpha = 0.05f), CircleShape)
                .pointerInput(disabled, min, max, step) {
                    if (disabled) return@pointerInput
                    detectTapGestures(
                        onDoubleTap = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onValueChange(defaultValue)
                        }
                    )
                }
                .pointerInput(disabled, min, max, step) {
                    if (disabled) return@pointerInput
                    detectVerticalDragGestures(
                        onDragStart = {
                            isDragging = true
                            accumulatedDelta = 0f
                        },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false },
                        onVerticalDrag = { _, dragAmount ->
                            // Deslizar arriba aumenta ganancia.
                            val dragSensitivity = range / 300f
                            accumulatedDelta -= dragAmount * dragSensitivity

                            if (kotlin.math.abs(accumulatedDelta) >= step) {
                                val stepsCount = (accumulatedDelta / step).toInt()
                                val rawNewValue = (clampedValue + stepsCount * step).coerceIn(min, max)
                                val rounded = (rawNewValue / step).roundToInt() * step
                                if (rounded != clampedValue) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onValueChange(rounded)
                                }
                                accumulatedDelta -= stepsCount * step
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            // Dial convexo giratorio.
            val dialSize = (size - 8.dp).coerceAtLeast(16.dp)
            Box(
                modifier = Modifier
                    .size(dialSize)
                    .shadow(elevation = if (isDragging) 6.dp else 3.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                if (isDragging) Color(0xFF1F2C46) else Color(0xFF182338),
                                Color(0xFF0F1626)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = if (isDragging) CanopyColors.SecondaryAccent.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f),
                        shape = CircleShape
                    )
                    .rotate(animatedAngle),
                contentAlignment = Alignment.TopCenter,
            ) {
                // Muesca luminosa indicadora.
                Box(
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .width(3.dp)
                        .height(7.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.White, CanopyColors.SecondaryAccent)
                            )
                        )
                )
            }
        }

        if (label.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = CanopyColors.Muted,
            )
        }

        Spacer(Modifier.height(4.dp))

        // Lectura numérica en píldora.
        val formatted = if (clampedValue > 0f) {
            String.format(Locale.US, "+%.1f %s", clampedValue, unit)
        } else {
            String.format(Locale.US, "%.1f %s", clampedValue, unit)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFF090E18))
                .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(999.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = formatted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF8FAFC),
            )
        }
    }
}
