/*
 * Copyright (C) 2026 SFG545 / AKASHI DEV
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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

/**
 * Conmutador táctil neumórfico para Veyra Music (Obsidian Industrial).
 * Canal empotrado profundo (#090D15) con disco convexo y micro-LED indicador Sky Flare (#38BDF8).
 */
@Composable
fun NeumorphicSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val haptic = LocalHapticFeedback.current
    val trackWidth = 50.dp
    val trackHeight = 28.dp
    val thumbSize = 22.dp
    val thumbPadding = 3.dp

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) trackWidth - thumbSize - thumbPadding else thumbPadding,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 550f),
        label = "NeumorphicSwitchThumbOffset",
    )

    val ledColor by animateColorAsState(
        targetValue = if (checked) Color(0xFF38BDF8) else Color(0xFF334155),
        animationSpec = tween(180),
        label = "NeumorphicSwitchLedColor",
    )

    val trackBorderColor by animateColorAsState(
        targetValue = if (checked) Color(0x3D38BDF8) else Color(0x1AFFFFFF),
        animationSpec = tween(200),
        label = "NeumorphicSwitchBorder",
    )

    Box(
        modifier = modifier
            .size(width = trackWidth, height = trackHeight)
            .clip(RoundedCornerShape(999.dp))
            .clickable(
                enabled = enabled && onCheckedChange != null,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCheckedChange?.invoke(!checked)
            }
            .drawBehind {
                // Canal empotrado cóncavo profundo
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF060910),
                            Color(0xFF0C1322),
                        )
                    )
                )
                // Sombra interior superior en bajorrelieve
                drawLine(
                    color = Color.Black.copy(alpha = 0.85f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2.dp.toPx(),
                )
            }
            .border(1.dp, trackBorderColor, RoundedCornerShape(999.dp)),
        contentAlignment = Alignment.CenterStart,
    ) {
        // Disco físico convexo móvil
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .shadow(elevation = 4.dp, shape = CircleShape, spotColor = Color.Black)
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF222F45),
                            Color(0xFF131B2B),
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x38FFFFFF),
                            Color(0x0A000000),
                        )
                    ),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            // Micro-LED indicador
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(ledColor)
                    .then(
                        if (checked) {
                            Modifier.drawBehind {
                                drawCircle(
                                    color = Color(0x6638BDF8),
                                    radius = size.minDimension * 1.6f,
                                )
                            }
                        } else Modifier
                    ),
            )
        }
    }
}
