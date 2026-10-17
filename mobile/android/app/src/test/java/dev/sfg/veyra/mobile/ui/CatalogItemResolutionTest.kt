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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogItemResolutionTest {

    @Test
    fun `song catalog item delegates to play action instead of open detail`() {
        val songItem = CatalogItem.Song(Track(id = "song-123", title = "Quick Song", artist = "Artist"))
        var playedTrackId: String? = null
        var openedDetailId: String? = null

        val handleItem: (CatalogItem) -> Unit = { item ->
            when (item) {
                is CatalogItem.Song -> playedTrackId = item.track.id
                else -> openedDetailId = item.stableId
            }
        }

        handleItem(songItem)

        assertEquals("song-123", playedTrackId)
        assertEquals(null, openedDetailId)
    }

    @Test
    fun `album collection and artist catalog items delegate to open detail`() {
        val recordItem = CatalogItem.Record(Album(id = "MPREalbum-1", title = "Album Title", artist = "Artist"))
        val collectionItem = CatalogItem.Collection(Playlist(id = "VLplaylist-1", title = "Playlist Title"))
        val artistItem = CatalogItem.Performer(Artist(id = "UCartist-1", name = "Artist Name"))

        val openedDetails = mutableListOf<String>()
        val handleItem: (CatalogItem) -> Unit = { item ->
            when (item) {
                is CatalogItem.Song -> Unit
                else -> openedDetails.add(item.stableId)
            }
        }

        handleItem(recordItem)
        handleItem(collectionItem)
        handleItem(artistItem)

        assertEquals(listOf("MPREalbum-1", "VLplaylist-1", "UCartist-1"), openedDetails)
    }
}
