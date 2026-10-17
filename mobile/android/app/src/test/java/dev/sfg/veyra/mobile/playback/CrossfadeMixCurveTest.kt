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

import kotlin.math.sqrt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CrossfadeMixCurveTest {

    @Test
    fun `both upper and bass bands crossfade smoothly at constant power`() {
        for (step in 0..10) {
            val progress = step / 10.0
            val gains = djMixGains(progress = progress, fadeSeconds = 7.5)
            assertEquals(
                1.0,
                gains.outgoingUpper * gains.outgoingUpper +
                    gains.incomingUpper * gains.incomingUpper,
                1e-9,
            )
            assertEquals(
                1.0,
                gains.outgoingBass * gains.outgoingBass +
                    gains.incomingBass * gains.incomingBass,
                1e-9,
            )
        }
    }

    @Test
    fun `the incoming track owns every band when the blend finishes`() {
        val end = djMixGains(progress = 1.0, fadeSeconds = 7.5)

        assertEquals(0.0, end.outgoingUpper, 1e-9)
        assertEquals(0.0, end.outgoingBass, 1e-9)
        assertEquals(1.0, end.incomingUpper, 1e-9)
        assertEquals(1.0, end.incomingBass, 1e-9)
    }
}
