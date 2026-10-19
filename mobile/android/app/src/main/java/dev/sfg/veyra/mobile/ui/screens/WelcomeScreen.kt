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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import dev.sfg.veyra.mobile.ui.scroll.orchardVerticalScroll as verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.mobile.auth.AuthState
import dev.sfg.veyra.mobile.model.OrchardSettings
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.tv.LocalIsTv
import dev.sfg.veyra.mobile.ui.tv.tvFocusable
import androidx.compose.ui.res.stringResource
import dev.sfg.veyra.connect.R

/**
 * Modern onboarding and welcome screen providing new and existing users with an intuitive
 * setup walkthrough for YouTube Music login, animated visual backdrops, crossfade transitions,
 * audio quality, and offline stream cache limits.
 */
@Composable
fun WelcomeScreen(
    settings: OrchardSettings,
    auth: AuthState,
    onUpdateSettings: (OrchardSettings) -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onFinish: () -> Unit,
    sessionQrPayload: String = "",
    sessionQrStatus: String = "",
    onStartSessionQr: () -> Unit = {},
    onCancelSessionQr: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    val firstFocusRequester = remember { FocusRequester() }
    val isTv = LocalIsTv.current

    LaunchedEffect(isTv) {
        if (isTv) firstFocusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CanopyColors.Chrome),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(36.dp))

            WelcomeHeroHeader()

            Spacer(Modifier.height(28.dp))

            WelcomeLanguageSection(firstFocusRequester)

            Spacer(Modifier.height(22.dp))

            WelcomeAuthSection(
                auth = auth,
                onSignIn = onSignIn,
                onSignOut = onSignOut,
            )

            if (isTv && auth !is AuthState.SignedIn) {
                WelcomeSessionQrSection(
                    qrPayload = sessionQrPayload,
                    status = sessionQrStatus,
                    onStart = onStartSessionQr,
                    onCancel = onCancelSessionQr,
                )
            }

            Spacer(Modifier.height(22.dp))

            WelcomeVisualsSection(
                settings = settings,
                onUpdateSettings = onUpdateSettings,
            )

            Spacer(Modifier.height(22.dp))

            WelcomeCrossfadeSection(
                settings = settings,
                onUpdateSettings = onUpdateSettings,
            )

            Spacer(Modifier.height(22.dp))

            WelcomeQualityAndStorageSection(
                settings = settings,
                onUpdateSettings = onUpdateSettings,
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onFinish,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalAccent.current,
                    contentColor = Color.Black,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .tvFocusable(
                        shape = CircleShape,
                        scale = 1.035f,
                        onClick = onFinish,
                    ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        stringResource(R.string.welcome_get_started),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
