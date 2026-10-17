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

import dev.sfg.veyra.mobile.model.Track
import dev.sfg.veyra.mobile.ui.components.collectionDownloadAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CollectionActionTest {

    private val track1 = Track(id = "t1", title = "Track 1", artist = "Artist 1")
    private val track2 = Track(id = "t2", title = "Track 2", artist = "Artist 2")
    private val tracks = listOf(track1, track2)

    @Test
    fun returnsNullWhenCallbacksMissingOrTracksEmpty() {
        val downloaded = setOf("t1", "t2")
        val onDownload: (List<Track>) -> Unit = {}
        val onRemove: (List<Track>) -> Unit = {}

        assertNull(collectionDownloadAction(tracks, downloaded, null, onRemove))
        assertNull(collectionDownloadAction(tracks, downloaded, onDownload, null))
        assertNull(collectionDownloadAction(emptyList(), downloaded, onDownload, onRemove))
    }

    @Test
    fun downloadsWhenNoneAreDownloaded() {
        var downloadedTracks: List<Track>? = null
        var removedTracks: List<Track>? = null

        val action = collectionDownloadAction(
            tracks = tracks,
            downloadedTrackIds = emptySet(),
            onDownloadTracks = { downloadedTracks = it },
            onRemoveDownloadTracks = { removedTracks = it },
        )

        assertNotNull(action)
        action?.invoke()
        assertEquals(tracks, downloadedTracks)
        assertNull(removedTracks)
    }

    @Test
    fun downloadsRemainingWhenPartiallyDownloaded() {
        var downloadedTracks: List<Track>? = null
        var removedTracks: List<Track>? = null

        val action = collectionDownloadAction(
            tracks = tracks,
            downloadedTrackIds = setOf("t1"),
            onDownloadTracks = { downloadedTracks = it },
            onRemoveDownloadTracks = { removedTracks = it },
        )

        assertNotNull(action)
        action?.invoke()
        assertEquals(tracks, downloadedTracks)
        assertNull(removedTracks)
    }

    @Test
    fun removesDownloadsWhenAllAreDownloaded() {
        var downloadedTracks: List<Track>? = null
        var removedTracks: List<Track>? = null

        val action = collectionDownloadAction(
            tracks = tracks,
            downloadedTrackIds = setOf("t1", "t2"),
            onDownloadTracks = { downloadedTracks = it },
            onRemoveDownloadTracks = { removedTracks = it },
        )

        assertNotNull(action)
        action?.invoke()
        assertNull(downloadedTracks)
        assertEquals(tracks, removedTracks)
    }
}
