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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.sfg.veyra.mobile.model.PlaybackSnapshot

@Composable
fun CanopyReadout(
    playback: PlaybackSnapshot,
    onOpen: () -> Unit,
    onToggle: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    onPrevious: () -> Unit = {},
    transition: dev.sfg.veyra.mobile.model.TransitionMarker? = null,
    mixProgress: Float? = null,
    onArtworkBounds: ((androidx.compose.ui.geometry.Rect) -> Unit)? = null,
    onClear: () -> Unit = {},
) {
    GroveFloatingDock(
        playback = playback,
        onTogglePlay = onToggle,
        onNext = onNext,
        onPrevious = onPrevious,
        onClick = onOpen,
        modifier = modifier,
        transition = transition,
        mixProgress = mixProgress,
        onArtworkBounds = onArtworkBounds,
        onClear = onClear,
    )
}
