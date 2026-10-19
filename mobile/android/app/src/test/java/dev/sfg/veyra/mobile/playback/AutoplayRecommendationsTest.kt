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

package dev.sfg.veyra.mobile.playback

import dev.sfg.veyra.mobile.model.MUSIC_VIDEO_TYPE_ATV
import dev.sfg.veyra.mobile.model.MUSIC_VIDEO_TYPE_OMV
import dev.sfg.veyra.mobile.model.Track
import org.junit.Assert.assertEquals
import org.junit.Test

class AutoplayRecommendationsTest {
    @Test
    fun `offline autoplay excludes queued downloads and applies its refill limit`() {
        val candidates = AutoplayRecommendations.offlineCandidates(
            downloaded = listOf(
                track("current", "Current", "Artist"),
                track("one", "One", "Artist"),
                track("one", "One duplicate row", "Artist"),
                track("two", "Two", "Artist"),
            ),
            queuedIds = setOf("current"),
            limit = 1,
        )

        assertEquals(listOf("one"), candidates.map(Track::id))
    }

    @Test
    fun `offline autoplay returns no duplicates when all downloads are already queued`() {
        val candidates = AutoplayRecommendations.offlineCandidates(
            downloaded = listOf(track("one", "One", "Artist"), track("two", "Two", "Artist")),
            queuedIds = setOf("one", "two"),
            limit = 10,
        )

        assertEquals(emptyList<Track>(), candidates)
    }

    @Test
    fun `different video ids for the same song collapse to one recommendation`() {
        val selected = AutoplayRecommendations.select(
            existing = emptyList(),
            candidates = listOf(
                track("video", "Every Second", "Mina Okabe"),
                track("audio", "Every Second", "Mina Okabe"),
            ),
            limit = 20,
        )

        assertEquals(listOf("video"), selected.map(Track::id))
    }

    @Test
    fun `album audio replaces an earlier music video without changing radio order`() {
        val selected = AutoplayRecommendations.select(
            existing = emptyList(),
            candidates = listOf(
                track("video", "Heaven Knows I'm Miserable Now", "The Smiths", MUSIC_VIDEO_TYPE_OMV),
                track("middle", "Sense", "Jasmine Rodgers"),
                track("audio", "Heaven Knows I'm Miserable Now", "The Smiths", MUSIC_VIDEO_TYPE_ATV),
            ),
            limit = 20,
        )

        assertEquals(listOf("audio", "middle"), selected.map(Track::id))
    }

    @Test
    fun `collaborator byline and radio edit are treated as song variants`() {
        val selected = AutoplayRecommendations.select(
            existing = emptyList(),
            candidates = listOf(
                track("solo", "Fall in Love Alone", "Stacey Ryan", durationMs = 205_000),
                track("duet", "Fall in Love Alone", "Stacey Ryan & Ziva Magnolia", durationMs = 207_000),
                track("original", "Kiss Me", "Sixpence None the Richer", durationMs = 208_000),
                track("radio", "Kiss Me - Radio Edit", "Sixpence None the Richer", durationMs = 196_000),
            ),
            limit = 20,
        )

        assertEquals(listOf("solo", "original"), selected.map(Track::id))
    }

    @Test
    fun `semantic duplicate already in the queue is not appended`() {
        val selected = AutoplayRecommendations.select(
            existing = listOf(track("queued", "I Love You So", "The Walters")),
            candidates = listOf(
                track("alternate", "I Love You So (Official Audio)", "The Walters"),
                track("next", "Duvet", "b\u00F4a"),
            ),
            limit = 20,
        )

        assertEquals(listOf("next"), selected.map(Track::id))
    }

    @Test
    fun `generated duplicate rows are identified without removing user playlist duplicates`() {
        val userFirst = track("user-1", "Same Song", "Artist")
        val userSecond = track("user-2", "Same Song", "Artist")
        val generatedDuplicate = track("radio-1", "Same Song (feat. Guest)", "Artist ft. Guest")
            .copy(autoplayGenerated = true)
        val generatedUnique = track("radio-2", "Next Song", "Artist")
            .copy(autoplayGenerated = true)
        val generatedRepeated = track("radio-3", "Next Song (Official Audio)", "Artist")
            .copy(autoplayGenerated = true)

        val duplicates = AutoplayRecommendations.duplicateGeneratedIndices(
            listOf(userFirst, userSecond, generatedDuplicate, generatedUnique, generatedRepeated),
        )

        assertEquals(listOf(2, 4), duplicates)
    }

    @Test
    fun `same title from another artist and substantially different versions remain`() {
        val selected = AutoplayRecommendations.select(
            existing = emptyList(),
            candidates = listOf(
                track("one", "Home", "Artist One", durationMs = 180_000),
                track("cover", "Home", "Artist Two", durationMs = 180_000),
                track("extended", "Home", "Artist One", durationMs = 300_000),
            ),
            limit = 20,
        )

        assertEquals(listOf("one", "cover", "extended"), selected.map(Track::id))
    }

    private fun track(
        id: String,
        title: String,
        artist: String,
        musicVideoType: String = "",
        durationMs: Long = 200_000,
    ) = Track(
        id = id,
        title = title,
        artist = artist,
        durationMs = durationMs,
        musicVideoType = musicVideoType,
    )
}
