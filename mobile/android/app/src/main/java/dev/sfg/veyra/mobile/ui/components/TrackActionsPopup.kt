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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SkipNext
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.Track
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent

/** Bottom sheet popup shown when tapping the "..." button on a track row. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TrackActionsPopup(
    track: Track,
    onDismiss: () -> Unit,
    // Null where the action does not apply; the now playing player already plays this track,
    // so it offers no "Play Now" but does offer the queue.
    onPlay: (() -> Unit)? = null,
    onPlayNext: (() -> Unit)? = null,
    onAddToQueue: (() -> Unit)? = null,
    onAddToPlaylist: (() -> Unit)? = null,
    onRemoveFromPlaylist: (() -> Unit)? = null,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null,
    onViewQueue: (() -> Unit)? = null,
    onDownload: (() -> Unit)? = null,
    onRemoveDownload: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null,
    onViewAlbum: (() -> Unit)? = null,
    onViewArtist: (() -> Unit)? = null,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = CanopyColors.Surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = CanopyColors.Muted.copy(alpha = 0.4f)) },
    ) {
        Column(Modifier.padding(horizontal = 20.dp).padding(bottom = 28.dp)) {
            // Track preview header
            Row(verticalAlignment = Alignment.CenterVertically) {
                ArtworkTile(track.artworkUrl, track.title, Modifier.size(52.dp), 10)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            track.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = CanopyColors.Text,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        if (track.explicit) {
                            Spacer(Modifier.width(6.dp))
                            ExplicitBadge()
                        }
                    }
                    if (track.artist.isNotBlank() && !track.artist.equals(dev.sfg.veyra.mobile.ui.components.UNKNOWN_ARTIST_SENTINEL, ignoreCase = true)) {
                        Text(
                            dev.sfg.veyra.mobile.ui.components.localizedArtistName(track.artist),
                            style = MaterialTheme.typography.bodyMedium,
                            color = CanopyColors.Muted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Box(Modifier.fillMaxWidth().height(0.5.dp).background(CanopyColors.Rule))
            Spacer(Modifier.height(6.dp))

            // Action items
            onPlay?.let { action ->
                PopupActionRow(Icons.Rounded.PlayArrow, stringResource(R.string.act_play_now)) { onDismiss(); action() }
            }
            onPlayNext?.let { action ->
                PopupActionRow(Icons.Rounded.SkipNext, stringResource(R.string.act_play_next)) { onDismiss(); action() }
            }
            onAddToQueue?.let { action ->
                PopupActionRow(Icons.AutoMirrored.Rounded.PlaylistAdd, stringResource(R.string.act_add_to_queue)) { onDismiss(); action() }
            }
            onAddToPlaylist?.let { action ->
                PopupActionRow(Icons.AutoMirrored.Rounded.PlaylistAdd, stringResource(R.string.act_add_to_playlist)) { onDismiss(); action() }
            }
            onRemoveFromPlaylist?.let { action ->
                PopupActionRow(Icons.Rounded.Delete, stringResource(R.string.act_remove_from_playlist)) { onDismiss(); action() }
            }
            onMoveUp?.let { action ->
                PopupActionRow(Icons.Rounded.KeyboardArrowUp, stringResource(R.string.act_move_up)) { onDismiss(); action() }
            }
            onMoveDown?.let { action ->
                PopupActionRow(Icons.Rounded.KeyboardArrowDown, stringResource(R.string.act_move_down)) { onDismiss(); action() }
            }
            onViewQueue?.let { action ->
                PopupActionRow(Icons.AutoMirrored.Rounded.List, stringResource(R.string.nav_queue)) { onDismiss(); action() }
            }
            onDownload?.let { action ->
                PopupActionRow(Icons.Rounded.Download, stringResource(R.string.action_download)) { onDismiss(); action() }
            }
            onRemoveDownload?.let { action ->
                PopupActionRow(Icons.Rounded.Delete, stringResource(R.string.action_delete)) { onDismiss(); action() }
            }
            onViewAlbum?.let { action ->
                if (track.albumId.isNotBlank()) {
                    PopupActionRow(Icons.Rounded.Album, stringResource(R.string.act_view_album)) { onDismiss(); action() }
                }
            }
            onViewArtist?.let { action ->
                PopupActionRow(Icons.Rounded.Person, stringResource(R.string.act_view_artist)) { onDismiss(); action() }
            }
            onShare?.let { action ->
                PopupActionRow(Icons.Rounded.Share, stringResource(R.string.action_share)) { onDismiss(); action() }
            }
        }
    }
}

@Composable
private fun PopupActionRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = LocalAccent.current, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(16.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = CanopyColors.Text,
            )
        }
    }
}
