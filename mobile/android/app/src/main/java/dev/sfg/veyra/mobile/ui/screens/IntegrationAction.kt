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

package dev.sfg.veyra.mobile.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent

/**
 * Botón de acción con estilo industrial Obsidian Slate y squircle para integraciones y ajustes.
 */
@Composable
internal fun IntegrationAction(
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accentColor: Color? = null,
    subtitle: String? = null,
    destructive: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val actionShape = RoundedCornerShape(12.dp)
    val effectiveAccent = accentColor ?: LocalAccent.current
    val surfaceColor = when {
        !enabled -> Color(0xFF131B2B).copy(alpha = 0.35f)
        destructive -> Color(0xFFFF5252).copy(alpha = 0.08f)
        else -> Color(0xFF131B2B).copy(alpha = 0.65f)
    }
    val borderColor = when {
        !enabled -> Color.White.copy(alpha = 0.04f)
        destructive -> Color(0xFFFF5252).copy(alpha = 0.22f)
        else -> Color.White.copy(alpha = 0.08f)
    }
    val contentColor = when {
        !enabled -> CanopyColors.Muted
        destructive -> Color(0xFFFF6B6B)
        else -> CanopyColors.Text
    }
    val iconTint = if (destructive) Color(0xFFFF6B6B) else effectiveAccent

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.982f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 550f),
        label = "IntegrationActionScale",
    )
    val animatedElevation by animateFloatAsState(
        targetValue = if (isPressed && enabled) 1f else 3f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 500f),
        label = "IntegrationActionElevation",
    )

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        enabled = enabled,
        interactionSource = interactionSource,
        shape = actionShape,
        color = surfaceColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = animatedElevation.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            },
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false),
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (destructive) Color(0xFFFF5252).copy(alpha = 0.14f)
                                else iconTint.copy(alpha = 0.12f),
                                RoundedCornerShape(8.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = if (enabled) iconTint else CanopyColors.Muted,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                }
                Column {
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = contentColor,
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = CanopyColors.Muted,
                        )
                    }
                }
            }
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = when {
                    !enabled -> Color.White.copy(alpha = 0.15f)
                    destructive -> Color(0xFFFF6B6B).copy(alpha = 0.45f)
                    else -> Color.White.copy(alpha = 0.35f)
                },
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
