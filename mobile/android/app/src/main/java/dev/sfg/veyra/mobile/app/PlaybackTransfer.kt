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

import android.content.Context
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.connect.protocol.ConnectCommand
import dev.sfg.veyra.mobile.VeyraGraph
import dev.sfg.veyra.mobile.connect.PlaybackTargetCoordinator
import dev.sfg.veyra.mobile.model.PlaybackTarget
import dev.sfg.veyra.mobile.playback.LocalPlaybackController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

suspend fun performTransferToRemote(
    target: PlaybackTarget.Remote,
    local: LocalPlaybackController,
    graph: VeyraGraph,
    coordinator: PlaybackTargetCoordinator,
    context: Context,
) {
    val source = local.snapshot.value
    val beforeTransfer = graph.connect.snapshot.value
    local.pause()
    if (!graph.connect.transfer(source.currentTrack, source.positionMs)) {
        coordinator.failTransfer(context.getString(R.string.msg_transfer_failed))
        return
    }
    val confirmed = withTimeoutOrNull(12_000) {
        graph.connect.snapshot.first { remote ->
            val expected = source.currentTrack
            remote != beforeTransfer && remote.currentTrack != null &&
                (expected == null || remote.currentTrack.id == expected.id ||
                remote.currentTrack.title.equals(expected.title, true))
        }
    }
    if (confirmed == null) {
        coordinator.failTransfer(context.getString(R.string.msg_transfer_no_confirm))
    } else {
        coordinator.completeTransfer(target)
        confirmed.currentTrack?.let(graph.library::recordPlayed)
    }
}

suspend fun performTransferToPhone(
    local: LocalPlaybackController,
    graph: VeyraGraph,
    coordinator: PlaybackTargetCoordinator,
    context: Context,
) {
    val remote = graph.connect.snapshot.value
    if (remote.isPlaying && !graph.connect.send(ConnectCommand.Pause)) {
        coordinator.failTransfer(context.getString(R.string.msg_transfer_pause_failed))
        return
    }
    val pauseConfirmed = !remote.isPlaying || withTimeoutOrNull(5_000) {
        graph.connect.snapshot.first { !it.isPlaying }
    } != null
    if (!pauseConfirmed) {
        coordinator.failTransfer(context.getString(R.string.msg_transfer_pause_no_confirm))
        return
    }
    val queue = remote.queue.toMutableList().apply {
        val current = remote.currentTrack
        if (current != null && none { it.id == current.id }) add(0, current)
    }
    if (queue.isNotEmpty()) {
        val index = remote.currentTrack?.let { current -> queue.indexOfFirst { it.id == current.id } }
            ?.takeIf { it >= 0 } ?: remote.currentIndex.coerceIn(0, queue.lastIndex)
        local.replaceQueue(queue, index, remote.positionMs, play = true, contextTitle = remote.contextTitle.ifBlank { context.getString(R.string.playerx_connected_device) })
    }
    coordinator.completeTransfer(PlaybackTarget.LocalPhone)
}
