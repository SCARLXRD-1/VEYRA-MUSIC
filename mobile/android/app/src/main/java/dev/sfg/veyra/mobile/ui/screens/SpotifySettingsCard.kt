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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.OrchardSettings
import dev.sfg.veyra.mobile.spotify.SpotifyCanvasRepository
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent

@Composable
fun SpotifySettingsCard(
    settings: OrchardSettings,
    onSettings: (OrchardSettings) -> Unit,
    onConnectSpotify: () -> Unit,
) {
    var showSpdcDialog by remember { mutableStateOf(false) }
    val isConnected = settings.spotifySpdc.isNotBlank()

    val shape = RoundedCornerShape(20.dp)
    Surface(
        color = Color.Transparent,
        shape = shape,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            SpotifyToggleRow(
                title = stringResource(R.string.spot_canvas_title),
                subtitle = stringResource(R.string.spot_canvas_subtitle),
                checked = settings.spotifyCanvasEnabled,
                onChecked = { onSettings(settings.copy(spotifyCanvasEnabled = it)) },
            )
            Spacer(Modifier.height(8.dp))
            if (isConnected) {
                IntegrationAction(stringResource(R.string.spot_sign_out), destructive = true) {
                    onSettings(settings.copy(spotifySpdc = ""))
                }
            } else {
                IntegrationAction(stringResource(R.string.spot_sign_in), onClick = onConnectSpotify)
                IntegrationAction(stringResource(R.string.spot_use_session_cookie)) { showSpdcDialog = true }
            }
        }
    }

    if (showSpdcDialog) {
        var input by remember { mutableStateOf(settings.spotifySpdc) }
        AlertDialog(
            onDismissRequest = { showSpdcDialog = false },
            title = { Text(stringResource(R.string.spot_spdc_dialog_title), color = CanopyColors.Text) },
            text = {
                Column {
                    Text(
                        stringResource(R.string.spot_spdc_dialog_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = CanopyColors.Muted,
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        placeholder = { Text(stringResource(R.string.spot_spdc_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val extracted = SpotifyCanvasRepository.extractSpdc(input)
                    onSettings(settings.copy(spotifySpdc = extracted))
                    showSpdcDialog = false
                }) {
                    Text(stringResource(R.string.action_save), color = LocalAccent.current)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSpdcDialog = false }) {
                    Text(stringResource(R.string.action_cancel), color = CanopyColors.Muted)
                }
            },
            containerColor = CanopyColors.Surface,
        )
    }
}

@Composable
private fun SpotifyToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = CanopyColors.Text,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = CanopyColors.Muted,
            )
        }
        Switch(
            checked = checked,
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
