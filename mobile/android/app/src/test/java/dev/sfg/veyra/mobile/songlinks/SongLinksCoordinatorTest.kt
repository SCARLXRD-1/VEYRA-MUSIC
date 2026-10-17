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

package dev.sfg.veyra.mobile.songlinks

import dev.sfg.veyra.mobile.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SongLinksCoordinatorTest {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
    private val repository = SongLinksRepository(OkHttpClient())
    private lateinit var coordinator: SongLinksCoordinator

    @Before
    fun setUp() {
        coordinator = SongLinksCoordinator(repository, scope)
    }

    @After
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun initialStateIsNull() {
        assertNull(coordinator.shareState.value)
    }

    @Test
    fun dismissClearsState() {
        val track = Track(
            id = "test-id",
            title = "Test Song",
            artist = "Test Artist",
            album = "Test Album",
            durationMs = 180000,
            artworkUrl = "https://example.com/art.jpg",
        )
        coordinator.shareTrack(track)
        assertNotNull(coordinator.shareState.value)

        coordinator.dismissShare()
        assertNull(coordinator.shareState.value)
    }

    @Test
    fun parsesDirectVideoLinks() = runTest {
        val resolution = coordinator.resolveLink("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
        assertTrue(resolution is LinkResolution.PlayTrack)
        assertEquals("dQw4w9WgXcQ", (resolution as LinkResolution.PlayTrack).track.id)
    }

    @Test
    fun parsesBrowsePlaylistLinks() = runTest {
        val resolution = coordinator.resolveLink("https://www.youtube.com/playlist?list=PL123456789")
        assertTrue(resolution is LinkResolution.OpenCollection)
        assertEquals("PL123456789", (resolution as LinkResolution.OpenCollection).browseId)
    }

    @Test
    fun nonLinksReturnNull() = runTest {
        val resolution = coordinator.resolveLink("random search text")
        assertNull(resolution)
    }
}
