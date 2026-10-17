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

import dev.sfg.veyra.mobile.model.Artist
import dev.sfg.veyra.mobile.model.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NowPlayingArtistActionTest {
    @Test
    fun multipleArtistsOpenSelectionDialog() {
        var dialogOpened = false
        val openedCollections = mutableListOf<String>()
        val track = Track(
            id = "track",
            title = "Duet",
            artist = "One & Two",
            artists = listOf(Artist("UC1", "One"), Artist("UC2", "Two")),
        )

        val action = artistOpenAction(track, openedCollections::add) { dialogOpened = true }
        action?.invoke()

        assertTrue(dialogOpened)
        assertTrue(openedCollections.isEmpty())
    }

    @Test
    fun singleArtistOpensArtistDirectly() {
        var dialogOpened = false
        val openedCollections = mutableListOf<String>()
        val track = Track(
            id = "track",
            title = "Solo",
            artist = "One",
            artists = listOf(Artist("UC1", "One")),
        )

        val action = artistOpenAction(track, openedCollections::add) { dialogOpened = true }
        action?.invoke()

        assertFalse(dialogOpened)
        assertEquals(listOf("UC1"), openedCollections)
    }

    @Test
    fun invalidAndDuplicateArtistsDoNotCreateExtraChoices() {
        val track = Track(
            id = "track",
            title = "Solo",
            artist = "One",
            artists = listOf(
                Artist("", "Missing"),
                Artist("UC1", "One"),
                Artist("UC1", "One again"),
            ),
        )

        assertEquals(listOf(Artist("UC1", "One")), selectableTrackArtists(track))
    }
}
