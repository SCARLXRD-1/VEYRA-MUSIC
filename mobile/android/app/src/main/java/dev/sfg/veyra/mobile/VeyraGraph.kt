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

package dev.sfg.veyra.mobile

import android.content.Context
import dev.sfg.veyra.mobile.artwork.ArtistImageRepository
import dev.sfg.veyra.mobile.artwork.ArtworkRepository
import dev.sfg.veyra.mobile.auth.NativeYouTubeAuthRepository
import dev.sfg.veyra.mobile.auth.SecureYouTubeSessionStore
import dev.sfg.veyra.mobile.catalog.CatalogRepository
import dev.sfg.veyra.mobile.catalog.AudioVersionResolver
import dev.sfg.veyra.mobile.catalog.VideoVersionResolver
import dev.sfg.veyra.mobile.catalog.InnerTubeClient
import dev.sfg.veyra.mobile.catalog.PlaylistActions
import dev.sfg.veyra.mobile.connect.ConnectDeviceRepository
import dev.sfg.veyra.mobile.library.LibraryCache
import dev.sfg.veyra.mobile.library.LibraryRepository
import dev.sfg.veyra.mobile.lyrics.LyricsRepository
import dev.sfg.veyra.mobile.lastfm.LastfmRepository
import dev.sfg.veyra.mobile.listenbrainz.ListenBrainzRepository
import dev.sfg.veyra.mobile.settings.SettingsRepository
import dev.sfg.veyra.mobile.download.DownloadManager
import dev.sfg.veyra.mobile.playback.YouTubePoTokenMinter
import dev.sfg.veyra.mobile.playback.smart.BestMixFeatureStore
import dev.sfg.veyra.mobile.songlinks.SongLinksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Explicit process graph; presentation code receives repositories, not Android
 * singletons or transport objects. The graph owns only application-lifetime work.
 */
class VeyraGraph(context: Context) {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()
    // The loader is deferred so auth and innerTube can depend on each other without a cycle.
    val auth = NativeYouTubeAuthRepository(
        store = SecureYouTubeSessionStore(context),
        context = context,
        scope = applicationScope,
        profileLoader = { innerTube.accountInfo() },
    )
    val sessionQrPairing = dev.sfg.veyra.mobile.auth.SessionQrPairingRepository(applicationScope, auth)
    private val innerTube: InnerTubeClient = InnerTubeClient(http, auth)
    val settings = SettingsRepository(context, applicationScope)
    val bestMixFeatures = BestMixFeatureStore(context)
    val networkMonitor = dev.sfg.veyra.mobile.network.NetworkMonitor(context)
    /**
     * Shared by playback and downloads: attesting is expensive once and free afterwards, so the
     * two paths must not each stand up their own BotGuard WebView. Created lazily because a
     * WebView is not free and a session that only browses never needs one.
     */
    val poTokenMinter: YouTubePoTokenMinter by lazy { YouTubePoTokenMinter(context) }

    /**
     * Also shared, and also deferred: the attesting client hands back ciphered URLs, so downloads
     * need the same solver playback uses rather than a second WebView fetching the same player.
     */
    val challengeSolver: dev.sfg.veyra.mobile.playback.YouTubeChallengeSolver by lazy {
        dev.sfg.veyra.mobile.playback.YouTubeChallengeSolver(context, http)
    }
    val spotifyCanvas = dev.sfg.veyra.mobile.spotify.SpotifyCanvasRepository(context, http, settings)
    val artwork = ArtworkRepository(http, spotifyCanvas)
    val downloads = DownloadManager(
        context = context,
        http = http,
        sessionProvider = auth,
        scope = applicationScope,
        poTokenMinter = { poTokenMinter },
        challengeSolver = { challengeSolver },
        artworkResolver = artwork::artwork,
        downloadAnimatedArtworkProvider = { settings.settings.value.downloadAnimatedArtwork },
        qualityProvider = { settings.settings.value.audioQuality },
    )
    val artistImages = ArtistImageRepository(http)
    val catalog = CatalogRepository(innerTube)
    val playlistActions = PlaylistActions(innerTube)
    val audioVersions = AudioVersionResolver(innerTube)
    val videoVersions = VideoVersionResolver(innerTube)
    val library = LibraryRepository(LibraryCache(context), catalog, applicationScope)
    val lyrics = LyricsRepository(http, innerTube)
    val connect = ConnectDeviceRepository(context, applicationScope)
    val songLinks = SongLinksRepository(http)
    val lastfm = LastfmRepository(context, http, applicationScope)
    val listenBrainz = ListenBrainzRepository(context, http, applicationScope)

    /**
     * The transition the playback service has planned, or null when there is none.
     *
     * Held on the graph rather than sent through the media session because both live in this
     * process: the session carries what a remote controller needs, and a planned-but-not-started
     * transition is not that. A Connect target simply leaves this null, which is correct; the
     * marker describes local playback.
     */
    /**
     * Stored analysis for a track, set by the playback service once it is running.
     *
     * The transition planner uses this to plan crossfades. Both live in this process, so the lookup
     * is shared directly rather than copied through the media session. Null before playback starts.
     */
    @Volatile
    var analysisLookup: ((dev.sfg.veyra.mobile.model.Track) ->
        dev.sfg.veyra.mobile.playback.smart.TrackAnalysis)? = null

    /**
     * Clear hook for the active playback service's [StreamCache].
     * Null before the playback service starts or after it is destroyed.
     */
    @Volatile
    var onClearStreamCache: (() -> Unit)? = null



    val transitionMarker = kotlinx.coroutines.flow.MutableStateFlow<dev.sfg.veyra.mobile.model.TransitionMarker?>(null)
    val warningEvent = kotlinx.coroutines.flow.MutableSharedFlow<String>(extraBufferCapacity = 16)
    val activeBitrate = kotlinx.coroutines.flow.MutableStateFlow(0)
    val activeTrackIsQobuz = kotlinx.coroutines.flow.MutableStateFlow(false)
    val qobuz = dev.sfg.veyra.mobile.qobuz.QobuzRepository(context, applicationScope)
    val qobuzResolver = dev.sfg.veyra.mobile.qobuz.QobuzResolver(qobuz, http)
    val discordAuth = dev.sfg.veyra.mobile.discord.DiscordOAuthRepository(context, http, applicationScope)
    val discordPresence = dev.sfg.veyra.mobile.discord.DiscordPresenceCoordinator(
        context = context,
        http = http,
        auth = discordAuth,
        songLinks = songLinks,
        scope = applicationScope,
    )

    fun postWarning(message: String) {
        warningEvent.tryEmit(message)
    }

    val updates = UpdateManager(context) { settings.settings.value.betaChannelEnabled }

    init {
        applicationScope.launch { auth.restore() }
        updates.checkForUpdates()
    }

    companion object {
        fun from(context: Context): VeyraGraph =
            (context.applicationContext as VeyraApplication).graph
    }
}
