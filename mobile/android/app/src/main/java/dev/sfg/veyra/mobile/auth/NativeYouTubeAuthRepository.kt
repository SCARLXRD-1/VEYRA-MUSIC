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

import android.content.Context
import android.util.Log
import android.webkit.CookieManager
import dev.sfg.veyra.connect.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicLong

/** Posee la sesión de cookies cifrada capturada por la pantalla de login nativa de Veyra Music en Android. */
/**
 * [profileLoader] devuelve el nombre y la URL de avatar de la cuenta. Es una lambda en lugar
 * de una referencia a un cliente porque el cliente InnerTube necesita este repositorio como
 * su proveedor de sesión.
 */
class NativeYouTubeAuthRepository(
    private val store: SecureYouTubeSessionStore,
    context: Context,
    private val scope: CoroutineScope,
    private val profileLoader: () -> Pair<String, String> = { "" to "" },
) : YouTubeSessionProvider {
    private val appContext = context.applicationContext
    private val mutableState = MutableStateFlow<AuthState>(AuthState.Restoring)
    val state: StateFlow<AuthState> = mutableState.asStateFlow()
    private val generation = AtomicLong()
    private val sessionGuard = Any()
    @Volatile private var activeSession: YouTubeSession? = null

    override fun session(): YouTubeSession? = activeSession

    suspend fun restore() {
        val restoreGeneration = generation.get()
        try {
            val restored = withContext(Dispatchers.IO) { store.load() }
            synchronized(sessionGuard) {
                if (generation.get() != restoreGeneration) return
                activeSession = restored
                mutableState.value = restored?.let { AuthState.SignedIn(it.displayName, it.avatarUrl) }
                    ?: AuthState.SignedOut
            }
            // Refresh in the background so a renamed account or new avatar catches up.
            if (restored != null) refreshProfile(restoreGeneration)
        } catch (error: Exception) {
            Log.w(TAG, "Stored YouTube session could not be restored", error)
            synchronized(sessionGuard) {
                if (generation.get() == restoreGeneration) {
                    activeSession = null
                    mutableState.value =
                        AuthState.Error(appContext.getString(R.string.auth_session_restore_failed))
                }
            }
        }
    }

    fun beginSignIn() = synchronized(sessionGuard) {
        generation.incrementAndGet()
        mutableState.value = AuthState.Authorizing
    }

    fun completeSignIn(cookie: String, visitorData: String, dataSyncId: String) {
        val signInGeneration = generation.get()
        mutableState.value = AuthState.Authorizing
        scope.launch {
            try {
                val normalized = YouTubeSession(
                    cookie = cookie.trim(),
                    visitorData = visitorData.trim(),
                    dataSyncId = YouTubeSessionAuth.normalizeDataSyncId(dataSyncId),
                )
                require(YouTubeSessionAuth.loginCookieValue(normalized.cookie) != null) {
                    appContext.getString(R.string.auth_no_session_returned)
                }
                val committed = withContext(Dispatchers.IO) { commitSession(normalized, signInGeneration) }
                if (!committed) return@launch
                refreshProfile(signInGeneration)
            } catch (error: Exception) {
                Log.w(TAG, "Native YouTube sign-in could not be completed", error)
                synchronized(sessionGuard) {
                    if (generation.get() == signInGeneration) {
                        mutableState.value =
                            AuthState.Error(
                                error.message ?: appContext.getString(R.string.auth_sign_in_incomplete),
                            )
                    }
                }
            }
        }
    }

    /** Commits an already authenticated session received through an explicit local device link. */
    fun importSession(session: YouTubeSession) {
        val importedGeneration = synchronized(sessionGuard) {
            generation.incrementAndGet().also { current ->
                require(YouTubeSessionAuth.loginCookieValue(session.cookie) != null) {
                    appContext.getString(R.string.auth_session_cookie_incomplete)
                }
                store.save(session)
                activeSession = session
                mutableState.value = AuthState.SignedIn(session.displayName, session.avatarUrl)
            }
        }
        scope.launch { refreshProfile(importedGeneration) }
    }

    fun cancelSignIn() = synchronized(sessionGuard) {
        generation.incrementAndGet()
        mutableState.value = activeSession?.let { AuthState.SignedIn(it.displayName, it.avatarUrl) }
            ?: AuthState.SignedOut
    }

    /**
     * Fetches the account name and avatar, then persists them onto the stored session.
     * Best-effort: the session stays usable with its fallback name if the call fails.
     */
    private suspend fun refreshProfile(expectedGeneration: Long) {
        val profile = withContext(Dispatchers.IO) { runCatching { profileLoader() }.getOrNull() }
            ?: return
        val (name, avatar) = profile
        if (name.isBlank() && avatar.isBlank()) return

        synchronized(sessionGuard) {
            if (generation.get() != expectedGeneration) return
            val current = activeSession ?: return
            val updated = current.copy(
                displayName = name.ifBlank { current.displayName },
                avatarUrl = avatar.ifBlank { current.avatarUrl },
            )
            if (updated == current) return
            activeSession = updated
            runCatching { store.save(updated) }
                .onFailure { Log.w(TAG, "Account profile could not be persisted", it) }
            mutableState.value = AuthState.SignedIn(updated.displayName, updated.avatarUrl)
        }
    }

    fun signOut() {
        synchronized(sessionGuard) {
            generation.incrementAndGet()
            activeSession = null
            try {
                store.clear()
                mutableState.value = AuthState.SignedOut
            } catch (error: Exception) {
                Log.w(TAG, "YouTube session could not be deleted", error)
                mutableState.value =
                    AuthState.Error(appContext.getString(R.string.auth_sign_out_clear_failed))
            }
        }
        scope.launch(Dispatchers.Main) {
            runCatching {
                CookieManager.getInstance().apply { removeAllCookies { flush() } }
            }.onFailure { Log.w(TAG, "Web sign-in cookies could not be cleared", it) }
        }
    }

    private fun commitSession(session: YouTubeSession, expectedGeneration: Long): Boolean =
        synchronized(sessionGuard) {
            if (generation.get() != expectedGeneration) return@synchronized false
            store.save(session)
            activeSession = session
            mutableState.value = AuthState.SignedIn(session.displayName, session.avatarUrl)
            true
        }

    private companion object {
        const val TAG = "OrchardAuth"
    }
}
