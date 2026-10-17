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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.Podcasts
import androidx.compose.material.icons.rounded.Sync
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.discord.DiscordAuthState
import dev.sfg.veyra.mobile.discord.GatewayConnectionState
import dev.sfg.veyra.mobile.model.OrchardSettings
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent

private val DiscordBlurple = Color(0xFF5865F2)

@Composable
fun DiscordSettingsCard(
    settings: OrchardSettings,
    discordAuth: DiscordAuthState,
    discordConnection: GatewayConnectionState,
    onSettings: (OrchardSettings) -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Surface(
        color = Color.Transparent,
        shape = shape,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            when (discordAuth) {
                is DiscordAuthState.SignedIn -> {
                    val account = discordAuth.session.account
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (!account?.avatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = account.avatarUrl,
                                contentDescription = account.displayName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(48.dp).clip(CircleShape),
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(DiscordBlurple, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    (account?.displayName?.take(1) ?: "D").uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                )
                            }
                        }

                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                account?.displayName ?: stringResource(R.string.disc_user_fallback),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = CanopyColors.Text,
                            )
                            val subtitle = if (account?.username != null && account.globalName != null) {
                                "@${account.username}"
                            } else {
                                "Discord"
                            }
                            Text(
                                subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = CanopyColors.Muted,
                            )
                        }

                    }

                    Spacer(Modifier.height(14.dp))
                    IntegrationAction(stringResource(R.string.disc_sign_out), destructive = true, onClick = onDisconnect)

                    Spacer(Modifier.height(16.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(CanopyColors.Rule)
                    )
                    Spacer(Modifier.height(12.dp))

                    DiscordToggleRow(
                        icon = Icons.Rounded.Podcasts,
                        title = stringResource(R.string.disc_share_presence),
                        subtitle = stringResource(R.string.disc_share_presence_desc),
                        checked = settings.discordPresenceEnabled,
                        onChecked = { onSettings(settings.copy(discordPresenceEnabled = it)) },
                    )

                    Spacer(Modifier.height(10.dp))
                    DiscordToggleRow(
                        icon = Icons.Rounded.Animation,
                        title = stringResource(R.string.settings_animated_artwork),
                        subtitle = stringResource(R.string.disc_animated_artwork_desc),
                        checked = settings.discordAnimatedArtwork,
                        enabled = settings.discordPresenceEnabled,
                        onChecked = { onSettings(settings.copy(discordAnimatedArtwork = it)) },
                    )
                }

                DiscordAuthState.Authorizing -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Sync,
                            contentDescription = null,
                            tint = LocalAccent.current,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            stringResource(R.string.disc_authorizing),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = CanopyColors.Text,
                        )
                    }
                }

                is DiscordAuthState.SignedOut, is DiscordAuthState.Error -> {
                    Text(
                        "Discord",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CanopyColors.Text,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        stringResource(R.string.disc_share_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CanopyColors.Muted,
                    )
                    if (discordAuth is DiscordAuthState.Error) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            discordAuth.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = CanopyColors.Danger,
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    IntegrationAction(stringResource(R.string.disc_sign_in), onClick = onConnect)
                }
            }
        }
    }
}

@Composable
private fun DiscordToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean = true,
    onChecked: (Boolean) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(CanopyColors.Canvas, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (enabled) LocalAccent.current else CanopyColors.Muted,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = if (enabled) CanopyColors.Text else CanopyColors.Muted,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = CanopyColors.Muted,
            )
        }
        Switch(
            checked = checked && enabled,
            enabled = enabled,
            onCheckedChange = onChecked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = LocalAccent.current,
                uncheckedThumbColor = CanopyColors.Muted,
                uncheckedTrackColor = CanopyColors.Canvas,
            ),
        )
    }
}
