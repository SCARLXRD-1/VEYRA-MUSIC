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

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.mediarouter.app.MediaRouteButton
import androidx.mediarouter.media.MediaRouter
import androidx.mediarouter.media.MediaRouterParams
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent

private tailrec fun Context.findFragmentActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is android.content.ContextWrapper -> baseContext.findFragmentActivity()
    else -> null
}

/** Observes the active CastSession name so UI dynamically updates when connected. */
@Composable
internal fun rememberCurrentCastDeviceName(): String? {
    val context = LocalContext.current
    var castDeviceName by remember { mutableStateOf<String?>(null) }

    DisposableEffect(context) {
        val castContext = runCatching { CastContext.getSharedInstance(context) }.getOrNull()
        val sessionManager = castContext?.sessionManager
        castDeviceName = sessionManager?.currentCastSession?.castDevice?.friendlyName

        val listener =
            object : SessionManagerListener<CastSession> {
                override fun onSessionStarted(session: CastSession, sessionId: String) {
                    castDeviceName = session.castDevice?.friendlyName
                }

                override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
                    castDeviceName = session.castDevice?.friendlyName
                }

                override fun onSessionEnded(session: CastSession, error: Int) {
                    castDeviceName = null
                }

                override fun onSessionSuspended(session: CastSession, reason: Int) {}

                override fun onSessionStarting(session: CastSession) {}

                override fun onSessionStartFailed(session: CastSession, error: Int) {
                    castDeviceName = null
                }

                override fun onSessionEnding(session: CastSession) {}

                override fun onSessionResuming(session: CastSession, sessionId: String) {}

                override fun onSessionResumeFailed(session: CastSession, error: Int) {
                    castDeviceName = null
                }
            }

        runCatching {
            sessionManager?.addSessionManagerListener(listener, CastSession::class.java)
        }
        onDispose {
            runCatching {
                sessionManager?.removeSessionManagerListener(listener, CastSession::class.java)
            }
        }
    }

    return castDeviceName
}

/**
 * Underlying MediaRouteButton wrapped for Jetpack Compose, configured to display the in-app dialog
 * directly.
 */
@Composable
internal fun ChromecastMediaRouteButton(
    modifier: Modifier = Modifier,
    onButtonReady: ((MediaRouteButton) -> Unit)? = null,
) {
    val context = LocalContext.current
    val hostActivity = remember(context) { context.findFragmentActivity() }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val resolvedActivity = ctx.findFragmentActivity() ?: hostActivity
            val buttonContext = resolvedActivity ?: ctx

            runCatching {
                val router = MediaRouter.getInstance(buttonContext)
                val currentParams = router.routerParams
                val newParams =
                    (if (currentParams != null) MediaRouterParams.Builder(currentParams)
                        else MediaRouterParams.Builder())
                        .setOutputSwitcherEnabled(false)
                        .build()
                router.routerParams = newParams
            }

            MediaRouteButton(buttonContext).apply {
                if (resolvedActivity != null) {
                    runCatching {
                        CastButtonFactory.setUpMediaRouteButton(resolvedActivity, this)
                    }
                }
                onButtonReady?.invoke(this)
            }
        },
        update = { button -> onButtonReady?.invoke(button) },
    )
}

/** Receiver picker row inside Settings -> Devices, matching the flat modern Settings style. */
@Composable
internal fun ChromecastSettingsRow(modifier: Modifier = Modifier) {
    var routeButton by remember { mutableStateOf<MediaRouteButton?>(null) }
    val castDeviceName = rememberCurrentCastDeviceName()
    val isConnected = castDeviceName != null

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable {
                    runCatching {
                        if (routeButton?.showDialog() != true) {
                            routeButton?.performClick()
                        }
                    }
                }
                .defaultMinSize(minHeight = 64.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                "Chromecast",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                color = CanopyColors.Text,
            )
            val subtitle =
                if (isConnected && castDeviceName != null) {
                    stringResource(R.string.cast_connected_format, castDeviceName)
                } else {
                    stringResource(R.string.cast_play_speakers)
                }
            Text(
                subtitle,
                color = if (isConnected) LocalAccent.current else CanopyColors.Muted,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        ChromecastMediaRouteButton(
            modifier = Modifier.size(40.dp),
            onButtonReady = { routeButton = it },
        )
    }
}

/** Receiver picker row inside Connect / Devices Screen matching FrostedDeviceRow style. */
@Composable
internal fun ChromecastConnectRow(modifier: Modifier = Modifier) {
    var routeButton by remember { mutableStateOf<MediaRouteButton?>(null) }
    val castDeviceName = rememberCurrentCastDeviceName()
    val isConnected = castDeviceName != null
    val shape = RoundedCornerShape(16.dp)

    Surface(
        onClick = {
            runCatching {
                if (routeButton?.showDialog() != true) {
                    routeButton?.performClick()
                }
            }
        },
        shape = shape,
        color = Color.Transparent,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier.size(40.dp)
                        .clip(CircleShape)
                        .background(CanopyColors.Canvas)
                        .border(1.dp, CanopyColors.Rule, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Cast,
                    contentDescription = null,
                    tint = if (isConnected) LocalAccent.current else CanopyColors.MutedStrong,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Chromecast",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CanopyColors.Text,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier.size(7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isConnected) CanopyColors.Accent else CanopyColors.Muted
                                )
                    )
                    Spacer(Modifier.width(6.dp))
                    val subtitle =
                        if (isConnected && castDeviceName != null) {
                            stringResource(R.string.cast_connected_format, castDeviceName)
                        } else {
                            stringResource(R.string.cast_speakers_displays)
                        }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = CanopyColors.Muted,
                    )
                }
            }

            ChromecastMediaRouteButton(
                modifier = Modifier.size(40.dp),
                onButtonReady = { routeButton = it },
            )
        }
    }
}
