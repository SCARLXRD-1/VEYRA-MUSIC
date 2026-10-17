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

package dev.sfg.veyra.connect.transport

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SocketIoPacketCodecTest {
    @Test
    fun encodesDesktopEventWithAcknowledgementId() {
        val packet = SocketIoPacketCodec.event("connect:hello", JSONObject().put("name", "Phone"), 7)

        assertTrue(packet.startsWith("427[\"connect:hello\""))
    }

    @Test
    fun decodesDesktopAcknowledgementEnvelope() {
        val packet = SocketIoPacketCodec.decode("431[{\"ok\":true,\"data\":{\"status\":\"approved\"}}]")
            as SocketIoPacketCodec.Incoming.Ack

        assertEquals(1, packet.id)
        assertEquals("approved", packet.payload.getJSONObject("data").getString("status"))
    }

    @Test
    fun decodesStateEvent() {
        val packet = SocketIoPacketCodec.decode("42[\"connect:state\",{\"status\":\"connected\"}]")
            as SocketIoPacketCodec.Incoming.Event

        assertEquals("connect:state", packet.name)
        assertEquals("connected", (packet.payload as JSONObject).getString("status"))
    }
}
