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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.auth.SupabaseSyncService
import dev.sfg.veyra.mobile.model.OrchardSettings
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import kotlinx.coroutines.launch

private val CloudAccent = Color(0xFF7B9FE8)

@Composable
fun VeyraAccountSettingsCard(
    settings: OrchardSettings? = null,
    onSettings: ((OrchardSettings) -> Unit)? = null,
) {
    val context = LocalContext.current
    val syncService = remember { SupabaseSyncService(context) }
    val scope = rememberCoroutineScope()

    var showAuthDialog by remember { mutableStateOf(false) }
    var isAuthLoading by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf(syncService.userEmail) }
    val isAuthenticated = userEmail.isNotBlank()

    val shape = RoundedCornerShape(20.dp)
    Surface(
        color = Color.Transparent,
        shape = shape,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(CloudAccent.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.CloudSync,
                        contentDescription = stringResource(R.string.acct_cloud_sync_cd),
                        tint = CloudAccent,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.acct_card_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        if (isAuthenticated) stringResource(R.string.acct_signed_in_as, userEmail)
                        else stringResource(R.string.acct_sync_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Transparency disclaimer
            Surface(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        Icons.Rounded.Info,
                        contentDescription = stringResource(R.string.acct_privacy_cd),
                        tint = CloudAccent,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.auth_supabase_disclaimer),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (settings != null && onSettings != null) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            stringResource(R.string.acct_cloud_analysis_title),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = CanopyColors.Text,
                        )
                        Text(
                            stringResource(R.string.acct_cloud_analysis_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = settings.bestMixSupabaseSync,
                        onCheckedChange = { onSettings(settings.copy(bestMixSupabaseSync = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = CloudAccent,
                            uncheckedThumbColor = CanopyColors.Muted,
                            uncheckedTrackColor = CanopyColors.Canvas,
                        ),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                if (isAuthenticated) {
                    OutlinedButton(
                        onClick = {
                            syncService.signOut()
                            userEmail = ""
                        },
                    ) {
                        Text(stringResource(R.string.action_sign_out), color = Color(0xFFFF5252))
                    }
                } else {
                    Button(
                        onClick = {
                            authError = ""
                            showAuthDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CloudAccent),
                    ) {
                        Text(stringResource(R.string.acct_sign_in_register))
                    }
                }
            }
        }
    }

    if (showAuthDialog) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var customUrl by remember { mutableStateOf(syncService.supabaseUrl) }
        var customKey by remember { mutableStateOf(syncService.anonKey) }
        var showCustomConfig by remember { mutableStateOf(!syncService.isConfigured()) }
        var authSuccessNotice by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { if (!isAuthLoading) showAuthDialog = false },
            title = { Text(stringResource(R.string.acct_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (authSuccessNotice.isNotBlank()) {
                        Text(authSuccessNotice, color = CanopyColors.Accent, style = MaterialTheme.typography.bodySmall)
                    }
                    if (authError.isNotBlank()) {
                        Text(authError, color = Color(0xFFFF5252), style = MaterialTheme.typography.bodySmall)
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(stringResource(R.string.acct_email_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(R.string.acct_password_label)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    TextButton(onClick = { showCustomConfig = !showCustomConfig }) {
                        Text(
                            if (showCustomConfig) stringResource(R.string.acct_hide_supabase_settings)
                            else stringResource(R.string.acct_custom_supabase_settings),
                        )
                    }

                    if (showCustomConfig) {
                        OutlinedTextField(
                            value = customUrl,
                            onValueChange = { customUrl = it },
                            label = { Text(stringResource(R.string.acct_supabase_url)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = customKey,
                            onValueChange = { customKey = it },
                            label = { Text(stringResource(R.string.acct_supabase_anon_key)) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customUrl.isNotBlank()) syncService.supabaseUrl = customUrl
                        if (customKey.isNotBlank()) syncService.anonKey = customKey

                        isAuthLoading = true
                        authError = ""
                        authSuccessNotice = ""
                        scope.launch {
                            val res = syncService.signIn(email, password)
                            isAuthLoading = false
                            if (res.isSuccess) {
                                userEmail = syncService.userEmail
                                showAuthDialog = false
                            } else {
                                authError = res.exceptionOrNull()?.message
                                    ?: context.getString(R.string.acct_sign_in_failed)
                            }
                        }
                    },
                    enabled = email.isNotBlank() && password.isNotBlank() && !isAuthLoading,
                ) {
                    if (isAuthLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    } else {
                        Text(stringResource(R.string.action_sign_in))
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (customUrl.isNotBlank()) syncService.supabaseUrl = customUrl
                        if (customKey.isNotBlank()) syncService.anonKey = customKey

                        isAuthLoading = true
                        authError = ""
                        authSuccessNotice = ""
                        scope.launch {
                            val res = syncService.signUp(email, password)
                            isAuthLoading = false
                            if (res.isSuccess) {
                                if (syncService.isAuthenticated()) {
                                    userEmail = syncService.userEmail
                                    showAuthDialog = false
                                } else {
                                    authSuccessNotice =
                                        context.getString(R.string.acct_account_created_msg)
                                }
                            } else {
                                authError = res.exceptionOrNull()?.message
                                    ?: context.getString(R.string.acct_sign_up_failed)
                            }
                        }
                    },
                    enabled = email.isNotBlank() && password.isNotBlank() && !isAuthLoading,
                ) {
                    Text(stringResource(R.string.acct_create_account))
                }
            },
        )
    }
}
