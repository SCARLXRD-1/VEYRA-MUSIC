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

package dev.sfg.veyra.mobile.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import dev.sfg.veyra.mobile.model.LibrarySnapshot
import dev.sfg.veyra.mobile.model.OrchardSettings
import dev.sfg.veyra.mobile.model.PlaybackSnapshot
import dev.sfg.veyra.mobile.model.PlaybackTargetState
import dev.sfg.veyra.mobile.model.Track
import dev.sfg.veyra.mobile.model.TransitionMarker
import dev.sfg.veyra.mobile.ui.components.PlaylistPickerSheet
import dev.sfg.veyra.mobile.ui.navigation.Routes
import dev.sfg.veyra.mobile.ui.screens.NowPlayingScreen

/**
 * The full player, presented over the app rather than routed to.
 *
 * Keeping it out of the [androidx.navigation.compose.NavHost] is what lets the screen
 * behind it stay composed: the pull-down uncovers the real page and the real pill instead
 * of an empty back stack. [collapseBounds] is where that pill actually sits, so the
 * dismiss can shrink into it.
 */
@Composable
fun NowPlayingOverlay(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    collapseBounds: Rect?,
    collapseArtworkBounds: Rect?,
    restingCoverBounds: Rect?,
    onRestingCoverBounds: (Rect) -> Unit,
    nav: NavHostController,
    viewModel: VeyraViewModel,
    playback: PlaybackSnapshot,
    transition: TransitionMarker?,
    mixProgress: Float,
    targets: PlaybackTargetState,
    library: LibrarySnapshot,
    settings: OrchardSettings,
    modifier: Modifier = Modifier,
) {
    // Nothing to show once playback is cleared, and no reason to hold the player's
    // state machine alive while it is closed.
    if (!open) return
    val currentTrack = playback.currentTrack ?: return

    val lyrics by viewModel.lyrics.collectAsStateWithLifecycle()
    val activeBitrate by viewModel.activeBitrate.collectAsStateWithLifecycle()
    val activeTrackIsQobuz by viewModel.activeTrackIsQobuz.collectAsStateWithLifecycle()
    val autoplayLoading by viewModel.autoplayLoading.collectAsStateWithLifecycle()
    val autoplayError by viewModel.autoplayError.collectAsStateWithLifecycle()
    val connectProtocolVersion by viewModel.connectProtocolVersion.collectAsStateWithLifecycle()
    val connectRemoteVolume by viewModel.connectRemoteVolume.collectAsStateWithLifecycle()
    val downloadedTrackIds by viewModel.downloadedTrackIds.collectAsStateWithLifecycle()
    val sleepTimerRemainingSeconds by viewModel.sleepTimerRemainingSeconds.collectAsStateWithLifecycle()
    val sleepTimerEndOfTrack by viewModel.sleepTimerEndOfTrack.collectAsStateWithLifecycle()
    val musicVideo by viewModel.musicVideo.collectAsStateWithLifecycle()
    val videoPlayer by viewModel.videoPlayer.collectAsStateWithLifecycle()
    var playlistPickerTrack by remember { mutableStateOf<Track?>(null) }

    val liked = library.likedTracks.any { it.id == currentTrack.id }

    NowPlayingScreen(
        modifier = modifier,
        collapseBounds = collapseBounds,
        collapseArtworkBounds = collapseArtworkBounds,
        restingCoverBounds = restingCoverBounds,
        onRestingCoverBounds = onRestingCoverBounds,
        autoplayEnabled = settings.autoplayEnabled,
        autoplayLoading = autoplayLoading,
        autoplayError = autoplayError,
        onAutoplayEnabled = viewModel::setAutoplayEnabled,
        sleepTimerRemainingSeconds = sleepTimerRemainingSeconds,
        sleepTimerEndOfTrack = sleepTimerEndOfTrack,
        onStartSleepTimer = viewModel::startSleepTimer,
        onStartSleepTimerAtEndOfTrack = viewModel::startSleepTimerAtEndOfTrack,
        onCancelSleepTimer = viewModel::cancelSleepTimer,
        smartCrossfade = settings.smartCrossfade,
        onBestMixUpcoming = viewModel::bestMixUpcoming,
        musicVideo = musicVideo,
        videoPlayer = videoPlayer,
        onToggleMusicVideo = viewModel::toggleMusicVideo,
        playback = playback,
        transition = transition,
        mixProgress = mixProgress,
        targets = targets,
        lyrics = lyrics,
        settings = settings,
        onSettings = viewModel::updateSettings,
        animatedArtworkEnabled = settings.animatedArtwork,
        animatedBackgroundEnabled = settings.animatedBackground,
        gesturesEnabled = settings.playerGesturesEnabled,
        showBitrate = settings.showBitrate,
        bitrateKbps = activeBitrate,
        isQobuz = activeTrackIsQobuz || currentTrack.isQobuz,
        liked = liked,
        protocolVersion = connectProtocolVersion,
        remoteVolume = connectRemoteVolume,
        onRemoteVolumeChange = viewModel::setRemoteVolume,
        onBack = { onOpenChange(false) },
        onToggle = viewModel::togglePlayback,
        onPrevious = viewModel::previous,
        onNext = viewModel::next,
        onSeek = { positionMs ->
            // At the audible handoff the UI is already on the incoming timeline while the engine
            // may still own the outgoing deck. A seek commits to the track the user can see.
            val authoritative = viewModel.playback.value
            if (authoritative.currentTrack?.id != currentTrack.id &&
                playback.currentIndex in playback.queue.indices
            ) {
                viewModel.playQueueIndex(playback.currentIndex)
            }
            viewModel.seek(positionMs)
        },
        onShuffle = viewModel::toggleShuffle,
        onRepeat = viewModel::cycleRepeat,
        onLiked = { viewModel.toggleLiked(currentTrack) },
        onDevices = {
            onOpenChange(false)
            nav.navigate(Routes.DEVICES)
        },
        onPlayQueueIndex = viewModel::playQueueIndex,
        onRemoveQueueIndex = viewModel::removeQueueIndex,
        onMoveQueueItem = viewModel::moveQueueItem,
        onClearUpcoming = viewModel::clearUpcoming,
        downloadedTrackIds = downloadedTrackIds,
        onDownloadTrack = viewModel::downloadTrack,
        onRemoveDownloadTrack = viewModel::removeDownload,
        onAddToPlaylist = { playlistPickerTrack = it },
        onShare = { viewModel.shareTrack(currentTrack) },
        // Closes the player so the collection is not buried underneath it.
        onOpenCollection = { id ->
            onOpenChange(false)
            viewModel.openDetail(id)
            nav.navigate(Routes.detail(id))
        },
    )

    playlistPickerTrack?.let { track ->
        PlaylistPickerSheet(
            track = track,
            playlists = library.savedPlaylists,
            onDismiss = { playlistPickerTrack = null },
            onSelect = { playlist ->
                playlistPickerTrack = null
                viewModel.addTrackToPlaylist(playlist.id, track)
            },
        )
    }
}
