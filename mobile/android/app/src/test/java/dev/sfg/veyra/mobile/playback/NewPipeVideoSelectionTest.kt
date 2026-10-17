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

import dev.sfg.veyra.mobile.model.AudioQuality
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NewPipeVideoSelectionTest {
    @Test
    fun `high quality caps video at 720p`() {
        assertEquals(
            1,
            selectNewPipeVideoStreamIndex(
                heights = listOf(360, 720, 1080),
                mimeTypes = listOf("video/mp4", "video/mp4", "video/mp4"),
                quality = AudioQuality.HIGH,
            ),
        )
    }

    @Test
    fun `mp4 wins over webm at the same resolution`() {
        assertEquals(
            1,
            selectNewPipeVideoStreamIndex(
                heights = listOf(720, 720),
                mimeTypes = listOf("video/webm", "video/mp4"),
                quality = AudioQuality.MAX,
            ),
        )
    }

    @Test
    fun `empty formats have no selection`() {
        assertNull(selectNewPipeVideoStreamIndex(emptyList(), emptyList(), AudioQuality.NORMAL))
    }
}
