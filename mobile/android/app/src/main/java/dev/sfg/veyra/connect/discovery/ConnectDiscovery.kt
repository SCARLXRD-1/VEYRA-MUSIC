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
import dev.sfg.veyra.connect.protocol.PairingInput
import java.net.URI
import java.net.URLDecoder
import java.util.Locale

/** Pure URL parsing and validation shared by QR, deep-link, and manual pairing. */
object ConnectDiscovery {
    fun cleanServerUrl(value: String?): String {
        return try {
            val uri = URI(value.orEmpty().trim())
            val scheme = uri.scheme?.lowercase(Locale.US) ?: return ""
            if (scheme != "http" && scheme != "https") return ""
            val host = uri.host ?: return ""
            URI(scheme, null, host, uri.port, null, null, null).toASCIIString()
        } catch (_: Exception) {
            ""
        }
    }

    fun serverHost(value: String?): String = try {
        URI(value.orEmpty().trim()).host.orEmpty()
    } catch (_: Exception) {
        ""
    }

    fun parsePairingInput(value: String?): PairingInput {
        var text = value.orEmpty().trim()
        if (text.isEmpty()) return PairingInput()
        if (Regex("%[0-9a-f]{2}", RegexOption.IGNORE_CASE).containsMatchIn(text)) {
            text = decode(text)
        }

        return try {
            val uri = URI(text)
            val isPairingScheme = uri.scheme.equals(ConnectProtocol.PAIRING_SCHEME, ignoreCase = true) ||
                uri.scheme.equals(ConnectProtocol.LEGACY_PAIRING_SCHEME, ignoreCase = true)
            if (isPairingScheme) {
                if (!uri.host.equals(ConnectProtocol.PAIRING_HOST, ignoreCase = true)) return PairingInput()
                val parameters = queryParameters(uri.rawQuery)
                PairingInput(cleanServerUrl(parameters["server"]), parameters[ConnectProtocol.Field.TOKEN].orEmpty())
            } else {
                PairingInput(cleanServerUrl(text), queryParameters(uri.rawQuery)[ConnectProtocol.Field.TOKEN].orEmpty())
            }
        } catch (_: Exception) {
            val parameters = queryParameters(text.removePrefix("?").removePrefix("#"))
            val serverUrl = cleanServerUrl(parameters["server"])
            if (serverUrl.isNotEmpty() || parameters[ConnectProtocol.Field.TOKEN].orEmpty().isNotEmpty()) {
                PairingInput(serverUrl, parameters[ConnectProtocol.Field.TOKEN].orEmpty())
            } else {
                PairingInput(cleanServerUrl(text), "")
            }
        }
    }

    private fun queryParameters(query: String?): Map<String, String> {
        if (query.isNullOrBlank()) return emptyMap()
        return query.split("&").mapNotNull { part ->
            val separator = part.indexOf('=')
            if (separator < 0) return@mapNotNull null
            decode(part.substring(0, separator)) to decode(part.substring(separator + 1))
        }.toMap()
    }

    private fun decode(value: String): String = try {
        URLDecoder.decode(value, Charsets.UTF_8.name())
    } catch (_: Exception) {
        value
    }
}
