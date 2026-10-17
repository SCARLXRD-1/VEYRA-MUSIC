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

package dev.sfg.veyra.mobile.widget

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.Player
import dev.sfg.veyra.mobile.VeyraGraph
import dev.sfg.veyra.mobile.playback.MediaItemMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Coalesces player events into one durable state write and one widget render pass. */
object VeyraWidgetUpdater {
    private val renderMutex = Mutex()

    fun onPlayerChanged(context: Context, player: Player, forcePaused: Boolean = false) {
        val appContext = context.applicationContext
        val graph = VeyraGraph.from(appContext)
        val current = player.currentMediaItem?.let(MediaItemMapper::toTrack)
        val duration = player.duration
            .takeUnless { it == C.TIME_UNSET }
            ?.coerceAtLeast(0)
            ?: current?.durationMs
            ?: 0
        val isPlaying = player.isPlaying && !forcePaused
        val positionMs = player.currentPosition.coerceAtLeast(0)
        val recentlyPlayed = graph.library.library.value.recentlyPlayed.take(4)
        val state = VeyraWidgetState(
            currentTrack = current,
            recentlyPlayed = recentlyPlayed,
            isPlaying = isPlaying,
            positionMs = positionMs,
            durationMs = duration,
        )
        graph.applicationScope.launch(Dispatchers.IO) {
            OrchardWidgetStateStore(appContext).save(state)
            renderMutex.withLock {
                VeyraWidgetRenderer.updateAll(
                    context = appContext,
                    state = state,
                    client = graph.http,
                )
            }
        }
    }

    fun requestRender(context: Context) {
        val appContext = context.applicationContext
        val graph = VeyraGraph.from(appContext)
        graph.applicationScope.launch(Dispatchers.IO) {
            renderMutex.withLock {
                VeyraWidgetRenderer.updateAll(
                    context = appContext,
                    state = OrchardWidgetStateStore(appContext).load(),
                    client = graph.http,
                )
            }
        }
    }
}
