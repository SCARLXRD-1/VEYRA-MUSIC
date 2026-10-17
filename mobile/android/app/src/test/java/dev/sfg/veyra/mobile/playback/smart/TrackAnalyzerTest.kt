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

package dev.sfg.veyra.mobile.playback.smart

import org.junit.Assert.assertEquals
import org.junit.Test

class TrackAnalyzerTest {

    @Test
    fun `catalog duration wins when both sources have one`() {
        assertEquals(240.0, analysisDuration(240.0, 241.5), 1e-9)
    }

    @Test
    fun `cached container supplies a duration omitted by the catalog`() {
        assertEquals(222.601, analysisDuration(0.0, 222.601), 1e-9)
    }

    @Test
    fun `invalid duration sources remain unavailable`() {
        assertEquals(0.0, analysisDuration(Double.NaN, null), 1e-9)
        assertEquals(0.0, analysisDuration(-1.0, Double.POSITIVE_INFINITY), 1e-9)
    }
}
