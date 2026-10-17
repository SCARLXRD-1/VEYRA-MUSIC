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

package dev.sfg.veyra.mobile.ui

import dev.sfg.veyra.mobile.model.Album
import dev.sfg.veyra.mobile.model.Artist
import dev.sfg.veyra.mobile.model.CatalogItem
import dev.sfg.veyra.mobile.model.Playlist
import dev.sfg.veyra.mobile.model.Track
import dev.sfg.veyra.mobile.ui.components.filterCatalogItems
import dev.sfg.veyra.mobile.ui.components.filterTracks
import dev.sfg.veyra.mobile.ui.components.normalizeSearchText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CollectionSearchTest {

    private val track1 = Track(
        id = "t1",
        title = "Blinding Lights",
        artist = "The Weeknd",
        album = "After Hours",
        durationMs = 200_000,
    )

    private val track2 = Track(
        id = "t2",
        title = "Save Your Tears",
        artist = "The Weeknd",
        album = "After Hours",
        durationMs = 215_000,
    )

    private val track3 = Track(
        id = "t3",
        title = "Levitating",
        artist = "Dua Lipa",
        album = "Future Nostalgia",
        durationMs = 203_000,
    )

    private val track4 = Track(
        id = "t4",
        title = "Déjà Vu",
        artist = "Beyoncé",
        album = "B'Day",
        durationMs = 240_000,
    )

    private val tracks = listOf(track1, track2, track3, track4)

    @Test
    fun normalizeSearchTextStripsDiacriticsAndLowercases() {
        assertEquals("deja vu", normalizeSearchText("Déjà Vu"))
        assertEquals("beyonce", normalizeSearchText("Beyoncé"))
        assertEquals("after hours", normalizeSearchText("  AFTER Hours  "))
        assertEquals("", normalizeSearchText(null))
        assertEquals("", normalizeSearchText(""))
    }

    @Test
    fun filterTracksByTitleReturnsMatches() {
        val results = filterTracks(tracks, "blinding")
        assertEquals(1, results.size)
        assertEquals("t1", results[0].id)
    }

    @Test
    fun filterTracksByArtistReturnsMatches() {
        val results = filterTracks(tracks, "weeknd")
        assertEquals(2, results.size)
        assertEquals("t1", results[0].id)
        assertEquals("t2", results[1].id)
    }

    @Test
    fun filterTracksByAlbumReturnsMatches() {
        val results = filterTracks(tracks, "nostalgia")
        assertEquals(1, results.size)
        assertEquals("t3", results[0].id)
    }

    @Test
    fun filterTracksHandlesDiacriticsInQueryOrTitle() {
        // Query without accent matches track with accent
        val results1 = filterTracks(tracks, "deja")
        assertEquals(1, results1.size)
        assertEquals("t4", results1[0].id)

        // Query with accent matches track without accent
        val results2 = filterTracks(tracks, "Beyonce")
        assertEquals(1, results2.size)
        assertEquals("t4", results2[0].id)
    }

    @Test
    fun filterTracksWithBlankQueryReturnsOriginalList() {
        val results = filterTracks(tracks, "   ")
        assertEquals(tracks.size, results.size)
    }

    @Test
    fun filterTracksWithNoMatchesReturnsEmptyList() {
        val results = filterTracks(tracks, "nonexistent song 12345")
        assertTrue(results.isEmpty())
    }

    @Test
    fun filterCatalogItemsMatchesPlaylistsAlbumsAndArtists() {
        val items = listOf(
            CatalogItem.Collection(Playlist(id = "p1", title = "Chill Vibes", author = "Curator", description = "Relaxing songs")),
            CatalogItem.Record(Album(id = "a1", title = "After Hours", artist = "The Weeknd")),
            CatalogItem.Performer(Artist(id = "ar1", name = "Dua Lipa", subtitle = "Pop Artist")),
            CatalogItem.Category(id = "c1", title = "Pop & Dance"),
        )

        val chillMatch = filterCatalogItems(items, "chill")
        assertEquals(1, chillMatch.size)
        assertEquals("p1", chillMatch[0].stableId)

        val descMatch = filterCatalogItems(items, "relaxing")
        assertEquals(1, descMatch.size)
        assertEquals("p1", descMatch[0].stableId)

        val artistMatch = filterCatalogItems(items, "dua")
        assertEquals(1, artistMatch.size)
        assertEquals("ar1", artistMatch[0].stableId)

        val subtitleMatch = filterCatalogItems(items, "pop artist")
        assertEquals(1, subtitleMatch.size)
        assertEquals("ar1", subtitleMatch[0].stableId)

        val categoryMatch = filterCatalogItems(items, "dance")
        assertEquals(1, categoryMatch.size)
        assertEquals("c1", categoryMatch[0].stableId)

        val blankMatch = filterCatalogItems(items, "")
        assertEquals(4, blankMatch.size)
    }
}
