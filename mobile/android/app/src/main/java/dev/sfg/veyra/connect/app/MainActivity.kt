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

package dev.sfg.veyra.connect.app

import android.Manifest
import android.app.SearchManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sfg.veyra.mobile.app.VeyraApp
import dev.sfg.veyra.mobile.app.VeyraViewModel
import dev.sfg.veyra.mobile.i18n.AppLocale
import dev.sfg.veyra.mobile.ui.theme.VeyraTheme

/** Thin Android entry point; app behavior lives in repositories and state holders. */
class MainActivity : FragmentActivity() {
    private val viewModel by viewModels<VeyraViewModel>()
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { /* Media3 remains usable in-app if the user declines notifications. */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        runCatching {
            com.google.android.gms.cast.framework.CastContext.getSharedInstance(this)
        }
        handleIntent(intent)
        requestNotificationPermission()
        AppLocale.seed(this)
        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val language by AppLocale.language.collectAsStateWithLifecycle()
            val baseContext = LocalContext.current
            val localizedContext = remember(language, baseContext) {
                AppLocale.localizedContext(baseContext, language)
            }
            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedContext.resources.configuration,
                LocalResources provides localizedContext.resources,
            ) {
                VeyraTheme(useSystemColors = settings.useSystemColors) {
                    VeyraApp(viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        if (intent.action == MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH) {
            // Assistant sends the spoken words as the query, and narrows them into the focus
            // extras when it recognised a specific artist or album. An empty query is the
            // documented "just play something" request.
            val query = intent.getStringExtra(SearchManager.QUERY).orEmpty().ifBlank {
                listOfNotNull(
                    intent.getStringExtra(MediaStore.EXTRA_MEDIA_TITLE),
                    intent.getStringExtra(MediaStore.EXTRA_MEDIA_ARTIST),
                    intent.getStringExtra(MediaStore.EXTRA_MEDIA_ALBUM),
                ).joinToString(" ")
            }
            viewModel.playFromSearch(query)
            return
        }
        if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("text/") == true) {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT).orEmpty()
            if (sharedText.isNotBlank()) {
                viewModel.handleIncomingLink(sharedText)
            }
            return
        }

        val uri = intent.data
        val dataString = uri?.toString().orEmpty()
        if (dataString.isBlank()) return
        val isDiscordCallback = (uri?.scheme == "orchard" && uri.host == "discord") ||
            (uri?.scheme?.startsWith("discord-") == true)
        if (isDiscordCallback) {
            val code = uri.getQueryParameter("code")
            val state = uri.getQueryParameter("state")
            if (!code.isNullOrBlank()) {
                viewModel.handleDiscordAuthCallback(code, state)
            }
            return
        }
        if (uri?.scheme == "veyra-connect" || uri?.scheme == "orchard-connect") {
            viewModel.pairDevice(dataString)
        } else {
            viewModel.handleIncomingLink(dataString)
        }
    }

    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        when (keyCode) {
            android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            android.view.KeyEvent.KEYCODE_HEADSETHOOK -> {
                viewModel.togglePlayback()
                return true
            }
            android.view.KeyEvent.KEYCODE_MEDIA_PLAY -> {
                if (!viewModel.playback.value.isPlaying) {
                    viewModel.togglePlayback()
                }
                return true
            }
            android.view.KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                if (viewModel.playback.value.isPlaying) {
                    viewModel.togglePlayback()
                }
                return true
            }
            android.view.KeyEvent.KEYCODE_MEDIA_NEXT,
            android.view.KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                viewModel.next()
                return true
            }
            android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS,
            android.view.KeyEvent.KEYCODE_MEDIA_REWIND -> {
                viewModel.previous()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < 33) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

