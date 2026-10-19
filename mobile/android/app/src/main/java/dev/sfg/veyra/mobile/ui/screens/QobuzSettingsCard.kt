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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.qobuz.QobuzQuality
import dev.sfg.veyra.mobile.qobuz.QobuzStatus
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.theme.CanopyColors

val QobuzGold = Color(0xFFDFB15B)

@Composable
fun QobuzSettingsCard(
    status: QobuzStatus,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onEnabledChange: (Boolean) -> Unit,
    onQualityChange: (QobuzQuality) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isConnected = status.isConnected
    val shape = RoundedCornerShape(20.dp)

    Surface(
        color = Color.Transparent,
        shape = shape,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f).padding(end = 16.dp)) {
                    Text(
                        text = "Qobuz",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = CanopyColors.Text,
                    )
                    Text(
                        stringResource(R.string.qobuz_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = CanopyColors.Muted,
                    )
                }

                if (isConnected) {
                    Switch(
                        checked = status.enabled,
                        onCheckedChange = onEnabledChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = LocalAccent.current,
                            uncheckedThumbColor = CanopyColors.Muted,
                            uncheckedTrackColor = CanopyColors.SurfaceHover,
                        ),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Surface(
                color = Color.White.copy(alpha = 0.04f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Rounded.Info,
                        contentDescription = null,
                        tint = QobuzGold.copy(alpha = 0.85f),
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.qobuz_requires_subscription),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = CanopyColors.Muted,
                    )
                }
            }

            if (status.lastError.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = status.lastError,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.height(14.dp))

            if (!isConnected) {
                IntegrationAction(
                    label = stringResource(R.string.qobuz_sign_in),
                    icon = Icons.Rounded.Login,
                    accentColor = QobuzGold,
                    onClick = onConnect,
                )
            } else {
                // Quality options row
                Text(
                    text = stringResource(R.string.welcome_quality_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = CanopyColors.MutedStrong,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    QobuzQuality.entries.forEach { quality ->
                        val isSelected = quality == status.quality
                        val chipShape = RoundedCornerShape(10.dp)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(chipShape)
                                .background(
                                    if (isSelected) LocalAccent.current.copy(alpha = 0.20f)
                                    else CanopyColors.SurfaceHover
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) LocalAccent.current else Color.Transparent,
                                    chipShape
                                )
                                .clickable { onQualityChange(quality) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = quality.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) LocalAccent.current else CanopyColors.Text,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                IntegrationAction(
                    label = stringResource(R.string.qobuz_sign_out),
                    icon = Icons.Rounded.Logout,
                    destructive = true,
                    onClick = onDisconnect,
                )
            }
        }
    }
}
