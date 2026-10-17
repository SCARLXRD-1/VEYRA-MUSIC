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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.lastfm.LastfmState
import dev.sfg.veyra.mobile.ui.theme.CanopyColors

private val LastfmRed = Color(0xFFD51007)

@Composable
fun LastfmSettingsCard(
    state: LastfmState,
    onConnect: () -> Unit,
    onComplete: () -> Unit,
    onDisconnect: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Surface(color = Color.Transparent, shape = shape, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp).background(LastfmRed.copy(alpha = 0.16f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("L", color = LastfmRed, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Last.fm",
                        style =
                            MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CanopyColors.Text,
                    )
                    Text(
                        lastfmSubtitle(state),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state is LastfmState.Error) CanopyColors.Danger else CanopyColors.Muted,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            when (state) {
                LastfmState.SignedOut,
                is LastfmState.Error ->
                    IntegrationAction(stringResource(R.string.lastfm_connect), onClick = onConnect)
                LastfmState.Connecting ->
                    IntegrationAction(stringResource(R.string.lastfm_connecting), onClick = {})
                is LastfmState.Pending ->
                    IntegrationAction(stringResource(R.string.lastfm_finish_connection), onClick = onComplete)
                is LastfmState.Connected ->
                    IntegrationAction(
                        stringResource(R.string.lastfm_sign_out),
                        destructive = true,
                        onClick = onDisconnect,
                    )
            }
        }
    }
}

@Composable
private fun lastfmSubtitle(state: LastfmState): String =
    when (state) {
        LastfmState.SignedOut -> stringResource(R.string.lastfm_signed_out_desc)
        LastfmState.Connecting -> stringResource(R.string.lastfm_starting_auth)
        is LastfmState.Pending -> stringResource(R.string.lastfm_pending_desc)
        is LastfmState.Connected -> stringResource(R.string.lastfm_connected_as, state.user)
        is LastfmState.Error -> state.message
    }
