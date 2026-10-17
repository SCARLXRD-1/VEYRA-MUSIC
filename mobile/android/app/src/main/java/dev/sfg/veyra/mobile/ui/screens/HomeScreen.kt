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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import dev.sfg.veyra.mobile.ui.scroll.OrchardLazyColumn as LazyColumn
import dev.sfg.veyra.mobile.ui.scroll.OrchardLazyRow as LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.mobile.auth.AuthState
import dev.sfg.veyra.mobile.download.DownloadItem
import dev.sfg.veyra.mobile.download.DownloadStatus
import dev.sfg.veyra.mobile.model.Album
import dev.sfg.veyra.mobile.model.Artist
import dev.sfg.veyra.mobile.model.BuiltInHomeSection
import dev.sfg.veyra.mobile.model.CatalogItem
import dev.sfg.veyra.mobile.model.CatalogSection
import dev.sfg.veyra.mobile.model.LibraryFilter
import dev.sfg.veyra.mobile.model.LibrarySnapshot
import dev.sfg.veyra.mobile.model.LoadState
import dev.sfg.veyra.mobile.model.OrchardSettings
import dev.sfg.veyra.mobile.model.Playlist
import dev.sfg.veyra.mobile.model.Track
import dev.sfg.veyra.mobile.ui.components.ArtworkTile
import dev.sfg.veyra.mobile.ui.components.CatalogSectionBottomSheet
import dev.sfg.veyra.mobile.ui.components.ExplicitBadge
import dev.sfg.veyra.mobile.ui.components.HomeSectionShimmer
import dev.sfg.veyra.mobile.ui.components.MessagePanel
import dev.sfg.veyra.mobile.ui.components.OrchardChromeHeight
import dev.sfg.veyra.mobile.ui.components.VeyraMark
import dev.sfg.veyra.mobile.ui.components.VeyraWordmark
import dev.sfg.veyra.mobile.ui.components.StatusPillBadge
import dev.sfg.veyra.mobile.ui.components.StatusPillTone
import dev.sfg.veyra.mobile.ui.components.StatusPillSize
import androidx.compose.foundation.border
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.ui.glass.LocalGlass
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.tv.tvFocusable
import java.util.Calendar

private data class QuickGridItem(
    val id: String,
    val title: String,
    val artworkUrl: String,
    val icon: ImageVector? = null,
    val gradient: Brush? = null,
    val onClick: () -> Unit,
    val onPlay: () -> Unit,
)

data class HomeSectionSheetState(
    val title: String,
    val initialItems: List<CatalogItem>,
    val browseId: String = "",
    val params: String = "",
)

@Composable
fun HomeScreen(
    settings: OrchardSettings,
    state: LoadState<List<CatalogSection>>,
    library: LibrarySnapshot,
    auth: AuthState = AuthState.SignedOut,
    downloads: List<DownloadItem> = emptyList(),
    downloadedTrackIds: Set<String> = emptySet(),
    isOffline: Boolean = false,
    onRefresh: () -> Unit,
    onSearch: () -> Unit,
    onLibrary: (LibraryFilter) -> Unit,
    onDevices: () -> Unit,
    onPlay: (Track) -> Unit,
    onOpenDetail: (String) -> Unit,
    onEditLayout: () -> Unit = {},
    onToggleLike: (Track) -> Unit,
    onPlayNext: ((Track) -> Unit)? = null,
    onAddToQueue: ((Track) -> Unit)? = null,
    onAddToPlaylist: ((Track) -> Unit)? = null,
    onShare: ((Track) -> Unit)? = null,
    onOpenProfile: () -> Unit = {},
    onFetchSectionItems: (suspend (String, String) -> List<CatalogItem>)? = null,
    onPlayItem: ((CatalogItem) -> Unit)? = null,
    onPlayCollection: ((String, String) -> Unit)? = null,
) {
    if (dev.sfg.veyra.mobile.ui.foldable.isFoldableOrWideLayout()) {
        dev.sfg.veyra.mobile.ui.foldable.FoldableHomeScreen(
            settings = settings,
            state = state,
            library = library,
            auth = auth,
            downloads = downloads,
            downloadedTrackIds = downloadedTrackIds,
            isOffline = isOffline,
            onRefresh = onRefresh,
            onSearch = onSearch,
            onLibrary = onLibrary,
            onDevices = onDevices,
            onPlay = onPlay,
            onOpenDetail = onOpenDetail,
            onEditLayout = onEditLayout,
            onToggleLike = onToggleLike,
            onPlayNext = onPlayNext,
            onAddToQueue = onAddToQueue,
            onAddToPlaylist = onAddToPlaylist,
            onShare = onShare,
            onOpenProfile = onOpenProfile,
            onFetchSectionItems = onFetchSectionItems,
            onPlayItem = onPlayItem,
            onPlayCollection = onPlayCollection,
        )
        return
    }

    val glass = LocalGlass.current.enabled
    val context = LocalContext.current
    var activeSectionSheet by remember { mutableStateOf<HomeSectionSheetState?>(null) }

    val playCatalogItem: (CatalogItem) -> Unit = { item ->
        if (onPlayItem != null) {
            onPlayItem(item)
        } else if (onPlayCollection != null) {
            when (item) {
                is CatalogItem.Song -> onPlay(item.track)
                is CatalogItem.Collection -> onPlayCollection(item.playlist.id, item.title)
                is CatalogItem.Record -> onPlayCollection(item.album.id, item.title)
                is CatalogItem.Performer -> onPlayCollection(item.artist.id, item.title)
                is CatalogItem.Category -> onOpenDetail(item.stableId)
            }
        } else {
            when (item) {
                is CatalogItem.Song -> onPlay(item.track)
                else -> onOpenDetail(item.stableId)
            }
        }
    }

    val openCatalogItem: (CatalogItem) -> Unit = { item ->
        when (item) {
            is CatalogItem.Song -> onPlay(item.track)
            else -> onOpenDetail(item.stableId)
        }
    }

    activeSectionSheet?.let { sheet ->
        CatalogSectionBottomSheet(
            title = sheet.title,
            initialItems = sheet.initialItems,
            browseId = sheet.browseId,
            params = sheet.params,
            onFetchFullItems = onFetchSectionItems,
            onPlay = onPlay,
            onPlayItem = playCatalogItem,
            onOpen = onOpenDetail,
            onDismiss = { activeSectionSheet = null },
        )
    }

    val completedDownloads =
        remember(downloads) {
            downloads.filter { it.status == DownloadStatus.COMPLETED && it.filePath.isNotBlank() }
        }
    val downloadedTracks: List<Track> =
        remember(completedDownloads) { completedDownloads.map { it.track } }
    val effectiveOffline = isOffline || state is LoadState.Error

    val offlinePlaylistItems: List<CatalogItem.Collection> =
        remember(downloadedTracks, downloadedTrackIds, library.savedPlaylists) {
            buildList {
                if (downloadedTracks.isNotEmpty()) {
                    add(
                        CatalogItem.Collection(
                            Playlist(
                                id = "offline_downloads",
                                title = context.getString(R.string.home_downloaded_music),
                                author = "Veyra Music",
                                artworkUrl =
                                    downloadedTracks
                                        .firstOrNull { it.artworkUrl.isNotBlank() }
                                        ?.artworkUrl
                                        .orEmpty(),
                                description = context.getString(R.string.home_downloaded_music_desc),
                                tracks = downloadedTracks,
                            )
                        )
                    )
                }
                library.savedPlaylists.forEach { playlist ->
                    val matching = playlist.tracks.filter { it.id in downloadedTrackIds }
                    if (matching.isNotEmpty()) {
                        add(CatalogItem.Collection(playlist.copy(tracks = matching)))
                    }
                }
            }
        }

    val offlineArtistItems: List<CatalogItem.Performer> =
        remember(downloadedTracks, library.savedArtists) {
            val grouped = mutableMapOf<String, MutableList<Track>>()
            downloadedTracks.forEach { track ->
                if (track.artist.isNotBlank()) {
                    grouped.getOrPut(track.artist) { mutableListOf() }.add(track)
                }
            }
            grouped.map { (artistName, artistTracks) ->
                val artistId =
                    artistTracks.firstOrNull { it.artistId.isNotBlank() }?.artistId ?: artistName
                val artworkUrl =
                    library.savedArtists
                        .firstOrNull { it.name.equals(artistName, ignoreCase = true) }
                        ?.artworkUrl
                        ?: artistTracks
                            .firstOrNull { it.artworkUrl.isNotBlank() }
                            ?.artworkUrl
                            .orEmpty()
                CatalogItem.Performer(
                    Artist(
                        id = artistId,
                        name = artistName,
                        artworkUrl = artworkUrl,
                        subtitle =
                            context.getString(R.string.home_downloaded_songs_count, artistTracks.size),
                    )
                )
            }
        }

    val offlineAlbumItems: List<CatalogItem.Record> =
        remember(downloadedTracks) {
            val grouped = mutableMapOf<String, MutableList<Track>>()
            downloadedTracks.forEach { track ->
                if (track.album.isNotBlank()) {
                    grouped.getOrPut(track.album) { mutableListOf() }.add(track)
                }
            }
            grouped.map { (albumTitle, albumTracks) ->
                val albumId =
                    albumTracks.firstOrNull { it.albumId.isNotBlank() }?.albumId ?: albumTitle
                val artist = albumTracks.firstOrNull()?.artist.orEmpty()
                val artworkUrl =
                    albumTracks.firstOrNull { it.artworkUrl.isNotBlank() }?.artworkUrl.orEmpty()
                CatalogItem.Record(
                    Album(
                        id = albumId,
                        title = albumTitle,
                        artist = artist,
                        artworkUrl = artworkUrl,
                        year = "",
                        tracks = albumTracks,
                    )
                )
            }
        }

    // Hero items for the featured carousel (Apple Music / ArchiveTune spotlight)
    val featuredHeroItems = remember(state, library) {
        val fromState = if (state is LoadState.Content) {
            state.value.flatMap { it.items }.filter { it.artworkUrl.isNotBlank() }.distinctBy { it.stableId }
        } else emptyList()
        val fromPlaylists = library.savedPlaylists.map { CatalogItem.Collection(it) }
        val combined = (fromState + fromPlaylists).distinctBy { it.stableId }.take(5)
        combined
    }

    // Spotify-style 6-grid quick access items
    val quickGridItems = remember(library, state) {
        buildList {
            // 1. Playlists
            library.savedPlaylists.take(6).forEach { playlist ->
                add(
                    QuickGridItem(
                        id = "pl_${playlist.id}",
                        title = playlist.title,
                        artworkUrl = playlist.artworkUrl,
                        onClick = { onOpenDetail(playlist.id) },
                        onPlay = { playCatalogItem(CatalogItem.Collection(playlist)) },
                    )
                )
            }
            // 2. Saved Albums
            if (size < 6) {
                library.savedAlbums.take(6 - size).forEach { album ->
                    add(
                        QuickGridItem(
                            id = "alb_${album.id}",
                            title = album.title,
                            artworkUrl = album.artworkUrl,
                            onClick = { onOpenDetail(album.id) },
                            onPlay = { playCatalogItem(CatalogItem.Record(album)) },
                        )
                    )
                }
            }
            // 3. Catalog items fallback
            if (size < 6 && state is LoadState.Content) {
                val stateItems = state.value.flatMap { it.items }.filter { it.artworkUrl.isNotBlank() }
                stateItems.distinctBy { it.stableId }.take(6 - size).forEach { item ->
                    add(
                        QuickGridItem(
                            id = "st_${item.stableId}",
                            title = item.title,
                            artworkUrl = item.artworkUrl,
                            onClick = { openCatalogItem(item) },
                            onPlay = { playCatalogItem(item) },
                        )
                    )
                }
            }
        }.take(6)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = OrchardChromeHeight),
    ) {
        // Cabecera superior oficial de Veyra Music
        item {
            HomeHeader(auth = auth, onSearch = onSearch, onProfile = onOpenProfile)
        }

        // Spotify 2x3 Quick-Access Grid (Top Playlists & Saved Items)
        if (quickGridItems.isNotEmpty() && !effectiveOffline) {
            item {
                SpotifyQuickGrid(
                    items = quickGridItems,
                )
                Spacer(Modifier.height(18.dp))
            }
        }

        // Featured Hero Carousel Banner (Apple Music / ArchiveTune spotlight)
        if (!effectiveOffline && featuredHeroItems.isNotEmpty()) {
            item {
                FeaturedHeroCarousel(
                    items = featuredHeroItems,
                    onPlay = { item -> playCatalogItem(item) },
                    onClick = { item -> openCatalogItem(item) },
                )
                Spacer(Modifier.height(18.dp))
            }
        }

        // Offline Mode Banner
        if (effectiveOffline) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = CanopyColors.Surface,
                    border = BorderStroke(1.dp, LocalAccent.current.copy(alpha = 0.35f)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Rounded.CloudOff,
                            contentDescription = null,
                            tint = LocalAccent.current,
                            modifier = Modifier.size(22.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.home_offline_mode),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = CanopyColors.Text,
                            )
                            Text(
                                if (downloadedTracks.isNotEmpty())
                                    stringResource(
                                        R.string.home_showing_downloaded,
                                        downloadedTracks.size,
                                    )
                                else stringResource(R.string.home_no_internet),
                                style = MaterialTheme.typography.bodySmall,
                                color = CanopyColors.Muted,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }
        }

        val layoutConfig =
            if (effectiveOffline) settings.homeLayoutOffline else settings.homeLayoutOnline

        layoutConfig.forEach { config ->
            if (!config.enabled) return@forEach

            when (config.section) {
                BuiltInHomeSection.YOUR_PLAYLISTS -> {
                    if (effectiveOffline) return@forEach
                    val playlistItems =
                        library.savedPlaylists
                            .map { CatalogItem.Collection(it) }
                            .ifEmpty { extractItemsOfKind<CatalogItem.Collection>(state) }
                            .distinctBy { it.stableId }
                    if (playlistItems.isNotEmpty()) {
                        item {
                            val sectionTitle = stringResource(R.string.library_your_playlists)
                            HomeSectionHeader(
                                title = sectionTitle,
                                onSeeAll = {
                                    activeSectionSheet = HomeSectionSheetState(
                                        title = sectionTitle,
                                        initialItems = playlistItems,
                                    )
                                },
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                itemsIndexed(
                                    playlistItems,
                                    key = { index, item -> "pl_${item.stableId}_$index" },
                                ) { _, item ->
                                    if (glass) {
                                        GlassJumpBackInCard(
                                            item = item,
                                            onClick = { openCatalogItem(item) },
                                            onPlay = { playCatalogItem(item) },
                                        )
                                    } else {
                                        PlaylistCard(
                                            item = item,
                                            onClick = { openCatalogItem(item) },
                                        )
                                    }
                                }
                            }
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }
                BuiltInHomeSection.SUBSCRIBED_ARTISTS -> {
                    if (effectiveOffline) return@forEach
                    val artistItems =
                        library.savedArtists
                            .map { CatalogItem.Performer(it) }
                            .ifEmpty { extractItemsOfKind<CatalogItem.Performer>(state) }
                            .distinctBy { it.stableId }
                    if (artistItems.isNotEmpty()) {
                        item {
                            val sectionTitle = stringResource(R.string.home_keep_listening)
                            HomeSectionHeader(
                                title = sectionTitle,
                                subtitle = stringResource(R.string.home_subscribed_artists_desc),
                                onSeeAll = {
                                    activeSectionSheet = HomeSectionSheetState(
                                        title = sectionTitle,
                                        initialItems = artistItems,
                                    )
                                },
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                itemsIndexed(
                                    artistItems,
                                    key = { index, item -> "art_${item.stableId}_$index" },
                                ) { _, item ->
                                    CircularArtistCard(
                                        item = item,
                                        onClick = { openCatalogItem(item) },
                                    )
                                }
                            }
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }
                BuiltInHomeSection.TOP_SONGS -> {
                    if (effectiveOffline) return@forEach
                    val songTracks =
                        library.mostPlayed
                            .ifEmpty { library.likedTracks }
                            .ifEmpty { library.recentlyPlayed }
                            .ifEmpty {
                                extractItemsOfKind<CatalogItem.Song>(state).map { it.track }
                            }
                            .distinctBy { it.id }
                    if (songTracks.isNotEmpty()) {
                        item {
                            val sectionTitle = stringResource(R.string.home_latest_songs)
                            HomeSectionHeader(
                                title = sectionTitle,
                                subtitle = stringResource(R.string.home_latest_songs_desc),
                                onSeeAll = {
                                    activeSectionSheet = HomeSectionSheetState(
                                        title = sectionTitle,
                                        initialItems = songTracks.map { CatalogItem.Song(it) },
                                    )
                                },
                            )
                        }
                        itemsIndexed(
                            songTracks.take(5),
                            key = { index, track -> "top_${track.id}_$index" },
                        ) { index, track ->
                            val liked = library.likedTracks.any { it.id == track.id }
                            RankedSongRow(
                                rank = index + 1,
                                track = track,
                                liked = liked,
                                onPlay = { onPlay(track) },
                                onToggleLike = { onToggleLike(track) },
                                onPlayNext = onPlayNext,
                                onAddToQueue = onAddToQueue,
                                onAddToPlaylist = onAddToPlaylist,
                                onShare = onShare,
                            )
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }
                BuiltInHomeSection.RECOMMENDATIONS -> {
                    if (effectiveOffline) return@forEach
                    when (state) {
                        is LoadState.Content -> {
                            state.value.forEachIndexed { sectionIndex, section ->
                                item(key = "head:${section.id}") {
                                    HomeSectionHeader(
                                        title = section.title,
                                        onSeeAll = {
                                            activeSectionSheet = HomeSectionSheetState(
                                                title = section.title,
                                                initialItems = section.items,
                                                browseId = section.browseId,
                                                params = section.params,
                                            )
                                        },
                                    )
                                }
                                item(key = "rail:${section.id}") {
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    ) {
                                        itemsIndexed(
                                            section.items,
                                            key = { index, item ->
                                                "${section.id}_${item.stableId}_$index"
                                            },
                                        ) { _, item ->
                                            if (glass) {
                                                when (sectionIndex % 3) {
                                                    0 -> GlassSquircleCard(
                                                        item = item,
                                                        onClick = { openCatalogItem(item) },
                                                        onPlay = { playCatalogItem(item) },
                                                    )
                                                    1 -> GlassJumpBackInCard(
                                                        item = item,
                                                        onClick = { openCatalogItem(item) },
                                                        onPlay = { playCatalogItem(item) },
                                                    )
                                                    else -> GlassRecentlyPlayedCard(
                                                        item = item,
                                                        onClick = { openCatalogItem(item) },
                                                    )
                                                }
                                            } else {
                                                PlaylistCard(
                                                    item = item,
                                                    onClick = {
                                                        openCatalogItem(item)
                                                    },
                                                )
                                            }
                                        }
                                    }
                                }
                                item(key = "gap:${section.id}") {
                                    Spacer(Modifier.height(20.dp))
                                }
                            }
                        }
                        LoadState.Loading -> {
                            item { HomeSectionShimmer(stringResource(R.string.home_recommendations)) }
                        }
                        is LoadState.Empty -> {
                            item {
                                MessagePanel(
                                    stringResource(R.string.home_library_waiting),
                                    state.message,
                                    stringResource(R.string.home_refresh_action),
                                    onRefresh,
                                )
                            }
                        }
                        else -> Unit
                    }
                }
                BuiltInHomeSection.DOWNLOADED_PLAYLISTS -> {
                    if (!effectiveOffline) return@forEach
                    if (offlinePlaylistItems.isNotEmpty()) {
                        item {
                            val sectionTitle = stringResource(R.string.home_downloaded_playlists)
                            HomeSectionHeader(
                                title = sectionTitle,
                                onSeeAll = {
                                    activeSectionSheet = HomeSectionSheetState(
                                        title = sectionTitle,
                                        initialItems = offlinePlaylistItems,
                                    )
                                },
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                itemsIndexed(
                                    offlinePlaylistItems,
                                    key = { index, item -> "off_pl_${item.stableId}_$index" },
                                ) { _, item ->
                                    if (glass) {
                                        GlassJumpBackInCard(
                                            item = item,
                                            onClick = { openCatalogItem(item) },
                                            onPlay = { playCatalogItem(item) },
                                        )
                                    } else {
                                        PlaylistCard(
                                            item = item,
                                            onClick = { openCatalogItem(item) },
                                        )
                                    }
                                }
                            }
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }
                BuiltInHomeSection.DOWNLOADED_ARTISTS -> {
                    if (!effectiveOffline) return@forEach
                    if (offlineArtistItems.isNotEmpty()) {
                        item {
                            val sectionTitle = stringResource(R.string.home_downloaded_artists)
                            HomeSectionHeader(
                                title = sectionTitle,
                                onSeeAll = {
                                    activeSectionSheet = HomeSectionSheetState(
                                        title = sectionTitle,
                                        initialItems = offlineArtistItems,
                                    )
                                },
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                itemsIndexed(
                                    offlineArtistItems,
                                    key = { index, item -> "off_art_${item.stableId}_$index" },
                                ) { _, item ->
                                    CircularArtistCard(
                                        item = item,
                                        onClick = { openCatalogItem(item) },
                                    )
                                }
                            }
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }
                BuiltInHomeSection.DOWNLOADED_ALBUMS -> {
                    if (!effectiveOffline) return@forEach
                    if (offlineAlbumItems.isNotEmpty()) {
                        item {
                            val sectionTitle = stringResource(R.string.home_downloaded_albums)
                            HomeSectionHeader(
                                title = sectionTitle,
                                onSeeAll = {
                                    activeSectionSheet = HomeSectionSheetState(
                                        title = sectionTitle,
                                        initialItems = offlineAlbumItems,
                                    )
                                },
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                itemsIndexed(
                                    offlineAlbumItems,
                                    key = { index, item -> "off_alb_${item.stableId}_$index" },
                                ) { _, item ->
                                    if (glass) {
                                        GlassRecentlyPlayedCard(
                                            item = item,
                                            onClick = { openCatalogItem(item) },
                                        )
                                    } else {
                                        PlaylistCard(
                                            item = item,
                                            onClick = { openCatalogItem(item) },
                                        )
                                    }
                                }
                            }
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }
                BuiltInHomeSection.DOWNLOADED_SONGS -> {
                    if (!effectiveOffline) return@forEach
                    if (downloadedTracks.isNotEmpty()) {
                        item {
                            val sectionTitle = stringResource(R.string.home_downloaded_songs)
                            HomeSectionHeader(
                                title = sectionTitle,
                                onSeeAll = {
                                    activeSectionSheet = HomeSectionSheetState(
                                        title = sectionTitle,
                                        initialItems = downloadedTracks.map { CatalogItem.Song(it) },
                                    )
                                },
                            )
                        }
                        itemsIndexed(
                            downloadedTracks,
                            key = { index, track -> "off_trk_${track.id}_$index" },
                        ) { index, track ->
                            val liked = library.likedTracks.any { it.id == track.id }
                            RankedSongRow(
                                rank = index + 1,
                                track = track,
                                liked = liked,
                                onPlay = { onPlay(track) },
                                onToggleLike = { onToggleLike(track) },
                                onPlayNext = onPlayNext,
                                onAddToQueue = onAddToQueue,
                                onAddToPlaylist = onAddToPlaylist,
                                onShare = onShare,
                            )
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    } else {
                        item {
                            MessagePanel(
                                title = stringResource(R.string.home_no_downloaded_music),
                                message = stringResource(R.string.home_no_downloaded_music_desc),
                                actionLabel = stringResource(R.string.home_try_reconnecting),
                                onAction = onRefresh,
                            )
                        }
                    }
                }
            }
        }

        item {
            Box(
                Modifier.fillMaxWidth().padding(vertical = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                OutlinedButton(onClick = onEditLayout) {
                    Text(stringResource(R.string.home_edit_layout), color = CanopyColors.Text)
                }
            }
        }
    }
}

/**
 * Cabecera editorial de vidrio esmerilado con el logo de Veyra Music, perfil, búsqueda y
 * saludo dinámico.
 */
@Composable
private fun HomeHeader(
    auth: AuthState,
    onSearch: () -> Unit,
    onProfile: () -> Unit = {},
) {
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = when {
        hour < 12 -> stringResource(R.string.home_greeting_morning)
        hour < 17 -> stringResource(R.string.home_greeting_afternoon)
        else -> stringResource(R.string.home_greeting_evening)
    }
    val listenerFallback = stringResource(R.string.home_listener)
    val firstName = remember(auth, listenerFallback) {
        (auth as? AuthState.SignedIn)?.displayName?.trim()?.split(" ")?.firstOrNull()?.ifBlank { listenerFallback }
            ?: listenerFallback
    }
    val avatarUrl = (auth as? AuthState.SignedIn)?.avatarUrl.orEmpty()
    val haptics = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                VeyraMark(
                    modifier = Modifier.size(38.dp),
                )
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Veyra Music",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.3).sp,
                            ),
                            color = CanopyColors.Text,
                        )
                        StatusPillBadge(
                            label = "STUDIO",
                            status = StatusPillTone.ACTIVE,
                            pulse = true,
                            size = StatusPillSize.SM,
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSearch()
                    },
                    color = CanopyColors.Surface,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    modifier = Modifier.size(42.dp),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            Icons.Rounded.Search,
                            contentDescription = stringResource(R.string.nav_search),
                            tint = CanopyColors.Text.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                Surface(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onProfile()
                    },
                    color = CanopyColors.Surface,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    modifier = Modifier.size(42.dp),
                ) {
                    if (avatarUrl.isNotBlank()) {
                        ArtworkTile(
                            url = avatarUrl,
                            description = firstName,
                            modifier = Modifier.fillMaxSize(),
                            radius = 999,
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                Icons.Rounded.Person,
                                contentDescription = stringResource(R.string.nav_profile),
                                tint = LocalAccent.current,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.home_greeting_user, greeting, firstName),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = (-0.4).sp,
            ),
            color = CanopyColors.Text,
        )
    }
}

/**
 * Spotify-style 2-column x 3-row compact grid for quick access favorites.
 */
@Composable
private fun SpotifyQuickGrid(
    items: List<QuickGridItem>,
) {
    val rows = remember(items) { items.chunked(2) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        QuickGridCard(
                            item = item,
                        )
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Individual Spotify-style quick grid item card con relieve Enterprise Slate de Desktop.
 */
@Composable
private fun QuickGridCard(
    item: QuickGridItem,
) {
    val shape = RoundedCornerShape(12.dp)
    val cardBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF151F31), Color(0xFF0F1624)),
    )
    val haptics = LocalHapticFeedback.current
    Surface(
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .tvFocusable(shape = shape) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                item.onClick()
            }
            .clip(shape)
            .background(cardBrush),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    .then(
                        if (item.gradient != null) Modifier.background(item.gradient)
                        else Modifier
                    )
            ) {
                if (item.artworkUrl.isNotBlank()) {
                    ArtworkTile(item.artworkUrl, item.title, Modifier.fillMaxSize(), 0)
                } else if (item.icon != null) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            item.icon,
                            contentDescription = item.title,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                ),
                color = CanopyColors.Text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
            )
        }
    }
}


/**
 * Featured Mix Hero Carousel banner with Apple Music editorial tags and ArchiveTune visual style.
 */
@Composable
private fun FeaturedHeroCarousel(
    items: List<CatalogItem>,
    onPlay: (CatalogItem) -> Unit,
    onClick: (CatalogItem) -> Unit,
) {
    if (items.isEmpty()) return
    val pageCount = items.size.coerceAtMost(5)
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val shape = RoundedCornerShape(26.dp)

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(255.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
        ) { page ->
            val item = items[page]
            val subtitle = catalogSubtitle(item).ifBlank { stringResource(R.string.home_curated_mix_desc) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .tvFocusable(shape = shape) { onClick(item) }
                    .clip(shape)
                    .border(BorderStroke(1.dp, CanopyColors.Rule), shape)
            ) {
                // Background Artwork
                ArtworkTile(
                    url = item.artworkUrl,
                    description = item.title,
                    modifier = Modifier.fillMaxSize(),
                    radius = 0,
                )

                // Dark gradient scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Black.copy(alpha = 0.20f),
                                0.35f to Color.Black.copy(alpha = 0.40f),
                                1f to Color.Black.copy(alpha = 0.88f),
                            )
                        )
                )

                // Hero Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Editorial kind badge con StatusPillBadge
                    StatusPillBadge(
                        label = catalogItemBadge(item).uppercase(),
                        status = StatusPillTone.ACTIVE,
                        size = StatusPillSize.SM,
                    )

                    // Bottom info and play button
                    Column {
                        Text(
                            text = item.title,
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.80f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            // Circular glowing play button matching playerbar styling
                            val heroHaptics = LocalHapticFeedback.current
                            Surface(
                                onClick = {
                                    heroHaptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onPlay(item)
                                },
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.94f),
                                shadowElevation = 4.dp,
                                modifier = Modifier.size(48.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        Icons.Rounded.PlayArrow,
                                        contentDescription = stringResource(R.string.action_play),
                                        tint = Color(0xFF101318),
                                        modifier = Modifier.size(28.dp),
                                    )
                                }
                            }

                            // Center Pager Dots
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(end = 8.dp),
                            ) {
                                repeat(pageCount) { index ->
                                    val isSelected = pagerState.currentPage == index
                                    Box(
                                        modifier = Modifier
                                            .height(5.dp)
                                            .width(if (isSelected) 16.dp else 5.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) Color.White else Color.White.copy(alpha = 0.35f)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Artwork card for "Made for you" section with bottom-right floating play button.
 */
@Composable
private fun GlassSquircleCard(
    item: CatalogItem,
    onClick: () -> Unit,
    onPlay: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .width(140.dp)
            .aspectRatio(1f)
            .tvFocusable(shape = shape, onClick = onClick)
            .clip(shape)
            .border(BorderStroke(1.dp, CanopyColors.Rule), shape)
    ) {
        ArtworkTile(item.artworkUrl, item.title, Modifier.fillMaxSize(), 0)

        // Gradient scrim at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.40f to Color.Black.copy(alpha = 0.30f),
                        1f to Color.Black.copy(alpha = 0.85f),
                    )
                )
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                ),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(end = 6.dp),
            )

            // Mini circular play button
            Surface(
                onClick = onPlay,
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.92f),
                shadowElevation = 3.dp,
                modifier = Modifier.size(32.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        contentDescription = stringResource(R.string.home_play_item_cd, item.title),
                        tint = Color(0xFF101318),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

/**
 * Compact card for "Jump back in" section.
 */
@Composable
private fun GlassJumpBackInCard(
    item: CatalogItem,
    onClick: () -> Unit,
    onPlay: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Surface(
        shape = shape,
        color = CanopyColors.Surface,
        border = BorderStroke(1.dp, CanopyColors.Rule),
        modifier = Modifier
            .width(220.dp)
            .height(68.dp)
            .tvFocusable(shape = shape, onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(BorderStroke(1.dp, CanopyColors.Rule), RoundedCornerShape(10.dp)),
            ) {
                ArtworkTile(item.artworkUrl, item.title, Modifier.fillMaxSize(), 10)
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    ),
                    color = CanopyColors.Text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = catalogSubtitle(item),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = CanopyColors.Muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(4.dp))
            Surface(
                onClick = onPlay,
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.92f),
                shadowElevation = 2.dp,
                modifier = Modifier.size(32.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        contentDescription = stringResource(R.string.action_play),
                        tint = Color(0xFF101318),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

/**
 * Vertical rounded card for "Recently played" section.
 */
@Composable
private fun GlassRecentlyPlayedCard(
    item: CatalogItem,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .width(120.dp)
            .tvFocusable(shape = shape, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape)
                .border(BorderStroke(1.dp, CanopyColors.Rule), shape)
        ) {
            ArtworkTile(item.artworkUrl, item.title, Modifier.fillMaxSize(), 14)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
            ),
            color = CanopyColors.Text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = catalogSubtitle(item),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = CanopyColors.Muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Section Header matching Apple Music & Spotify aesthetic with optional subtitle and see-all button.
 */
@Composable
private fun HomeSectionHeader(
    title: String,
    subtitle: String? = null,
    itemCount: Int? = null,
    onSeeAll: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = (-0.3).sp,
                    ),
                    color = CanopyColors.Text,
                )
                if (itemCount != null && itemCount > 0) {
                    StatusPillBadge(
                        label = "$itemCount",
                        status = StatusPillTone.MUTED,
                        size = StatusPillSize.SM,
                        dot = false,
                    )
                }
            }
            if (!subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = CanopyColors.Muted,
                )
            }
        }
        if (onSeeAll != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onSeeAll)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = stringResource(R.string.action_see_all),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                    ),
                    color = CanopyColors.Muted,
                )
                Spacer(Modifier.width(2.dp))
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = CanopyColors.Muted,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun PlaylistCard(item: CatalogItem, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .width(140.dp)
            .tvFocusable(shape = shape, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape)
                .border(BorderStroke(1.dp, CanopyColors.Rule), shape),
        ) {
            ArtworkTile(item.artworkUrl, item.title, Modifier.fillMaxSize(), 14)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = CanopyColors.Text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = catalogSubtitle(item),
            style = MaterialTheme.typography.bodySmall,
            color = CanopyColors.Muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Circular artist avatar card matching ArchiveTune "Keep listening" section.
 */
@Composable
private fun CircularArtistCard(item: CatalogItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(88.dp)
            .tvFocusable(shape = CircleShape, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(BorderStroke(1.dp, CanopyColors.Rule), CircleShape),
        ) {
            ArtworkTile(item.artworkUrl, item.title, Modifier.fillMaxSize(), 999)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
            ),
            color = CanopyColors.Text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = catalogSubtitle(item).ifBlank { stringResource(R.string.home_artist_label) },
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
            ),
            color = CanopyColors.Muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * Apple Music style ranked / latest song row with explicit badge, heart like, and options menu.
 */
@Composable
private fun RankedSongRow(
    rank: Int? = null,
    track: Track,
    liked: Boolean,
    onPlay: () -> Unit,
    onToggleLike: () -> Unit,
    onPlayNext: ((Track) -> Unit)?,
    onAddToQueue: ((Track) -> Unit)?,
    onAddToPlaylist: ((Track) -> Unit)?,
    onShare: ((Track) -> Unit)?,
) {
    var popupOpen by remember { mutableStateOf(false) }

    if (popupOpen) {
        dev.sfg.veyra.mobile.ui.components.TrackActionsPopup(
            track = track,
            onDismiss = { popupOpen = false },
            onPlay = onPlay,
            onPlayNext = onPlayNext?.let { { it(track) } },
            onAddToQueue = onAddToQueue?.let { { it(track) } },
            onAddToPlaylist = onAddToPlaylist?.let { { it(track) } },
            onShare = onShare?.let { { it(track) } },
        )
    }

    val rowShape = RoundedCornerShape(10.dp)
    Surface(
        color = Color.Transparent,
        shape = rowShape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .tvFocusable(shape = rowShape, onClick = onPlay),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (rank != null) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    ),
                    color = CanopyColors.Muted,
                    modifier = Modifier.width(22.dp),
                )
                Spacer(Modifier.width(6.dp))
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                ArtworkTile(track.artworkUrl, track.title, Modifier.fillMaxSize(), 12)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.5.sp,
                        ),
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
                Spacer(Modifier.height(2.dp))
                Text(
                    text = track.artist.ifBlank { stringResource(R.string.home_unknown_artist) },
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = CanopyColors.Muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(
                onClick = onToggleLike,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    if (liked) Icons.Filled.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription =
                        if (liked) stringResource(R.string.home_unlike_cd)
                        else stringResource(R.string.home_like_cd),
                    tint = if (liked) CanopyColors.Favorite else CanopyColors.Muted,
                    modifier = Modifier.size(18.dp),
                )
            }
            IconButton(
                onClick = { popupOpen = true },
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    Icons.Rounded.MoreHoriz,
                    contentDescription = stringResource(R.string.home_options_cd),
                    tint = CanopyColors.Muted,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

private inline fun <reified T : CatalogItem> extractItemsOfKind(
    state: LoadState<List<CatalogSection>>
): List<T> {
    if (state !is LoadState.Content) return emptyList()
    return state.value.flatMap { it.items }.filterIsInstance<T>()
}

@Composable
private fun catalogItemBadge(item: CatalogItem): String =
    when (item) {
        is CatalogItem.Song -> stringResource(R.string.collect_kind_song)
        is CatalogItem.Record -> stringResource(R.string.collect_kind_album)
        is CatalogItem.Performer -> stringResource(R.string.collect_kind_artist)
        is CatalogItem.Collection -> {
            if (item.playlist.title.contains("mix", ignoreCase = true) ||
                item.playlist.title.contains("radio", ignoreCase = true)
            ) {
                "MIX"
            } else {
                stringResource(R.string.collect_kind_playlist)
            }
        }
        is CatalogItem.Category -> stringResource(R.string.collect_kind_category)
    }

@Composable
private fun catalogSubtitle(item: CatalogItem): String =
    when (item) {
        is CatalogItem.Song -> dev.sfg.veyra.mobile.ui.components.localizedArtistName(item.track.artist)
        is CatalogItem.Record -> item.album.artist
        is CatalogItem.Performer ->
            item.artist.subtitle.ifBlank { stringResource(R.string.home_artist_label) }
        is CatalogItem.Collection ->
            item.playlist.author.ifBlank { stringResource(R.string.home_playlist_label) }
        is CatalogItem.Category -> ""
    }

private fun openItem(item: CatalogItem, play: (Track) -> Unit, detail: (String) -> Unit) {
    when (item) {
        is CatalogItem.Song -> play(item.track)
        else -> detail(item.stableId)
    }
}
