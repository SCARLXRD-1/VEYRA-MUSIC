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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyListScope
import dev.sfg.veyra.mobile.ui.scroll.OrchardLazyRow as LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Flare
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.mobile.model.CatalogItem
import dev.sfg.veyra.mobile.model.LoadState
import dev.sfg.veyra.mobile.model.SearchResults
import dev.sfg.veyra.mobile.model.Track
import dev.sfg.veyra.mobile.ui.components.CatalogCard
import dev.sfg.veyra.mobile.ui.components.MessagePanel
import dev.sfg.veyra.mobile.ui.components.OrchardChromeHeight
import dev.sfg.veyra.mobile.ui.components.OrchardSectionHeader
import dev.sfg.veyra.mobile.ui.components.TrackRow
import dev.sfg.veyra.mobile.ui.components.TrackRowShimmer
import dev.sfg.veyra.mobile.ui.glass.GlassStyle
import dev.sfg.veyra.mobile.ui.glass.LocalGlass
import dev.sfg.veyra.mobile.ui.glass.LocalGlassScene
import dev.sfg.veyra.mobile.ui.glass.glassSceneSource
import dev.sfg.veyra.mobile.ui.glass.rememberGlassScene
import dev.sfg.veyra.mobile.ui.glass.GlassTone
import dev.sfg.veyra.mobile.ui.glass.glassFill
import dev.sfg.veyra.mobile.ui.glass.glassPane
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.tv.tvFocusable

import androidx.compose.ui.res.stringResource
import dev.sfg.veyra.connect.R

private val ChipIconTint = Color(0xFF38BDF8)

@Composable
fun SearchScreen(
    query: String,
    state: LoadState<SearchResults>,
    history: List<String>,
    onQueryChange: (String) -> Unit,
    onSubmit: (String) -> Unit,
    onClearHistory: () -> Unit,
    onRemoveHistoryItem: (String) -> Unit = {},
    downloadedTrackIds: Set<String> = emptySet(),
    downloadingTrackIds: Set<String> = emptySet(),
    onPlay: (Track) -> Unit,
    onPlayNext: ((Track) -> Unit)?,
    onAddToQueue: ((Track) -> Unit)?,
    onAddToPlaylist: ((Track) -> Unit)? = null,
    onDownloadTrack: ((Track) -> Unit)? = null,
    onRemoveDownloadTrack: ((String) -> Unit)? = null,
    onOpenDetail: (String) -> Unit,
    onShare: ((Track) -> Unit)? = null,
) {
    // Record only the scrolling content. The floating field must never sample itself
    // from the app-wide scene used by the mini-player and navigation.
    val searchScene = if (LocalGlass.current.enabled) rememberGlassScene(downscale = 8f, chromeBlur = 24.dp) else null
    Box(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxSize().glassSceneSource(searchScene), contentPadding = PaddingValues(top = 84.dp, bottom = OrchardChromeHeight + 16.dp)) {
            item {
                Column(Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)) {
                    Text(stringResource(R.string.search_title), style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold), color = CanopyColors.Text)
                    Spacer(Modifier.height(14.dp))
                    SearchCategoryChips(onOpenDetail = onOpenDetail)
                    Spacer(Modifier.height(18.dp))
                }
            }
            when (state) {
                LoadState.Idle -> history(
                    history = history,
                    onClear = onClearHistory,
                    onRemoveItem = onRemoveHistoryItem,
                    onQueryChange = onQueryChange,
                    onSearch = onSubmit,
                )
                LoadState.Loading -> item {
                    Column {
                        repeat(5) { TrackRowShimmer() }
                    }
                }
                is LoadState.Content -> results(
                    results = state.value,
                    downloadedTrackIds = downloadedTrackIds,
                    downloadingTrackIds = downloadingTrackIds,
                    onPlay = onPlay,
                    onPlayNext = onPlayNext,
                    onAdd = onAddToQueue,
                    onAddToPlaylist = onAddToPlaylist,
                    onDownloadTrack = onDownloadTrack,
                    onRemoveDownloadTrack = onRemoveDownloadTrack,
                    onOpen = onOpenDetail,
                    onShare = onShare,
                )
                is LoadState.Empty -> item { MessagePanel(stringResource(R.string.search_no_matches), state.message) }
                is LoadState.Error -> item { MessagePanel(stringResource(R.string.search_unavailable), state.message, stringResource(R.string.action_retry)) { onSubmit(query) } }
            }
        }
        CompositionLocalProvider(LocalGlassScene provides searchScene) {
            Box(Modifier.align(Alignment.TopCenter).padding(horizontal = 16.dp, vertical = 12.dp)) {
                SearchField(query, onQueryChange, onSubmit)
            }
        }
    }
}

@Composable
private fun SearchField(query: String, onChange: (String) -> Unit, onSubmit: (String) -> Unit) {
    val fieldShape = RoundedCornerShape(14.dp)
    val isGlass = LocalGlass.current.enabled
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val fieldBorder = if (isFocused) {
        BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.55f))
    } else {
        BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    }
    val haptics = LocalHapticFeedback.current

    TextField(
        value = query,
        onValueChange = onChange,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isGlass) 0.dp else 4.dp, fieldShape, spotColor = Color.Black.copy(alpha = 0.60f))
            .background(if (isGlass) Color.Transparent else Color(0xFF0A0F1A), fieldShape)
            .border(fieldBorder, fieldShape)
            .glassPane(fieldShape, GlassTone.CHROME),
        placeholder = { Text(stringResource(R.string.search_placeholder), color = Color(0xFF94A3B8)) },
        leadingIcon = {
            Icon(
                Icons.Rounded.Search,
                contentDescription = null,
                tint = if (isFocused) Color(0xFF38BDF8) else Color(0xFF94A3B8),
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onChange("")
                }) {
                    Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.search_clear), tint = Color(0xFF94A3B8))
                }
            }
        },
        shape = fieldShape,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFF8FAFC)),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onSubmit(query)
        }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF0A0F1A),
            unfocusedContainerColor = Color(0xFF0A0F1A),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color(0xFFF8FAFC),
            unfocusedTextColor = Color(0xFFF8FAFC),
            disabledTextColor = Color(0xFF94A3B8),
        ),
    )
}

@Composable
private fun SearchCategoryChips(onOpenDetail: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SearchCategoryChip(
            icon = Icons.Rounded.Flare,
            label = stringResource(R.string.search_chip_releases),
            modifier = Modifier.weight(1f),
            onClick = { onOpenDetail("FEmusic_new_releases") },
        )
        SearchCategoryChip(
            icon = Icons.AutoMirrored.Rounded.TrendingUp,
            label = stringResource(R.string.search_chip_charts),
            modifier = Modifier.weight(1f),
            onClick = { onOpenDetail("FEmusic_charts") },
        )
        SearchCategoryChip(
            icon = Icons.Rounded.SentimentSatisfied,
            label = stringResource(R.string.search_chip_moods),
            modifier = Modifier.weight(1f),
            onClick = { onOpenDetail("FEmusic_moods_and_genres") },
        )
    }
}

@Composable
private fun SearchCategoryChip(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val chipShape = RoundedCornerShape(12.dp)
    val cardBrush = remember {
        Brush.linearGradient(listOf(Color(0xFF151F31), Color(0xFF0F1624)))
    }
    val haptics = LocalHapticFeedback.current
    Surface(
        modifier = modifier
            .height(46.dp)
            .tvFocusable(shape = chipShape, scale = 1.05f, onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            })
            .shadow(3.dp, chipShape, spotColor = Color.Black.copy(alpha = 0.50f))
            .clip(chipShape)
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.07f)), chipShape)
            .background(cardBrush, chipShape)
            .clickable(onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }),
        color = Color.Transparent,
        shape = chipShape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ChipIconTint,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFFF8FAFC),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun LazyListScope.history(
    history: List<String>,
    onClear: () -> Unit,
    onRemoveItem: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
) {
    if (history.isEmpty()) {
        item { MessagePanel(stringResource(R.string.search_empty_prompt), stringResource(R.string.search_empty_desc)) }
        return
    }
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.search_recent_title),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = CanopyColors.Muted,
            )
            val haptics = LocalHapticFeedback.current
            Text(
                text = stringResource(R.string.search_clear_all),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = CanopyColors.Muted,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClear()
                    })
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }
    }
    items(history, key = { it }) { value ->
        val itemShape = RoundedCornerShape(14.dp)
        val haptics = LocalHapticFeedback.current
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .tvFocusable(shape = itemShape, scale = 1.02f, onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onQueryChange(value)
                    onSearch(value)
                })
                .clip(itemShape)
                .clickable {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onQueryChange(value)
                    onSearch(value)
                },
            color = CanopyColors.Surface,
            shape = itemShape,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = null,
                    tint = CanopyColors.Muted,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = value,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = CanopyColors.Text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                IconButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onRemoveItem(value)
                    },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.search_remove),
                        tint = CanopyColors.Muted,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

private fun LazyListScope.results(
    results: SearchResults,
    downloadedTrackIds: Set<String>,
    downloadingTrackIds: Set<String> = emptySet(),
    onPlay: (Track) -> Unit,
    onPlayNext: ((Track) -> Unit)?,
    onAdd: ((Track) -> Unit)?,
    onAddToPlaylist: ((Track) -> Unit)? = null,
    onDownloadTrack: ((Track) -> Unit)? = null,
    onRemoveDownloadTrack: ((String) -> Unit)? = null,
    onOpen: (String) -> Unit,
    onShare: ((Track) -> Unit)? = null,
) {
    if (results.tracks.isNotEmpty()) {
        item { OrchardSectionHeader(stringResource(R.string.search_section_songs)) }
        itemsIndexed(results.tracks, key = { index, it -> "track:${it.id}_$index" }) { _, track ->
            val isDownloaded = downloadedTrackIds.contains(track.id)
            val isDownloading = downloadingTrackIds.contains(track.id)
            TrackRow(
                track = track,
                onPlay = { onPlay(track) },
                modifier = Modifier.padding(horizontal = 8.dp),
                onPlayNext = onPlayNext?.let { action -> { action(track) } },
                onAddToQueue = onAdd?.let { action -> { action(track) } },
                onAddToPlaylist = onAddToPlaylist?.let { action -> { action(track) } },
                onDownload = onDownloadTrack?.let { action -> { action(track) } },
                onRemoveDownload = onRemoveDownloadTrack?.let { action -> { action(track.id) } },
                isDownloaded = isDownloaded,
                isDownloading = isDownloading,
                onShare = onShare?.let { action -> { action(track) } },
                onViewAlbum = if (track.albumId.isNotBlank()) {{ onOpen(track.albumId) }} else null,
                onViewArtist = if (track.artistId.isNotBlank()) {{ onOpen(track.artistId) }} else null,
            )
        }
    }
    val shelves = listOf(
        R.string.search_section_albums to results.albums.map { CatalogItem.Record(it) },
        R.string.search_section_artists to results.artists.map { CatalogItem.Performer(it) },
        R.string.search_section_playlists to results.playlists.map { CatalogItem.Collection(it) },
    )
    shelves.filter { it.second.isNotEmpty() }.forEach { (titleRes, values) ->
        item { OrchardSectionHeader(stringResource(titleRes)) }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                itemsIndexed(values, key = { index, it -> "$titleRes:${it.stableId}_$index" }) { _, item ->
                    CatalogCard(item, { onOpen(item.stableId) })
                }
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

