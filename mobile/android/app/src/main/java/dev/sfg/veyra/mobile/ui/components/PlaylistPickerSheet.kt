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


package dev.sfg.veyra.mobile.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import dev.sfg.veyra.mobile.ui.scroll.OrchardLazyColumn as LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.Playlist
import dev.sfg.veyra.mobile.model.Track
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent

/** Selects one of the user's saved playlists, keeping duplicate adds unavailable. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaylistPickerSheet(
    track: Track,
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onSelect: (Playlist) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = CanopyColors.Surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = CanopyColors.Muted.copy(alpha = 0.4f)) },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 28.dp),
        ) {
            item {
                Text(stringResource(R.string.act_add_to_playlist), style = MaterialTheme.typography.titleLarge, color = CanopyColors.Text)
                Text(
                    track.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CanopyColors.Muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(14.dp))
            }
            if (playlists.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.picker_no_playlists),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CanopyColors.Muted,
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                }
            } else {
                items(playlists, key = { it.id }) { playlist ->
                    val containsTrack = playlist.tracks.any { it.id == track.id }
                    Surface(
                        onClick = { if (!containsTrack) onSelect(playlist) },
                        enabled = !containsTrack,
                        color = Color.Transparent,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ArtworkTile(playlist.artworkUrl, playlist.title, Modifier.size(42.dp), 8)
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(playlist.title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), color = CanopyColors.Text)
                                Text(
                                    if (containsTrack) stringResource(R.string.picker_already_added) else playlist.author,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CanopyColors.Muted,
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Rounded.PlaylistAdd,
                                contentDescription = null,
                                tint = if (containsTrack) CanopyColors.Muted else LocalAccent.current,
                            )
                        }
                    }
                }
            }
        }
    }
}
