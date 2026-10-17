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

package dev.sfg.veyra.connect.discovery

import dev.sfg.veyra.connect.protocol.ConnectProtocol
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Resuelve un escritorio que volvió al puerto LAN preferido de Veyra Music.
 *
 * Realiza E/S bloqueante y debe invocarse en un hilo de trabajo. El fallo es
 * intencionalmente no fatal: el último endpoint validado queda como respaldo.
 */
class LanEndpointDiscovery(private val timeoutMs: Int = 1_400) {
    fun discover(savedUrl: String, savedHost: String): String {
        val host = ConnectDiscovery.serverHost(savedUrl).ifEmpty { savedHost }
        val fallback = ConnectDiscovery.cleanServerUrl(savedUrl)
        if (host.isEmpty()) return fallback

        return try {
            val hostForUrl = if (':' in host && !host.startsWith("[")) "[$host]" else host
            val url = URL("http://$hostForUrl:${ConnectProtocol.PREFERRED_PORT}${ConnectProtocol.INFO_PATH}")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = timeoutMs
            connection.readTimeout = timeoutMs
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Cache-Control", "no-store")
            connection.inputStream.use { stream ->
                val payload = JSONObject(stream.bufferedReader().readText())
                ConnectDiscovery.cleanServerUrl(payload.optString("serverUrl")).ifEmpty { fallback }
            }
        } catch (_: Exception) {
            fallback
        }
    }
}
