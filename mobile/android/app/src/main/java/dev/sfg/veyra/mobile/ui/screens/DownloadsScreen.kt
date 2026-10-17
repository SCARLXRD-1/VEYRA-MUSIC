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
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import dev.sfg.veyra.mobile.ui.scroll.OrchardLazyColumn as LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.download.DownloadItem
import dev.sfg.veyra.mobile.download.DownloadStatus
import dev.sfg.veyra.mobile.model.Track
import dev.sfg.veyra.mobile.ui.components.ArtworkTile
import dev.sfg.veyra.mobile.ui.components.ExplicitBadge
import dev.sfg.veyra.mobile.ui.components.MessagePanel
import dev.sfg.veyra.mobile.ui.components.OrchardChromeHeight
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent

@Composable
fun DownloadsScreen(
    downloads: List<DownloadItem>,
    totalBytesUsed: Long,
    onPlay: (Track) -> Unit,
    onRemoveDownload: (String) -> Unit,
) {
    val completed = downloads.filter { it.status == DownloadStatus.COMPLETED }
    val active = downloads.filter { it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.QUEUED }
    val failed = downloads.filter { it.status == DownloadStatus.FAILED }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = OrchardChromeHeight),
    ) {
        item {
            Column(Modifier.padding(horizontal = 16.dp).padding(top = 16.dp, bottom = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        stringResource(R.string.library_filter_downloads),
                        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                        color = CanopyColors.Text,
                    )
                    if (completed.isNotEmpty()) {
                        dev.sfg.veyra.mobile.ui.components.StatusPillBadge(
                            label = "${completed.size} LISTAS",
                            status = dev.sfg.veyra.mobile.ui.components.StatusPillTone.ACTIVE,
                            pulse = active.isNotEmpty(),
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(R.string.library_offline_tracks_used, formatStorageSize(totalBytesUsed)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CanopyColors.Muted,
                )
            }
        }

        if (active.isNotEmpty()) {
            item {
                Text(
                    stringResource(R.string.library_downloading_count, active.size),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = LocalAccent.current,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            items(active, key = { "active_${it.track.id}" }) { item ->
                DownloadingRow(item = item, onCancel = { onRemoveDownload(item.track.id) })
            }
        }

        if (completed.isNotEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.library_downloaded_count, completed.size),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = CanopyColors.Muted,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            items(completed, key = { "completed_${it.track.id}" }) { item ->
                DownloadedTrackRow(
                    item = item,
                    onPlay = { onPlay(item.track) },
                    onDelete = { onRemoveDownload(item.track.id) },
                )
            }
        } else if (active.isEmpty() && failed.isEmpty()) {
            item {
                MessagePanel(
                    title = stringResource(R.string.library_no_downloaded_tracks),
                    message = stringResource(R.string.dl_no_downloaded_desc),
                )
            }
        }

        if (failed.isNotEmpty()) {
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(R.string.library_failed_count, failed.size),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            items(failed, key = { "failed_${it.track.id}" }) { item ->
                DownloadingRow(item = item, onCancel = { onRemoveDownload(item.track.id) })
            }
        }
    }
}

@Composable
internal fun DownloadedTrackRow(
    item: DownloadItem,
    onPlay: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        onClick = onPlay,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        color = Color(0xFF131B2B).copy(alpha = 0.50f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ArtworkTile(item.track.artworkUrl, item.track.title, Modifier.size(48.dp), 8)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.track.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFF8FAFC),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (item.track.explicit) {
                        Spacer(Modifier.width(6.dp))
                        ExplicitBadge()
                    }
                }
                Text(
                    "${dev.sfg.veyra.mobile.ui.components.localizedArtistName(item.track.artist)} • ${formatStorageSize(item.bytesDownloaded)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(onClick = onPlay) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = stringResource(R.string.dl_play_offline_cd), tint = Color(0xFF38BDF8))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Rounded.Delete, contentDescription = stringResource(R.string.dl_delete_download_cd), tint = Color(0xFF94A3B8))
            }
        }
    }
}

@Composable
internal fun DownloadingRow(
    item: DownloadItem,
    onCancel: () -> Unit,
) {
    val downloadFailedFallback = stringResource(R.string.dl_download_failed)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArtworkTile(item.track.artworkUrl, item.track.title, Modifier.size(48.dp), 8)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.track.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = CanopyColors.Text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (item.track.explicit) {
                    Spacer(Modifier.width(6.dp))
                    ExplicitBadge()
                }
            }
            Spacer(Modifier.height(4.dp))
            if (item.status == DownloadStatus.DOWNLOADING) {
                LinearProgressIndicator(
                    progress = { item.progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = LocalAccent.current,
                    trackColor = CanopyColors.Rule,
                )
            } else if (item.status == DownloadStatus.FAILED) {
                Text(
                    item.errorMessage.ifBlank { downloadFailedFallback },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    maxLines = 1,
                )
            } else {
                Text(
                    stringResource(R.string.dl_queued),
                    style = MaterialTheme.typography.bodySmall,
                    color = CanopyColors.Muted,
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onCancel) {
            Icon(Icons.Rounded.Delete, contentDescription = stringResource(R.string.dl_cancel_download_cd), tint = CanopyColors.Muted)
        }
    }
}

internal fun formatStorageSize(bytes: Long): String {
    if (bytes <= 0) return "0 MB"
    val mb = bytes.toDouble() / (1024 * 1024)
    return if (mb >= 1000) {
        String.format("%.1f GB", mb / 1024)
    } else {
        String.format("%.1f MB", mb)
    }
}
