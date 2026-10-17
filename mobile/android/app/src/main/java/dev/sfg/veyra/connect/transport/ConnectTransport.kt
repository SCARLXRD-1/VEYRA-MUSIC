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

/**
 * Costura de transporte neutral a la plataforma para Veyra Connect.
 *
 * Las implementaciones poseen los callbacks de red y pueden invocar [Listener] en hilos
 * arbitrarios. [dev.sfg.veyra.connect.client.VeyraConnectClient] serializa esos callbacks
 * en su ejecutor configurado antes de exponerlos a la UI.
 */
interface ConnectTransport {
    interface Listener {
        fun onOpened()
        fun onClosed(reason: String)
        fun onFailure(error: Throwable)
        fun onEvent(name: String, payload: Any?)
    }

    fun connect()
    fun emit(event: String, payload: Any? = JSONObject.NULL, ack: ((Result<JSONObject>) -> Unit)? = null)
    fun close()

    fun interface Factory {
        fun create(serverUrl: String, listener: Listener): ConnectTransport
    }
}
