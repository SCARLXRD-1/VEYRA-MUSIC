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

package dev.sfg.veyra.mobile.ui.screens

import android.annotation.SuppressLint
import android.os.Message
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.spotify.SpotifyCanvasRepository
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key

private const val SPOTIFY_MOBILE_UA =
    "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Mobile Safari/537.36"
private const val SPOTIFY_LOGIN_URL =
    "https://accounts.spotify.com/login?continue=https%3A%2F%2Fopen.spotify.com%2F"

/** Pantalla de inicio de sesión de Spotify con soporte de ventanas emergentes OAuth y captura de sp_dc. */
@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotifyLoginScreen(
    onSpdcCaptured: (String) -> Unit,
    onCancel: () -> Unit,
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var pageLoading by remember { mutableStateOf(true) }
    var pageError by remember { mutableStateOf("") }
    var webViewInitError by remember { mutableStateOf<String?>(null) }
    var reloadKey by remember { mutableStateOf(0) }
    var captured by remember { mutableStateOf(false) }
    var showManualDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            runCatching {
                webView?.stopLoading()
                webView?.destroy()
                webView = null
            }
        }
    }

    fun checkAndCaptureCookie(cookieManager: CookieManager?) {
        if (captured || cookieManager == null) return
        runCatching {
            val hosts = listOf("https://open.spotify.com", "https://accounts.spotify.com", "https://spotify.com")
            for (host in hosts) {
                val rawCookies = cookieManager.getCookie(host).orEmpty()
                val spdc = SpotifyCanvasRepository.extractSpdc(rawCookies)
                if (spdc.isNotBlank()) {
                    captured = true
                    onSpdcCaptured(spdc)
                    return
                }
            }
        }
    }

    BackHandler {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            onCancel()
        }
    }

    Column(Modifier.fillMaxSize().background(CanopyColors.Canvas)) {
        TopAppBar(
            title = { Text(stringResource(R.string.spot_login_title)) },
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.spot_back),
                    )
                }
            },
            actions = {
                IconButton(onClick = { showManualDialog = true }) {
                    Icon(
                        Icons.Rounded.VpnKey,
                        contentDescription = stringResource(R.string.spot_use_session_cookie),
                        tint = CanopyColors.Text,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = CanopyColors.Canvas,
                titleContentColor = CanopyColors.Text,
                navigationIconContentColor = CanopyColors.Text,
            ),
        )
        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (webViewInitError != null) {
                Surface(
                    color = CanopyColors.Surface,
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.spot_load_failed_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = CanopyColors.Danger,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = webViewInitError ?: stringResource(R.string.spot_page_load_failed),
                            style = MaterialTheme.typography.bodyMedium,
                            color = CanopyColors.Muted,
                        )
                        Spacer(Modifier.height(20.dp))
                        androidx.compose.material3.Button(
                            onClick = { showManualDialog = true },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = LocalAccent.current,
                            ),
                        ) {
                            Text(stringResource(R.string.spot_use_session_cookie))
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = {
                            webViewInitError = null
                            pageLoading = true
                            reloadKey++
                        }) {
                            Text(stringResource(R.string.action_retry))
                        }
                    }
                }
            } else {
                key(reloadKey) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            try {
                                WebView(context).apply {
                                    webView = this
                                    layoutParams = android.view.ViewGroup.LayoutParams(
                                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                    )
                                    isFocusable = true
                                    isFocusableInTouchMode = true
                                    setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                                    runCatching { settings.javaScriptEnabled = true }
                                    runCatching { settings.domStorageEnabled = true }
                                    runCatching { settings.javaScriptCanOpenWindowsAutomatically = true }
                                    runCatching { settings.setSupportMultipleWindows(false) }
                                    runCatching { settings.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT }
                                    runCatching {
                                        val curUa = settings.userAgentString.orEmpty()
                                        settings.userAgentString = if (curUa.contains("; wv")) {
                                            curUa.replace("; wv", "")
                                        } else {
                                            curUa.ifBlank { SPOTIFY_MOBILE_UA }
                                        }
                                    }

                                    val loginWebView = this
                                    val cookieManager = runCatching {
                                        CookieManager.getInstance().apply {
                                            setAcceptCookie(true)
                                            runCatching { setAcceptThirdPartyCookies(loginWebView, true) }
                                        }
                                    }.getOrNull()

                                    webChromeClient = object : WebChromeClient() {
                                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                            if (newProgress >= 70) pageLoading = false
                                        }
                                    }

                                    webViewClient = object : WebViewClient() {
                                        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                                            val uri = request.url
                                            val url = uri.toString()
                                            runCatching { cookieManager?.flush() }
                                            checkAndCaptureCookie(cookieManager)
                                            if (url.startsWith("http://") || url.startsWith("https://")) {
                                                return false
                                            }
                                            return try {
                                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri).apply {
                                                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                                }
                                                context.startActivity(intent)
                                                true
                                            } catch (_: Throwable) {
                                                true
                                            }
                                        }

                                        override fun onPageStarted(view: WebView, url: String?, favicon: android.graphics.Bitmap?) {
                                            super.onPageStarted(view, url, favicon)
                                            runCatching { cookieManager?.flush() }
                                            checkAndCaptureCookie(cookieManager)
                                        }

                                        override fun onPageFinished(view: WebView, url: String?) {
                                            super.onPageFinished(view, url)
                                            pageLoading = false
                                            pageError = ""
                                            runCatching { cookieManager?.flush() }
                                            checkAndCaptureCookie(cookieManager)
                                        }

                                        override fun onReceivedError(
                                            view: WebView,
                                            request: WebResourceRequest,
                                            error: WebResourceError,
                                        ) {
                                            super.onReceivedError(view, request, error)
                                            if (request.isForMainFrame) {
                                                pageLoading = false
                                                pageError = error.description?.toString().orEmpty()
                                                    .ifBlank { context.getString(R.string.spot_page_load_failed) }
                                            }
                                        }
                                    }

                                    loadUrl(SPOTIFY_LOGIN_URL)
                                }
                            } catch (t: Throwable) {
                                webViewInitError = t.localizedMessage ?: "WebView no disponible"
                                pageLoading = false
                                android.view.View(context)
                            }
                        },
                    )
                }

                // Indicador de carga fluido
                if (pageLoading && webViewInitError == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = CanopyColors.Accent,
                            modifier = Modifier.size(36.dp),
                            strokeWidth = 3.dp,
                        )
                    }
                }

                if (pageError.isNotBlank() && webViewInitError == null) {
                    Surface(
                        color = CanopyColors.Surface,
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                stringResource(R.string.spot_load_failed_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = CanopyColors.Danger,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                pageError,
                                style = MaterialTheme.typography.bodyMedium,
                                color = CanopyColors.Muted,
                            )
                            Spacer(Modifier.height(16.dp))
                            TextButton(onClick = {
                                pageLoading = true
                                pageError = ""
                                webView?.reload()
                            }) {
                                Text(stringResource(R.string.action_retry))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showManualDialog) {
        var manualSpdc by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showManualDialog = false },
            title = { Text(stringResource(R.string.spot_spdc_dialog_title), color = CanopyColors.Text) },
            text = {
                Column {
                    Text(
                        stringResource(R.string.spot_spdc_dialog_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = CanopyColors.Muted,
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = manualSpdc,
                        onValueChange = { manualSpdc = it },
                        placeholder = { Text(stringResource(R.string.spot_spdc_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val extracted = SpotifyCanvasRepository.extractSpdc(manualSpdc)
                    if (extracted.isNotBlank()) {
                        onSpdcCaptured(extracted)
                    }
                    showManualDialog = false
                }) {
                    Text(stringResource(R.string.action_save), color = LocalAccent.current)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualDialog = false }) {
                    Text(stringResource(R.string.action_cancel), color = CanopyColors.Muted)
                }
            },
            containerColor = CanopyColors.Surface,
        )
    }
}
