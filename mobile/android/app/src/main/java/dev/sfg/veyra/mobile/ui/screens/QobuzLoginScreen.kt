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
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.qobuz.QOBUZ_BASE_URL
import dev.sfg.veyra.mobile.qobuz.QOBUZ_USER_AGENT
import dev.sfg.veyra.mobile.qobuz.QobuzBootstrap
import dev.sfg.veyra.mobile.qobuz.QobuzBootstrapLoader
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

private const val QOBUZ_CALLBACK_URL = "https://localhost/qobuz-callback"

suspend fun exchangeQobuzCode(
    code: String,
    bootstrap: QobuzBootstrap,
    httpClient: OkHttpClient = OkHttpClient(),
): Pair<String, Long> = withContext(Dispatchers.IO) {
    val url = "$QOBUZ_BASE_URL/oauth/callback".toHttpUrl().newBuilder()
        .addQueryParameter("code", code)
        .addQueryParameter("private_key", bootstrap.oauthPrivateKey)
        .build()

    val request = Request.Builder()
        .url(url)
        .header("Accept", "application/json")
        .header("User-Agent", QOBUZ_USER_AGENT)
        .header("X-App-Id", bootstrap.appId)
        .get()
        .build()

    val response = httpClient.newCall(request).execute()
    val body = response.body.string()
    if (!response.isSuccessful) {
        throw IllegalStateException("Qobuz OAuth exchange failed (${response.code}): ${body.take(200)}")
    }

    val json = JSONObject(body)
    val token = json.optString("token").ifBlank { json.optString("user_auth_token") }
    val userId = json.optLong("user_id", -1L).takeIf { it > 0 }
        ?: json.optJSONObject("user")?.optLong("id", -1L) ?: -1L

    if (token.isBlank() || userId <= 0) {
        throw IllegalStateException("Qobuz OAuth returned incomplete credentials")
    }

    token to userId
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QobuzLoginScreen(
    bootstrapLoader: QobuzBootstrapLoader,
    onSuccess: (token: String, userId: Long) -> Unit,
    onCancel: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var webView by remember { mutableStateOf<WebView?>(null) }
    var bootstrap by remember { mutableStateOf<QobuzBootstrap?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var exchanging by remember { mutableStateOf(false) }
    var reloadTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(reloadTrigger) {
        loading = true
        errorMessage = ""
        try {
            bootstrap = bootstrapLoader.get(refresh = reloadTrigger > 0)
            loading = false
        } catch (e: Exception) {
            android.util.Log.e("QobuzLoginScreen", "Failed to initialize Qobuz login", e)
            errorMessage = e.localizedMessage?.ifBlank { null }
                ?: e.message?.ifBlank { null }
                ?: e.javaClass.simpleName
            loading = false
        }
    }

    BackHandler {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            onCancel()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            runCatching {
                webView?.apply {
                    stopLoading()
                    loadUrl("about:blank")
                    destroy()
                }
                webView = null
            }
        }
    }

    Column(Modifier.fillMaxSize().background(CanopyColors.Canvas)) {
        TopAppBar(
            title = { Text(stringResource(R.string.qobuz_login_title)) },
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.qobuz_back),
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = CanopyColors.Canvas,
                titleContentColor = CanopyColors.Text,
                navigationIconContentColor = CanopyColors.Text,
            ),
        )

        when {
            loading || exchanging -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = CanopyColors.Accent)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = if (exchanging) stringResource(R.string.qobuz_connecting_account)
                            else stringResource(R.string.qobuz_preparing_login),
                            color = CanopyColors.Text,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            errorMessage.isNotBlank() -> {
                Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                        Spacer(Modifier.height(16.dp))
                        androidx.compose.material3.Button(
                            onClick = { reloadTrigger++ },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = CanopyColors.Accent,
                                contentColor = androidx.compose.ui.graphics.Color.Black,
                            ),
                        ) {
                            Text(stringResource(R.string.action_retry))
                        }
                    }
                }
            }
            bootstrap != null -> {
                val bs = bootstrap!!
                val oauthUrl = "https://www.qobuz.com/signin/oauth?ext_app_id=${bs.appId}&redirect_url=${Uri.encode(QOBUZ_CALLBACK_URL)}"

                var popupWebView by remember { mutableStateOf<WebView?>(null) }

                Box(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            try {
                                WebView(context).apply {
                                    webView = this
                                    val loginWebView = this
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    settings.setSupportMultipleWindows(true)
                                    settings.javaScriptCanOpenWindowsAutomatically = true

                                    if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                                        runCatching { WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, false) }
                                    }
                                    if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                                        @Suppress("DEPRECATION")
                                        runCatching { WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_OFF) }
                                    }

                                    runCatching {
                                        android.webkit.CookieManager.getInstance().apply {
                                            setAcceptCookie(true)
                                            setAcceptThirdPartyCookies(loginWebView, true)
                                        }
                                    }

                                    val urlHandler = { uri: Uri ->
                                        if (uri.toString().startsWith(QOBUZ_CALLBACK_URL)) {
                                            val code = uri.getQueryParameter("code_autorisation")
                                            if (!code.isNullOrBlank() && !exchanging) {
                                                exchanging = true
                                                scope.launch {
                                                    try {
                                                        val (token, userId) = exchangeQobuzCode(code, bs)
                                                        popupWebView = null
                                                        onSuccess(token, userId)
                                                    } catch (e: Exception) {
                                                        errorMessage = e.message
                                                            ?: context.getString(R.string.qobuz_auth_exchange_failed)
                                                        exchanging = false
                                                    }
                                                }
                                            } else if (!exchanging) {
                                                val error = uri.getQueryParameter("error")
                                                    ?: context.getString(R.string.qobuz_no_auth_code)
                                                errorMessage = context.getString(R.string.qobuz_login_failed, error)
                                                popupWebView = null
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    }

                                    webViewClient = object : WebViewClient() {
                                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                            val url = request?.url ?: return false
                                            return urlHandler(url)
                                        }

                                        @Deprecated("Deprecated in Java")
                                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                            if (url == null) return false
                                            return urlHandler(Uri.parse(url))
                                        }
                                    }

                                    webChromeClient = object : android.webkit.WebChromeClient() {
                                        override fun onCreateWindow(
                                            view: WebView?,
                                            isDialog: Boolean,
                                            isUserGesture: Boolean,
                                            resultMsg: android.os.Message?
                                        ): Boolean {
                                            val newWebView = WebView(context).apply {
                                                settings.javaScriptEnabled = true
                                                settings.domStorageEnabled = true
                                                settings.setSupportMultipleWindows(true)
                                                if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                                                    runCatching { WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, false) }
                                                }
                                                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                                                    @Suppress("DEPRECATION")
                                                    runCatching { WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_OFF) }
                                                }
                                                runCatching {
                                                    android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                                                }

                                                webViewClient = object : WebViewClient() {
                                                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                                        val url = request?.url ?: return false
                                                        return urlHandler(url)
                                                    }
                                                    @Deprecated("Deprecated in Java")
                                                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                                        if (url == null) return false
                                                        return urlHandler(Uri.parse(url))
                                                    }
                                                }
                                                webChromeClient = object : android.webkit.WebChromeClient() {
                                                    override fun onCloseWindow(window: WebView?) {
                                                        popupWebView = null
                                                    }
                                                }
                                            }
                                            popupWebView = newWebView
                                            val transport = resultMsg?.obj as? WebView.WebViewTransport
                                            transport?.webView = newWebView
                                            resultMsg?.sendToTarget()
                                            return true
                                        }
                                    }
                                }
                            } catch (t: Throwable) {
                                android.util.Log.e("QobuzLoginScreen", "Failed to create WebView", t)
                                errorMessage = t.localizedMessage ?: "Error al inicializar navegador web"
                                View(context)
                            }
                        },
                        update = { view ->
                            if (view is WebView && view.url == null) {
                                view.loadUrl(oauthUrl)
                            }
                        }
                    )

                    if (popupWebView != null) {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = {
                                val pop = popupWebView!!
                                (pop.parent as? android.view.ViewGroup)?.removeView(pop)
                                pop
                            }
                        )
                    }
                }
            }
        }
    }
}
