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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.Track
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.mobile.ui.theme.LocalAccent

/**
 * Collection action buttons row: [ Circular Shuffle ] [ Wide White Play Pill ]
 * [ Circular Add/Favorite ] [ Circular Download ]
 */
@Composable
fun CollectionActionRow(
    // Defaults to white so playlists keep the neutral pill; albums pass their cover's colour.
    accent: Color = Color.White,
    onPlay: () -> Unit,
    onShuffle: () -> Unit,
    onSave: () -> Unit,
    isSaved: Boolean,
    playEnabled: Boolean = true,
    shuffleEnabled: Boolean = true,
    onDownload: (() -> Unit)? = null,
    isDownloaded: Boolean = false,
    isDownloading: Boolean = false,
    downloadEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val dishBrush = remember {
        Brush.linearGradient(listOf(Color(0xFF151F30), Color(0xFF0E1524)))
    }
    val dishBorder = remember {
        BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    }
    val isDefaultAccent = accent == Color.White || accent == Color.Unspecified
    val playBrush = remember(accent, isDefaultAccent) {
        if (isDefaultAccent) {
            Brush.linearGradient(listOf(Color(0xFF2275F8), Color(0xFF104CA8)))
        } else {
            Brush.linearGradient(listOf(accent, accent.copy(alpha = 0.78f)))
        }
    }
    val playBorder = remember(isDefaultAccent) {
        BorderStroke(1.dp, if (isDefaultAccent) Color.White.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.32f))
    }
    val playContentColor = if (isDefaultAccent) {
        Color.White
    } else {
        val luminance = (0.299f * accent.red + 0.587f * accent.green + 0.114f * accent.blue)
        if (luminance > 0.6f) Color.Black else Color.White
    }
    val haptics = LocalHapticFeedback.current

    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Shuffle button (recessed dish)
        Surface(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onShuffle()
            },
            enabled = shuffleEnabled,
            shape = CircleShape,
            color = Color.Transparent,
            border = dishBorder,
            modifier = Modifier
                .size(44.dp)
                .shadow(4.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.55f))
                .background(dishBrush, CircleShape),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Rounded.Shuffle,
                    contentDescription = stringResource(R.string.action_shuffle),
                    tint = if (shuffleEnabled) Color(0xFFCBD5E1) else Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // Center prominent Play pill button
        Button(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onPlay()
            },
            enabled = playEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = playContentColor,
                disabledContainerColor = Color.White.copy(alpha = 0.08f),
                disabledContentColor = Color.White.copy(alpha = 0.35f),
            ),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .shadow(
                    elevation = if (playEnabled) 6.dp else 0.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = if (isDefaultAccent) Color(0xFF1E6CEB).copy(alpha = 0.50f) else accent.copy(alpha = 0.45f),
                )
                .then(
                    if (playEnabled) {
                        Modifier
                            .background(playBrush, RoundedCornerShape(24.dp))
                            .border(playBorder, RoundedCornerShape(24.dp))
                    } else {
                        Modifier
                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)), RoundedCornerShape(24.dp))
                    }
                ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = if (playEnabled) playContentColor else Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(22.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.action_play),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (playEnabled) playContentColor else Color.White.copy(alpha = 0.35f),
                )
            }
        }

        // Add / Save button (recessed dish)
        Surface(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSave()
            },
            shape = CircleShape,
            color = Color.Transparent,
            border = if (isSaved) BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.50f)) else dishBorder,
            modifier = Modifier
                .size(44.dp)
                .shadow(4.dp, CircleShape, spotColor = if (isSaved) Color(0xFF1E6CEB).copy(alpha = 0.40f) else Color.Black.copy(alpha = 0.55f))
                .background(
                    if (isSaved) Brush.linearGradient(listOf(Color(0xFF182845), Color(0xFF101B2E))) else dishBrush,
                    CircleShape,
                ),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    if (isSaved) Icons.Rounded.Check else Icons.Rounded.Add,
                    contentDescription = if (isSaved) stringResource(R.string.collect_saved_to_library) else stringResource(R.string.collect_add_to_library),
                    tint = if (isSaved) Color(0xFF38BDF8) else Color(0xFFCBD5E1),
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        // Download / Offline button (recessed dish)
        if (onDownload != null) {
            Surface(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onDownload()
                },
                enabled = downloadEnabled,
                shape = CircleShape,
                color = Color.Transparent,
                border = if (isDownloaded) BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.50f)) else dishBorder,
                modifier = Modifier
                    .size(44.dp)
                    .shadow(4.dp, CircleShape, spotColor = if (isDownloaded) Color(0xFF1E6CEB).copy(alpha = 0.40f) else Color.Black.copy(alpha = 0.55f))
                    .background(
                        if (isDownloaded) Brush.linearGradient(listOf(Color(0xFF182845), Color(0xFF101B2E))) else dishBrush,
                        CircleShape,
                    ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color(0xFF38BDF8),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            if (isDownloaded) Icons.Rounded.DownloadDone else Icons.Rounded.Download,
                            contentDescription = if (isDownloaded) stringResource(R.string.collect_downloaded_offline) else stringResource(R.string.action_download),
                            tint =
                                if (isDownloaded) {
                                    Color(0xFF38BDF8)
                                } else if (downloadEnabled) {
                                    Color(0xFFCBD5E1)
                                } else {
                                    Color.White.copy(alpha = 0.35f)
                                },
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper to produce the collection download / delete callback based on current download state.
 */
fun collectionDownloadAction(
    tracks: List<Track>,
    downloadedTrackIds: Set<String>,
    onDownloadTracks: ((List<Track>) -> Unit)?,
    onRemoveDownloadTracks: ((List<Track>) -> Unit)?,
): (() -> Unit)? {
    if (onDownloadTracks == null || onRemoveDownloadTracks == null || tracks.isEmpty()) {
        return null
    }
    val allDownloaded = tracks.all { downloadedTrackIds.contains(it.id) }
    return {
        if (allDownloaded) {
            onRemoveDownloadTracks(tracks)
        } else {
            onDownloadTracks(tracks)
        }
    }
}

/** Editorial review / description preview with inline "MORE". */
@Composable
fun AlbumEditorialReview(
    description: String,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (description.isBlank()) return

    val moreText = stringResource(R.string.collect_more)
    val cleanText = description.replace(Regex("\\s+"), " ").trim()
    val previewText = if (cleanText.length > 140) cleanText.take(140).trimEnd() + "…" else cleanText

    val annotated = buildAnnotatedString {
        append(previewText)
        append(" ")
        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
            append(moreText)
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenAbout)
                .padding(horizontal = 24.dp, vertical = 6.dp)
    ) {
        Text(
            text = annotated,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp, fontSize = 13.sp),
            color = Color.White.copy(alpha = 0.72f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun CollectionTopBar(
    onBack: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit,
    isSaved: Boolean,
    onAbout: (() -> Unit)? = null,
    onBestMix: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    isSearching: Boolean = false,
    searchQuery: String = "",
    onSearchQueryChange: ((String) -> Unit)? = null,
    onCloseSearch: (() -> Unit)? = null,
    searchPlaceholder: String? = null,
    aboutLabel: String? = null,
    onDownload: (() -> Unit)? = null,
    isDownloaded: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val findInPlaylistText = searchPlaceholder ?: stringResource(R.string.collect_find_in_playlist)
    val aboutText = aboutLabel ?: stringResource(R.string.collect_about_label)
    // The label drives the album/playlist wording of the menu, so accept the
    // Spanish "álbum" too in case the caller passes a translated label.
    val isAlbumCollection = aboutText.contains("album", true) || aboutText.contains("álbum", true)

    if (isSearching && onSearchQueryChange != null && onCloseSearch != null) {
        CollectionTopSearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onClose = onCloseSearch,
            placeholder = findInPlaylistText,
            modifier = modifier,
        )
        return
    }

    var menuOpen by remember { mutableStateOf(false) }
    val dishBrush = remember {
        Brush.linearGradient(listOf(Color(0xFF151F30), Color(0xFF0E1524)))
    }
    val dishBorder = remember {
        BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            onClick = onBack,
            shape = CircleShape,
            color = Color.Transparent,
            border = dishBorder,
            modifier = Modifier
                .size(38.dp)
                .shadow(3.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.50f))
                .background(dishBrush, CircleShape),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.collect_back_cd),
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onSearch != null) {
                Surface(
                    onClick = onSearch,
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = dishBorder,
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(3.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.50f))
                        .background(dishBrush, CircleShape),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.Search,
                            contentDescription = stringResource(R.string.collect_search_in_collection_cd),
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            Surface(
                onClick = onShare,
                shape = CircleShape,
                color = Color.Transparent,
                border = dishBorder,
                modifier = Modifier
                    .size(38.dp)
                    .shadow(3.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.50f))
                    .background(dishBrush, CircleShape),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.IosShare,
                        contentDescription = stringResource(R.string.action_share),
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Box {
                Surface(
                    onClick = { menuOpen = true },
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = dishBorder,
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(3.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.50f))
                        .background(dishBrush, CircleShape),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.MoreHoriz,
                            contentDescription = stringResource(R.string.collect_more_options_cd),
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    if (onSearch != null) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (isAlbumCollection) {
                                        stringResource(R.string.collect_find_in_album)
                                    } else {
                                        stringResource(R.string.collect_find_in_playlist)
                                    }
                                )
                            },
                            onClick = {
                                menuOpen = false
                                onSearch()
                            },
                        )
                    }
                    if (onDownload != null) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (isDownloaded) {
                                        stringResource(R.string.action_delete)
                                    } else if (isAlbumCollection) {
                                        stringResource(R.string.collect_download_album)
                                    } else {
                                        stringResource(R.string.collect_download_playlist)
                                    }
                                )
                            },
                            onClick = {
                                menuOpen = false
                                onDownload()
                            },
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (isSaved) {
                                    stringResource(R.string.collect_remove_from_library)
                                } else {
                                    stringResource(R.string.collect_add_to_library)
                                }
                            )
                        },
                        onClick = {
                            menuOpen = false
                            onSave()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.action_share)) },
                        onClick = {
                            menuOpen = false
                            onShare()
                        },
                    )
                    if (onAbout != null) {
                        DropdownMenuItem(
                            text = { Text(aboutText) },
                            onClick = {
                                menuOpen = false
                                onAbout()
                            },
                        )
                    }
                    if (onBestMix != null) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.collect_play_with_best_mix)) },
                            onClick = {
                                menuOpen = false
                                onBestMix()
                            },
                        )
                    }
                }
            }
        }
    }
}
