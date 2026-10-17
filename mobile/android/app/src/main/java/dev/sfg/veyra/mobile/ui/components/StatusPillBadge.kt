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

package dev.sfg.veyra.mobile.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.mobile.ui.theme.CanopyColors

enum class StatusPillTone {
    ACTIVE,
    STREAMING,
    SYNC,
    PRIMARY,
    ACCENT,
    WARNING,
    COUNT,
    INFO,
    MUTED,
}

enum class StatusPillSize {
    SM,
    MD,
}

/**
 * Píldora de estado oficial de Veyra Music (Enterprise Slate & Obsidian Industrial).
 * Replica StatusPillBadge.vue de la versión desktop con micro-punto y borde hairline.
 */
@Composable
fun StatusPillBadge(
    label: String,
    modifier: Modifier = Modifier,
    status: StatusPillTone = StatusPillTone.INFO,
    dot: Boolean = true,
    pulse: Boolean = false,
    size: StatusPillSize = StatusPillSize.MD,
) {
    val bg = when (status) {
        StatusPillTone.ACTIVE, StatusPillTone.STREAMING -> Color(0x1F10B981)
        StatusPillTone.SYNC, StatusPillTone.ACCENT -> Color(0x1F38BDF8)
        StatusPillTone.PRIMARY -> Color(0x241E6CEB)
        StatusPillTone.WARNING -> Color(0x1FF59E0B)
        StatusPillTone.COUNT -> Color(0x0DFFFFFF)
        StatusPillTone.INFO -> CanopyColors.Surface
        StatusPillTone.MUTED -> Color(0x10FFFFFF)
    }

    val border = when (status) {
        StatusPillTone.ACTIVE, StatusPillTone.STREAMING -> Color(0x4210B981)
        StatusPillTone.SYNC, StatusPillTone.ACCENT -> Color(0x4738BDF8)
        StatusPillTone.PRIMARY -> Color(0x521E6CEB)
        StatusPillTone.WARNING -> Color(0x47F59E0B)
        StatusPillTone.COUNT -> Color(0x14FFFFFF)
        StatusPillTone.INFO -> CanopyColors.Rule
        StatusPillTone.MUTED -> CanopyColors.Rule
    }

    val textColor = when (status) {
        StatusPillTone.ACTIVE, StatusPillTone.STREAMING -> Color(0xFF34D399)
        StatusPillTone.SYNC, StatusPillTone.ACCENT -> Color(0xFF38BDF8)
        StatusPillTone.PRIMARY -> Color(0xFF60A5FA)
        StatusPillTone.WARNING -> Color(0xFFFBBF24)
        StatusPillTone.COUNT -> CanopyColors.Muted
        StatusPillTone.INFO -> CanopyColors.Text
        StatusPillTone.MUTED -> CanopyColors.Muted
    }

    val dotColor = when (status) {
        StatusPillTone.ACTIVE, StatusPillTone.STREAMING -> Color(0xFF10B981)
        StatusPillTone.SYNC, StatusPillTone.ACCENT -> Color(0xFF38BDF8)
        StatusPillTone.PRIMARY -> Color(0xFF1E6CEB)
        StatusPillTone.WARNING -> Color(0xFFF59E0B)
        StatusPillTone.COUNT -> CanopyColors.Muted
        StatusPillTone.INFO -> CanopyColors.SecondaryAccent
        StatusPillTone.MUTED -> CanopyColors.Muted
    }

    val dotAnim = if (pulse) {
        val infinite = rememberInfiniteTransition(label = "StatusDotPulse")
        val scale by infinite.animateFloat(
            initialValue = 1f,
            targetValue = 1.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "StatusDotScale",
        )
        scale
    } else 1f

    val padH = if (size == StatusPillSize.SM) 7.dp else 9.dp
    val padV = if (size == StatusPillSize.SM) 2.dp else 3.5.dp
    val fontSize = if (size == StatusPillSize.SM) 10.sp else 11.5.sp
    val dotSize = if (size == StatusPillSize.SM) 5.dp else 6.dp

    Row(
        modifier = modifier
            .background(bg, RoundedCornerShape(999.dp))
            .border(1.dp, border, RoundedCornerShape(999.dp))
            .padding(horizontal = padH, vertical = padV),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (dot) {
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .scale(dotAnim)
                    .background(dotColor, CircleShape),
            )
            Spacer(Modifier.width(if (size == StatusPillSize.SM) 4.5.dp else 6.dp))
        }
        Text(
            text = label,
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            lineHeight = fontSize,
            letterSpacing = 0.2.sp,
        )
    }
}
