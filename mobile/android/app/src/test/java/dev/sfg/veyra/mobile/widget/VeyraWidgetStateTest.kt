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

import dev.sfg.veyra.mobile.model.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VeyraWidgetStateTest {
    @Test
    fun `widget state round trips playback and recent tracks`() {
        val current = Track(
            id = "current",
            title = "Sunlit Static",
            artist = "The Orchard",
            album = "Canopy",
            artworkUrl = "https://example.com/current.jpg",
            durationMs = 213_000,
        )
        val recent = (1..6).map { index ->
            Track(id = "recent-$index", title = "Recent $index", artist = "Artist $index")
        }
        val encoded = OrchardWidgetStateCodec.encode(
            VeyraWidgetState(
                currentTrack = current,
                recentlyPlayed = recent,
                isPlaying = true,
                positionMs = 42_000,
                durationMs = 213_000,
            )
        )

        val decoded = OrchardWidgetStateCodec.decode(encoded)

        assertEquals(current, decoded.currentTrack)
        assertEquals(recent.take(4), decoded.recentlyPlayed)
        assertTrue(decoded.isPlaying)
        assertEquals(42_000, decoded.positionMs)
        assertEquals(213_000, decoded.durationMs)
    }

    @Test
    fun `negative positions are clamped at the storage boundary`() {
        val decoded = OrchardWidgetStateCodec.decode(
            OrchardWidgetStateCodec.encode(
                VeyraWidgetState(positionMs = -10, durationMs = -20, isPlaying = false)
            )
        )

        assertEquals(0, decoded.positionMs)
        assertEquals(0, decoded.durationMs)
        assertFalse(decoded.isPlaying)
    }
}
