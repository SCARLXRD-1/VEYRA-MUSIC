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

package dev.sfg.veyra.mobile.connect

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.connect.client.VeyraConnectClient
import dev.sfg.veyra.connect.discovery.ConnectDiscovery
import dev.sfg.veyra.connect.discovery.LanEndpointDiscovery
import dev.sfg.veyra.connect.protocol.ConnectAnalysisResults
import dev.sfg.veyra.connect.protocol.ConnectClientError
import dev.sfg.veyra.connect.protocol.ConnectClientStatus
import dev.sfg.veyra.connect.protocol.ConnectCommand
import dev.sfg.veyra.connect.protocol.ConnectResults
import dev.sfg.veyra.connect.protocol.ConnectSnapshot
import dev.sfg.veyra.connect.protocol.ConnectTrack
import dev.sfg.veyra.connect.session.AndroidKeystoreConnectSessionStore
import dev.sfg.veyra.connect.session.SecureDeviceTokenGenerator
import dev.sfg.veyra.connect.transport.OkHttpSocketIoTransportFactory
import dev.sfg.veyra.mobile.model.DeviceAvailability
import dev.sfg.veyra.mobile.model.DeviceType
import dev.sfg.veyra.mobile.model.PlaybackDevice
import dev.sfg.veyra.mobile.model.PlaybackSnapshot
import dev.sfg.veyra.mobile.model.PlaybackStatus
import dev.sfg.veyra.mobile.model.RepeatMode
import dev.sfg.veyra.mobile.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.net.URI
import java.util.concurrent.Executor

/**
 * Application-lifetime adapter from the desktop Connect protocol to observable
 * mobile device/playback state. Transport details never reach UI components.
 */
class ConnectDeviceRepository(
    context: Context,
    private val scope: CoroutineScope,
) : VeyraConnectClient.Listener {
    private val appContext = context.applicationContext
    private val sessionStore = AndroidKeystoreConnectSessionStore(context)
    private val transportFactory = OkHttpSocketIoTransportFactory()
    private val discovery = LanEndpointDiscovery()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val client = VeyraConnectClient(
        transportFactory = transportFactory,
        sessionStore = sessionStore,
        tokenGenerator = SecureDeviceTokenGenerator(),
        deviceName = "${Build.MANUFACTURER} ${Build.MODEL}".trim(),
        callbackExecutor = Executor(mainHandler::post),
        listener = this,
    )
    private val mutableStatus = MutableStateFlow(ConnectClientStatus.DISCONNECTED)
    val status: StateFlow<ConnectClientStatus> = mutableStatus.asStateFlow()
    private val mutableProtocolVersion = MutableStateFlow(1)
    val protocolVersion: StateFlow<Int> = mutableProtocolVersion.asStateFlow()
    private val mutableAudioEngine = MutableStateFlow(dev.sfg.veyra.connect.protocol.ConnectAudioEngine())
    val audioEngine: StateFlow<dev.sfg.veyra.connect.protocol.ConnectAudioEngine> = mutableAudioEngine.asStateFlow()
    private val mutableRemoteVolume = MutableStateFlow(1.0f)
    val remoteVolume: StateFlow<Float> = mutableRemoteVolume.asStateFlow()
    private val mutableSnapshot = MutableStateFlow(PlaybackSnapshot())
    val snapshot: StateFlow<PlaybackSnapshot> = mutableSnapshot.asStateFlow()
    private val mutableDevices = MutableStateFlow<List<PlaybackDevice>>(emptyList())
    val devices: StateFlow<List<PlaybackDevice>> = mutableDevices.asStateFlow()
    private val mutableDevice = MutableStateFlow<PlaybackDevice?>(null)
    val device: StateFlow<PlaybackDevice?> = mutableDevice.asStateFlow()
    private val mutableRemoteAnalysis = MutableStateFlow<Map<String, dev.sfg.veyra.mobile.playback.smart.TrackFeatures.Features>>(emptyMap())
    val remoteAnalysis: StateFlow<Map<String, dev.sfg.veyra.mobile.playback.smart.TrackFeatures.Features>> = mutableRemoteAnalysis.asStateFlow()
    private val mutableRemoteCommands = MutableSharedFlow<ConnectCommand>(extraBufferCapacity = 64)
    val remoteCommands: SharedFlow<ConnectCommand> = mutableRemoteCommands.asSharedFlow()
    private val mutableMessage = MutableStateFlow("")
    val message: StateFlow<String> = mutableMessage.asStateFlow()
    @Volatile private var serverUrl = ""
    @Volatile private var manualDisconnect = false
    private var reconnectJob: Job? = null
    private var reconnectAttempt = 0
    private var discoveryJob: Job? = null
    private val discoveredUrls = mutableMapOf<String, String>()
    private val onlineStatusCache = mutableMapOf<String, Boolean>()
    private data class PendingTransfer(val track: Track, val positionMs: Long)
    private var pendingTransfer: PendingTransfer? = null

    init {
        restoreKnownDevices()
    }

    fun pair(input: String) {
        val parsed = ConnectDiscovery.parsePairingInput(input)
        val rawServer = parsed.serverUrl.ifEmpty { ConnectDiscovery.cleanServerUrl(input) }
        if (rawServer.isBlank()) {
            mutableMessage.value = appContext.getString(R.string.conn_enter_pairing_link)
            return
        }
        scope.launch(Dispatchers.IO) {
            runCatching {
                val host = ConnectDiscovery.serverHost(rawServer)
                val target = discovery.discover(rawServer, host).ifBlank { rawServer }
                val defaultName = runCatching { URI(target).host }.getOrNull().orEmpty().ifBlank { "Orchard desktop" }
                val pairedDevice = dev.sfg.veyra.connect.session.StoredPairedDevice(
                    id = target,
                    serverUrl = target,
                    serverHost = host,
                    deviceToken = parsed.token,
                    defaultName = defaultName,
                    pairedAt = System.currentTimeMillis(),
                    lastSeenAt = System.currentTimeMillis()
                )
                sessionStore.saveDevice(pairedDevice)
                target
            }.onSuccess { target ->
                serverUrl = target
                manualDisconnect = false
                reconnectAttempt = 0
                reconnectJob?.cancel()
                client.connect(target, parsed.token)
                publishDevices()
                refreshDiscovery()
            }.onFailure { mutableMessage.value = it.message ?: appContext.getString(R.string.conn_device_unreachable) }
        }
    }

    fun connectTo(deviceId: String) {
        scope.launch(Dispatchers.IO) {
            val stored = sessionStore.loadDevices()
            val match = stored.firstOrNull { it.id == deviceId || it.serverUrl == deviceId } ?: return@launch
            val target = discoveredUrls[match.id]?.ifBlank { match.serverUrl } ?: match.serverUrl
            serverUrl = target
            manualDisconnect = false
            reconnectAttempt = 0
            reconnectJob?.cancel()
            client.connect(target, match.deviceToken)
            publishDevices()
        }
    }

    fun renameDevice(deviceId: String, newName: String) {
        scope.launch(Dispatchers.IO) {
            sessionStore.updateDeviceName(deviceId, newName.trim())
            publishDevices()
        }
    }

    fun removeDevice(deviceId: String) {
        scope.launch(Dispatchers.IO) {
            val isActive = serverUrl == deviceId || mutableDevices.value.any { it.id == deviceId && it.isActive }
            if (isActive) {
                manualDisconnect = true
                reconnectJob?.cancel()
                client.disconnect()
                serverUrl = ""
                mutableProtocolVersion.value = 1
                mutableAudioEngine.value = dev.sfg.veyra.connect.protocol.ConnectAudioEngine()
                mutableRemoteVolume.value = 1.0f
                mutableSnapshot.value = PlaybackSnapshot()
                mutableRemoteAnalysis.value = emptyMap()
            }
            sessionStore.removeDevice(deviceId)
            discoveredUrls.remove(deviceId)
            onlineStatusCache.remove(deviceId)
            publishDevices()
        }
    }

    fun disconnect() {
        manualDisconnect = true
        reconnectJob?.cancel()
        client.disconnect()
        scope.launch(Dispatchers.IO) {
            runCatching(sessionStore::clear)
                .onFailure { Log.w(TAG, "Remembered Connect device could not be cleared", it) }
            serverUrl = ""
            mutableProtocolVersion.value = 1
            mutableAudioEngine.value = dev.sfg.veyra.connect.protocol.ConnectAudioEngine()
            mutableRemoteVolume.value = 1.0f
            mutableSnapshot.value = PlaybackSnapshot()
            mutableRemoteAnalysis.value = emptyMap()
            discoveredUrls.clear()
            onlineStatusCache.clear()
            publishDevices()
        }
    }

    fun send(command: ConnectCommand): Boolean {
        val accepted = client.send(command)
        if (!accepted) mutableMessage.value = appContext.getString(R.string.conn_device_offline)
        return accepted
    }

    fun setVolume(volume: Float): Boolean = send(ConnectCommand.Volume(volume.toDouble().coerceIn(0.0, 1.0)))

    fun toggleShuffle(): Boolean = send(ConnectCommand.ToggleShuffle)

    fun cycleRepeat(): Boolean = send(ConnectCommand.CycleRepeat)

    fun moveQueueIndex(from: Int, to: Int): Boolean = send(ConnectCommand.MoveQueueIndex(from, to))

    fun removeQueueIndex(index: Int): Boolean = send(ConnectCommand.RemoveQueueIndex(index))

    fun playQueueIndex(index: Int): Boolean = send(ConnectCommand.PlayQueueIndex(index))

    fun clearUpcoming(): Boolean = send(ConnectCommand.ClearUpcoming)

    fun playNext(track: Track): Boolean = send(ConnectCommand.PlayNext(track.toPayload()))

    fun addToQueue(track: Track): Boolean = send(ConnectCommand.AddToQueue(track.toPayload()))

    fun setAudioEnginePreset(preset: String): Boolean = send(ConnectCommand.AudioEnginePreset(preset))

    fun toggleAutoEq(enabled: Boolean): Boolean = send(ConnectCommand.AutoEq(enabled))

    fun toggleManualEq(enabled: Boolean): Boolean = send(ConnectCommand.ManualEq(enabled))

    fun requestRemoteAnalysis(trackIds: List<String>): Boolean {
        if (trackIds.isEmpty()) return false
        val requestId = "analysis-${System.currentTimeMillis()}"
        return client.requestAnalysis(trackIds, requestId)
    }

    /** Search the desktop for an equivalent playable payload before transfer. */
    fun transfer(track: Track?, positionMs: Long = 0): Boolean {
        if (track == null) {
            return send(ConnectCommand.Play)
        }
        pendingTransfer = PendingTransfer(track, positionMs)
        val requestId = "transfer-${System.currentTimeMillis()}"
        if (!client.search("${track.title} ${track.artist}", requestId)) {
            pendingTransfer = null
            mutableMessage.value = appContext.getString(R.string.conn_connect_before_transfer)
            return false
        }
        return true
    }

    fun sendDeviceState(snapshot: PlaybackSnapshot, autoplay: Boolean): Boolean {
        if (client.status() != ConnectClientStatus.APPROVED) return false
        val payload = dev.sfg.veyra.connect.protocol.ConnectJsonCodec.deviceState(snapshot, client.protocolVersion(), autoplay)
        return client.sendDeviceState(payload)
    }

    override fun onStatusChanged(status: ConnectClientStatus) {
        mutableStatus.value = status
        mutableProtocolVersion.value = client.protocolVersion()
        publishDevices()
        when (status) {
            ConnectClientStatus.APPROVED -> {
                reconnectAttempt = 0
                reconnectJob?.cancel()
                mutableMessage.value = appContext.getString(R.string.conn_connected_format, deviceName())
            }
            ConnectClientStatus.DISCONNECTED -> if (!manualDisconnect && serverUrl.isNotBlank()) scheduleReconnect()
            ConnectClientStatus.AWAITING_APPROVAL -> mutableMessage.value = appContext.getString(R.string.conn_approve_phone)
            ConnectClientStatus.PAIRING_EXPIRED -> mutableMessage.value = appContext.getString(R.string.conn_pairing_expired)
            ConnectClientStatus.REJECTED -> mutableMessage.value = appContext.getString(R.string.conn_request_declined)
            ConnectClientStatus.REVOKED -> mutableMessage.value = appContext.getString(R.string.conn_access_revoked)
            else -> Unit
        }
    }

    override fun onSnapshot(snapshot: ConnectSnapshot) {
        mutableProtocolVersion.value = snapshot.protocolVersion
        mutableAudioEngine.value = snapshot.audioEngine
        mutableRemoteVolume.value = snapshot.playback.volume.toFloat()
        mutableSnapshot.value = snapshot.toPlaybackSnapshot()
    }

    override fun onSearchResults(results: ConnectResults) {
        val transfer = pendingTransfer ?: return
        pendingTransfer = null
        val match = results.items.firstOrNull { it.track.id == transfer.track.id }
            ?: results.items.firstOrNull {
                it.track.title.equals(transfer.track.title, true) && it.track.artist.contains(transfer.track.artist, true)
            }
        if (match == null) {
            mutableMessage.value = appContext.getString(R.string.conn_track_unavailable)
            return
        }
        client.send(ConnectCommand.PlayTrack(match))
        if (transfer.positionMs > 1_500) client.send(ConnectCommand.Seek(transfer.positionMs / 1_000.0))
    }

    override fun onLibraryResults(results: ConnectResults) = Unit

    override fun onAnalysisResults(results: ConnectAnalysisResults) {
        val mapped = results.results.associate { item ->
            item.id to dev.sfg.veyra.mobile.playback.smart.TrackFeatures.Features(
                duration = item.duration,
                bpm = item.bpm,
                beatInterval = if (item.bpm > 0) 60.0 / item.bpm else 0.0,
                firstBeat = item.cueIn,
                beatConfidence = item.beatConfidence,
                key = item.musicalKey,
                keyConfidence = item.keyConfidence,
                audibleStartTime = item.cueIn,
                pickupTime = 0.0,
                introEndTime = item.cueIn,
                outroStartTime = if (item.cueOut > 0) item.cueOut else item.duration,
                contentEndTime = if (item.cueOut > 0) item.cueOut else item.duration,
                mixInTime = item.cueIn,
                mixOutTime = if (item.cueOut > 0) item.cueOut else item.duration,
                vocalProbability = 0.0,
                downbeats = emptyList(),
                phraseBoundaries = emptyList(),
                vocalActivityMask = emptyList(),
                energyCurve = emptyList(),
                lowEnergyCurve = emptyList(),
                mixInCandidates = emptyList(),
                mixOutCandidates = emptyList(),
            )
        }
        mutableRemoteAnalysis.value = mutableRemoteAnalysis.value + mapped
    }

    override fun onCommandReceived(command: ConnectCommand) {
        mutableRemoteCommands.tryEmit(command)
    }

    override fun onError(error: ConnectClientError) {
        Log.w(TAG, error.message, error)
        mutableMessage.value = error.message
    }

    private fun restoreKnownDevices() {
        scope.launch(Dispatchers.IO) {
            runCatching { sessionStore.loadDevices() }.onSuccess { storedList ->
                if (storedList.isNotEmpty()) {
                    val primary = storedList.first()
                    serverUrl = primary.serverUrl
                    publishDevices()
                    if (serverUrl.isNotBlank()) {
                        client.connect(serverUrl, primary.deviceToken)
                    }
                    refreshDiscovery()
                }
            }.onFailure { Log.w(TAG, "Known Connect devices could not be restored", it) }
        }
    }

    private fun refreshDiscovery() {
        discoveryJob?.cancel()
        discoveryJob = scope.launch(Dispatchers.IO) {
            val devices = sessionStore.loadDevices()
            devices.forEach { dev ->
                val resolved = runCatching { discovery.discover(dev.serverUrl, dev.serverHost) }.getOrNull().orEmpty()
                if (resolved.isNotBlank()) {
                    discoveredUrls[dev.id] = resolved
                    onlineStatusCache[dev.id] = true
                } else {
                    onlineStatusCache[dev.id] = false
                }
            }
            publishDevices()
        }
    }

    private fun scheduleReconnect() {
        if (reconnectJob?.isActive == true) return
        reconnectJob = scope.launch {
            while (!manualDisconnect && serverUrl.isNotBlank()) {
                delay(ConnectReconnectPolicy.delayMs(reconnectAttempt))
                reconnectAttempt += 1
                if (manualDisconnect || serverUrl.isBlank()) return@launch
                client.connect(serverUrl)

                // Ignore the StateFlow's pre-connect DISCONNECTED value, then
                // wait for this transport attempt to leave CONNECTING. Pending
                // approval remains connected and therefore needs no retry.
                val outcome = withTimeoutOrNull(CONNECTION_ATTEMPT_TIMEOUT_MS) {
                    status.dropWhile { it == ConnectClientStatus.DISCONNECTED }
                        .first { it != ConnectClientStatus.CONNECTING }
                } ?: ConnectClientStatus.DISCONNECTED.also { client.disconnect() }
                if (!ConnectReconnectPolicy.shouldRetry(outcome)) return@launch
            }
        }
    }

    private fun publishDevices() {
        val stored = sessionStore.loadDevices()
        val mapped = stored.map { dev ->
            val isCurrent = (dev.id == serverUrl || dev.serverUrl == serverUrl)
            val isOnline = when {
                isCurrent -> client.status() == ConnectClientStatus.APPROVED
                onlineStatusCache[dev.id] == true -> true
                else -> false
            }
            val devName = dev.defaultName.ifBlank {
                runCatching { URI(dev.serverUrl).host }.getOrNull().orEmpty().ifBlank { "Orchard desktop" }
            }
            PlaybackDevice(
                id = dev.id,
                name = devName,
                customName = dev.customName,
                type = DeviceType.COMPUTER,
                availability = if (isOnline) DeviceAvailability.ONLINE else DeviceAvailability.OFFLINE,
                serverUrl = dev.serverUrl,
                lastSeenAt = dev.lastSeenAt,
            )
        }
        mutableDevices.value = mapped
        mutableDevice.value = mapped.firstOrNull { it.id == serverUrl || it.serverUrl == serverUrl } ?: mapped.firstOrNull()
    }

    private fun deviceName(): String {
        val activeDev = mutableDevices.value.firstOrNull { it.id == serverUrl || it.serverUrl == serverUrl }
        if (activeDev != null && activeDev.displayName.isNotBlank()) {
            return activeDev.displayName
        }
        return runCatching { URI(serverUrl).host }.getOrNull().orEmpty().ifBlank { "Orchard desktop" }
    }

    private fun ConnectSnapshot.toPlaybackSnapshot(): PlaybackSnapshot {
        val tracks = queue.map { it.toTrack() }
        val active = track?.toTrack()
        val currentIndex = active?.let { current -> tracks.indexOfFirst { it.id == current.id } } ?: -1
        val mappedRepeat = when (playback.repeatMode.lowercase()) {
            "one" -> RepeatMode.ONE
            "all" -> RepeatMode.ALL
            else -> RepeatMode.OFF
        }
        return PlaybackSnapshot(
            status = when {
                playback.buffering -> PlaybackStatus.BUFFERING
                playback.isPlaying -> PlaybackStatus.PLAYING
                active != null -> PlaybackStatus.PAUSED
                else -> PlaybackStatus.IDLE
            },
            currentTrack = active,
            queue = tracks,
            currentIndex = currentIndex,
            positionMs = (playback.currentTime * 1_000).toLong(),
            durationMs = (playback.duration * 1_000).toLong(),
            bufferedPositionMs = 0,
            isPlaying = playback.isPlaying,
            volume = playback.volume.toFloat().coerceIn(0.0f, 1.0f),
            shuffle = playback.shuffle,
            repeatMode = mappedRepeat,
        )
    }

    private fun ConnectTrack.toTrack() = Track(
        id = id,
        title = title,
        artist = artist,
        album = album,
        artworkUrl = artwork.ifBlank { thumbnail },
        animatedArtworkUrl = animatedArtwork,
        animatedArtworkVerticalUrl = animatedArtworkVertical,
    )

    private fun Track.toPayload(): org.json.JSONObject = org.json.JSONObject()
        .put("id", id)
        .put("title", title)
        .put("artist", artist)
        .put("album", album)
        .put("thumbnail", artworkUrl)
        .put("artwork", artworkUrl)
        .put("durationSeconds", durationMs / 1000.0)

    companion object {
        private const val TAG = "ConnectDeviceRepository"
        private const val CONNECTION_ATTEMPT_TIMEOUT_MS = 15_000L
    }
}
