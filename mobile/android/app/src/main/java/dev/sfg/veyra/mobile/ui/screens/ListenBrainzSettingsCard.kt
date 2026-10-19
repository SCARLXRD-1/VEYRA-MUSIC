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

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.listenbrainz.ListenBrainzState
import dev.sfg.veyra.mobile.ui.theme.CanopyColors

private val ListenBrainzOrange = Color(0xFFEB743B)

@Composable
fun ListenBrainzSettingsCard(
    state: ListenBrainzState,
    onConnect: (String) -> Unit,
    onDisconnect: () -> Unit,
) {
    var showTokenDialog by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(20.dp)
    Surface(color = Color.Transparent, shape = shape, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp)
                        .background(ListenBrainzOrange.copy(alpha = 0.16f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "B",
                        color = ListenBrainzOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "ListenBrainz",
                        style =
                            MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CanopyColors.Text,
                    )
                    Text(
                        listenBrainzSubtitle(state),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state is ListenBrainzState.Error) CanopyColors.Danger else CanopyColors.Muted,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            when (state) {
                ListenBrainzState.SignedOut,
                is ListenBrainzState.Error ->
                    IntegrationAction(
                        label = stringResource(R.string.lbz_connect),
                        icon = Icons.Rounded.Link,
                        accentColor = ListenBrainzOrange,
                        onClick = { showTokenDialog = true },
                    )
                ListenBrainzState.Validating ->
                    IntegrationAction(
                        label = stringResource(R.string.lbz_validating),
                        icon = Icons.Rounded.Sync,
                        accentColor = ListenBrainzOrange,
                        enabled = false,
                        onClick = {},
                    )
                is ListenBrainzState.Connected ->
                    IntegrationAction(
                        label = stringResource(R.string.lbz_sign_out),
                        icon = Icons.Rounded.Logout,
                        destructive = true,
                        onClick = onDisconnect,
                    )
            }
        }
    }

    if (showTokenDialog) {
        var token by remember { mutableStateOf("") }
        val dialogShape = RoundedCornerShape(24.dp)
        AlertDialog(
            onDismissRequest = { showTokenDialog = false },
            shape = dialogShape,
            containerColor = Color(0xFF131B2B),
            title = {
                Text(
                    stringResource(R.string.lbz_token_dialog_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CanopyColors.Text,
                )
            },
            text = {
                Column {
                    Text(
                        stringResource(R.string.lbz_token_dialog_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = CanopyColors.Muted,
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = token,
                        onValueChange = { token = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0B0F19),
                            unfocusedContainerColor = Color(0xFF0B0F19),
                            focusedBorderColor = ListenBrainzOrange,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedTextColor = CanopyColors.Text,
                            unfocusedTextColor = CanopyColors.Text,
                            cursorColor = ListenBrainzOrange,
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = token.isNotBlank(),
                    onClick = {
                        onConnect(token)
                        showTokenDialog = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ListenBrainzOrange,
                        disabledContainerColor = ListenBrainzOrange.copy(alpha = 0.35f),
                    ),
                ) {
                    Text(
                        stringResource(R.string.lbz_connect_confirm),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showTokenDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                ) {
                    Text(stringResource(R.string.action_cancel), color = CanopyColors.Muted)
                }
            },
        )
    }
}

@Composable
private fun listenBrainzSubtitle(state: ListenBrainzState): String =
    when (state) {
        ListenBrainzState.SignedOut -> stringResource(R.string.lbz_signed_out_desc)
        ListenBrainzState.Validating -> stringResource(R.string.lbz_checking_token)
        is ListenBrainzState.Connected -> stringResource(R.string.lbz_connected_as, state.user)
        is ListenBrainzState.Error -> state.message
    }
