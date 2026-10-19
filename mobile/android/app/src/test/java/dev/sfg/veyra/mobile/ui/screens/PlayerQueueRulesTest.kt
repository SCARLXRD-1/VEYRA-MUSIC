/*
 * Copyright (C) 2026 SFG545
 *
 * This file is part of Veyra Music and is licensed under AGPL-3.0-or-later.
 */

package dev.sfg.veyra.mobile.ui.screens

import dev.sfg.veyra.mobile.model.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerQueueRulesTest {
    @Test
    fun `the first upcoming row cannot move ahead of the current track`() {
        assertFalse(queueCanMoveUp(index = 4, currentIndex = 3))
        assertTrue(queueCanMoveUp(index = 5, currentIndex = 3))
    }

    @Test
    fun `move down stays within the queue bounds`() {
        assertFalse(queueCanMoveDown(index = 4, queueSize = 5))
        assertTrue(queueCanMoveDown(index = 3, queueSize = 5))
        assertFalse(queueCanMoveDown(index = -1, queueSize = 5))
    }

    @Test
    fun `upcoming keys stay with tracks after reordering and support duplicates`() {
        val first = Track(id = "one", title = "First", artist = "Artist")
        val second = Track(id = "two", title = "Second", artist = "Artist")
        val keys = upcomingQueueKeys(listOf(first, second, first))

        assertEquals(listOf("upcoming:one:0", "upcoming:two:0", "upcoming:one:1"), keys)
        assertEquals("upcoming:two:0", upcomingQueueKeys(listOf(second, first, first)).first())
    }
}
