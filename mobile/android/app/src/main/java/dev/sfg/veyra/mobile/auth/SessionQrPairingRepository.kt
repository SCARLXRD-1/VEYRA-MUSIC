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

package dev.sfg.veyra.mobile.auth

import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.concurrent.atomic.AtomicLong

/** One-shot, same-LAN handoff. The QR contains only an ephemeral public key and address. */
class SessionQrPairingRepository(
    private val scope: CoroutineScope,
    private val auth: NativeYouTubeAuthRepository,
) {
    private val mutableQr = MutableStateFlow("")
    val qrPayload: StateFlow<String> = mutableQr.asStateFlow()
    private val mutableStatus = MutableStateFlow("")
    val status: StateFlow<String> = mutableStatus.asStateFlow()
    private val receiverLock = Any()
    private val receiverGeneration = AtomicLong()
    private var receiverJob: Job? = null
    @Volatile private var receiver: ServerSocket? = null

    fun startReceiver() {
        val generation = synchronized(receiverLock) {
            receiverGeneration.incrementAndGet().also {
                receiverJob?.cancel()
                receiver?.close()
                receiver = null
                mutableStatus.value = "Esperando al móvil en la misma red…"
                mutableQr.value = ""
            }
        }
        val job = scope.launch(Dispatchers.IO, start = CoroutineStart.LAZY) {
            var server: ServerSocket? = null
            try {
                val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
                val nonce = ByteArray(24).also(SecureRandom()::nextBytes)
                server = ServerSocket(0, 1).also { it.soTimeout = QR_TTL_MS }
                val activeServer = server
                val isCurrent = synchronized(receiverLock) {
                    if (receiverGeneration.get() != generation) false
                    else {
                        receiver = activeServer
                        true
                    }
                }
                if (!isCurrent) return@launch
                val address = localAddress() ?: error("No se encontró una dirección de red local")
                val qr = "veyra-session://pair?host=${enc(address)}&port=${activeServer.localPort}" +
                    "&nonce=${b64(nonce)}&key=${b64(keyPair.public.encoded)}"
                synchronized(receiverLock) {
                    if (receiverGeneration.get() != generation) return@synchronized
                    mutableQr.value = qr
                }
                if (receiverGeneration.get() != generation) return@launch
                val socket = activeServer.accept().apply { soTimeout = SOCKET_TIMEOUT_MS }
                socket.use { receiveSession(it, keyPair, nonce) }
                synchronized(receiverLock) {
                    if (receiverGeneration.get() == generation) {
                        mutableQr.value = ""
                        mutableStatus.value = "Sesión conectada correctamente."
                    }
                }
            } catch (error: Exception) {
                synchronized(receiverLock) {
                    if (receiverGeneration.get() == generation && error !is CancellationException) {
                        mutableQr.value = ""
                        mutableStatus.value = if (error is java.net.SocketTimeoutException) {
                            "El QR venció. Genera uno nuevo para volver a intentarlo."
                        } else {
                            error.message ?: "No se pudo conectar la sesión."
                        }
                    }
                }
            } finally {
                synchronized(receiverLock) {
                    server?.close()
                    if (receiverGeneration.get() == generation && (server == null || receiver === server)) {
                        receiver = null
                        receiverJob = null
                    }
                }
            }
        }
        synchronized(receiverLock) {
            if (receiverGeneration.get() == generation) {
                receiverJob = job
                job.start()
            } else {
                job.cancel()
            }
        }
    }

    fun cancelReceiver() {
        synchronized(receiverLock) {
            receiverGeneration.incrementAndGet()
            receiverJob?.cancel()
            receiverJob = null
            receiver?.close()
            receiver = null
            mutableQr.value = ""
            mutableStatus.value = ""
        }
    }

    fun importFromQr(qr: String) {
        scope.launch(Dispatchers.IO) {
            try {
                val uri = URI(qr.trim())
                require(uri.scheme == "veyra-session" && uri.host == "pair") { "Este QR no es de sesión Veyra." }
                val params = uri.rawQuery.orEmpty().split('&').mapNotNull {
                    val i = it.indexOf('='); if (i < 0) null else dec(it.substring(0, i)) to dec(it.substring(i + 1))
                }.toMap()
                val session = auth.session() ?: error("Inicia sesión en YouTube Music en el móvil primero.")
                val address = params["host"] ?: error("QR de sesión incompleto.")
                require(address.matches(IPV4_PATTERN)) { "El QR no contiene una dirección local válida." }
                val ip = InetAddress.getByName(address)
                require(ip is Inet4Address && ip.isSiteLocalAddress && !ip.isLoopbackAddress) {
                    "El QR de sesión debe pertenecer a la red local."
                }
                val port = params["port"]?.toIntOrNull()?.takeIf { it in 1024..65535 }
                    ?: error("QR de sesión inválido.")
                val nonce = unb64(params["nonce"] ?: error("QR de sesión inválido."))
                require(nonce.size == NONCE_BYTES) { "QR de sesión inválido." }
                val publicKey = java.security.KeyFactory.getInstance("RSA")
                    .generatePublic(X509EncodedKeySpec(unb64(params["key"] ?: error("QR de sesión inválido."))))
                require((publicKey as? java.security.interfaces.RSAKey)?.modulus?.bitLength() in 2048..4096) {
                    "La clave del QR de sesión no es válida."
                }
                val content = JSONObject()
                    .put("cookie", session.cookie)
                    .put("visitorData", session.visitorData)
                    .put("dataSyncId", session.dataSyncId)
                    .put("displayName", session.displayName)
                    .put("avatarUrl", session.avatarUrl)
                    .put("nonce", b64(nonce))
                val aes = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
                val iv = ByteArray(12).also(SecureRandom()::nextBytes)
                val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE, aes, GCMParameterSpec(128, iv)) }
                val encrypted = cipher.doFinal(content.toString().toByteArray(Charsets.UTF_8))
                val wrappedKey = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding").run {
                    init(Cipher.ENCRYPT_MODE, publicKey); doFinal(aes.encoded)
                }
                val body = JSONObject().put("key", b64(wrappedKey)).put("iv", b64(iv)).put("data", b64(encrypted)).toString()
                Socket(address, port).use { socket ->
                    socket.soTimeout = 12_000
                    val out = socket.getOutputStream()
                    val bytes = body.toByteArray(Charsets.UTF_8)
                    out.write("POST /session HTTP/1.1\r\nHost: $address\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n".toByteArray())
                    out.write(bytes); out.flush()
                    val response = BufferedReader(InputStreamReader(socket.getInputStream())).readLine().orEmpty()
                    check(response.contains(" 200 ")) { "La TV no aceptó la sesión." }
                }
                mutableStatus.value = "Sesión enviada a la TV."
            } catch (error: Exception) {
                mutableStatus.value = error.message ?: "No se pudo enviar la sesión."
            }
        }
    }

    private suspend fun receiveSession(socket: Socket, keyPair: KeyPair, expectedNonce: ByteArray) {
        val input = socket.getInputStream()
        val headerBytes = ByteArrayOutputStream()
        var matched = 0
        val delimiter = byteArrayOf(13, 10, 13, 10)
        while (headerBytes.size() < 8192 && matched < delimiter.size) {
            val next = input.read()
            require(next >= 0) { "Solicitud incompleta." }
            headerBytes.write(next)
            matched = if (next == delimiter[matched].toInt()) matched + 1 else if (next == 13) 1 else 0
        }
        require(matched == delimiter.size) { "Cabeceras inválidas." }
        val headers = String(headerBytes.toByteArray(), Charsets.ISO_8859_1).split("\r\n")
        val request = headers.firstOrNull().orEmpty()
        require(request.startsWith("POST /session ")) { "Solicitud inválida." }
        val contentLength = headers.firstNotNullOfOrNull { line ->
            line.takeIf { it.startsWith("Content-Length:", true) }?.substringAfter(':')?.trim()?.toIntOrNull()
        } ?: 0
        require(contentLength in 1..32_768) { "Tamaño de sesión inválido." }
        val raw = ByteArray(contentLength)
        var offset = 0
        while (offset < raw.size) {
            val read = input.read(raw, offset, raw.size - offset)
            if (read < 0) break
            offset += read
        }
        require(offset == contentLength) { "Transferencia incompleta." }
        val envelope = JSONObject(String(raw, Charsets.UTF_8))
        val aesBytes = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding").run {
            init(Cipher.DECRYPT_MODE, keyPair.private); doFinal(unb64(envelope.getString("key")))
        }
        val decrypted = Cipher.getInstance("AES/GCM/NoPadding").run {
            init(Cipher.DECRYPT_MODE, SecretKeySpec(aesBytes, "AES"), GCMParameterSpec(128, unb64(envelope.getString("iv"))))
            doFinal(unb64(envelope.getString("data")))
        }
        val json = JSONObject(String(decrypted, Charsets.UTF_8))
        require(SecureEquals.matches(unb64(json.getString("nonce")), expectedNonce)) { "QR vencido o inválido." }
        auth.importSession(YouTubeSession(
            cookie = json.getString("cookie"), visitorData = json.optString("visitorData"),
            dataSyncId = json.optString("dataSyncId"), displayName = json.optString("displayName", "YouTube Music"),
            avatarUrl = json.optString("avatarUrl"),
        ))
        val out = socket.getOutputStream()
        out.write("HTTP/1.1 200 OK\r\nContent-Length: 2\r\nConnection: close\r\n\r\nOK".toByteArray()); out.flush()
    }

    private fun localAddress(): String? = runCatching {
        NetworkInterface.getNetworkInterfaces().toList().asSequence().flatMap { it.inetAddresses.toList().asSequence() }
            .filterIsInstance<Inet4Address>().firstOrNull { !it.isLoopbackAddress && it.isSiteLocalAddress }?.hostAddress
    }.getOrNull()

    private fun enc(value: String) = URLEncoder.encode(value, Charsets.UTF_8.name())
    private fun dec(value: String) = URLDecoder.decode(value, Charsets.UTF_8.name())
    private fun b64(value: ByteArray) = Base64.encodeToString(value, Base64.NO_WRAP or Base64.URL_SAFE)
    private fun unb64(value: String) = Base64.decode(value, Base64.NO_WRAP or Base64.URL_SAFE)

    private companion object {
        const val QR_TTL_MS = 180_000
        const val SOCKET_TIMEOUT_MS = 12_000
        const val NONCE_BYTES = 24
        val IPV4_PATTERN = Regex("(?:[0-9]{1,3}\\.){3}[0-9]{1,3}")
    }

    private object SecureEquals {
        fun matches(a: ByteArray, b: ByteArray): Boolean = java.security.MessageDigest.isEqual(a, b)
    }
}
