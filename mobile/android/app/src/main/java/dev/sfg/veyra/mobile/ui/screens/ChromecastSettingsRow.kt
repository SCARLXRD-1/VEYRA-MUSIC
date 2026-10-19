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
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

/** Holds the current Chromecast connection and connecting status. */
internal data class CastState(
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val deviceName: String? = null,
)

/** Observes the active CastSession state (connecting, connected, device name). */
@Composable
internal fun rememberCurrentCastState(): CastState {
    val context = LocalContext.current
    var state by remember { mutableStateOf(CastState()) }

    DisposableEffect(context) {
        val castContext = runCatching { CastContext.getSharedInstance(context) }.getOrNull()
        val sessionManager = castContext?.sessionManager
        val initialSession = sessionManager?.currentCastSession
        if (initialSession != null) {
            state = CastState(
                isConnected = initialSession.isConnected,
                isConnecting = initialSession.isConnecting,
                deviceName = initialSession.castDevice?.friendlyName,
            )
        }

        val listener =
            object : SessionManagerListener<CastSession> {
                override fun onSessionStarted(session: CastSession, sessionId: String) {
                    state = CastState(
                        isConnected = true,
                        isConnecting = false,
                        deviceName = session.castDevice?.friendlyName,
                    )
                }

                override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
                    state = CastState(
                        isConnected = true,
                        isConnecting = false,
                        deviceName = session.castDevice?.friendlyName,
                    )
                }

                override fun onSessionEnded(session: CastSession, error: Int) {
                    state = CastState()
                }

                override fun onSessionSuspended(session: CastSession, reason: Int) {}

                override fun onSessionStarting(session: CastSession) {
                    state = CastState(
                        isConnecting = true,
                        isConnected = false,
                        deviceName = session.castDevice?.friendlyName,
                    )
                }

                override fun onSessionStartFailed(session: CastSession, error: Int) {
                    state = CastState()
                }

                override fun onSessionEnding(session: CastSession) {}

                override fun onSessionResuming(session: CastSession, sessionId: String) {
                    state = CastState(
                        isConnecting = true,
                        isConnected = false,
                        deviceName = session.castDevice?.friendlyName,
                    )
                }

                override fun onSessionResumeFailed(session: CastSession, error: Int) {
                    state = CastState()
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

    return state
}

/** Observes the active CastSession name so UI dynamically updates when connected. */
@Composable
internal fun rememberCurrentCastDeviceName(): String? {
    val state = rememberCurrentCastState()
    return if (state.isConnected) state.deviceName else null
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
    val castState = rememberCurrentCastState()
    val isConnected = castState.isConnected
    val isConnecting = castState.isConnecting

    val infiniteTransition = rememberInfiniteTransition(label = "CastPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.40f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "PulseAlpha",
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "PulseScale",
    )

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
        val iconShape = RoundedCornerShape(12.dp)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(iconShape)
                .background(
                    when {
                        isConnected -> LocalAccent.current.copy(alpha = 0.18f)
                        isConnecting -> LocalAccent.current.copy(alpha = 0.12f * pulseAlpha)
                        else -> CanopyColors.Canvas
                    }
                )
                .border(
                    1.dp,
                    when {
                        isConnected -> LocalAccent.current.copy(alpha = 0.40f)
                        isConnecting -> LocalAccent.current.copy(alpha = 0.55f * pulseAlpha)
                        else -> CanopyColors.Rule
                    },
                    iconShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (isConnected || isConnecting) {
                Icon(
                    painter = painterResource(R.drawable.ic_notification),
                    contentDescription = "Veyra Music",
                    tint = if (isConnected) LocalAccent.current else LocalAccent.current.copy(alpha = pulseAlpha),
                    modifier = Modifier
                        .size(22.dp)
                        .then(
                            if (isConnecting) Modifier.graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                            else Modifier
                        ),
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Cast,
                    contentDescription = null,
                    tint = CanopyColors.MutedStrong,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                if (isConnected || isConnecting) "Chromecast • Veyra Music" else "Chromecast",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = CanopyColors.Text,
            )
            when {
                isConnecting -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(LocalAccent.current.copy(alpha = pulseAlpha))
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            stringResource(R.string.cast_connecting_veyra),
                            color = LocalAccent.current.copy(alpha = pulseAlpha),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        )
                    }
                }
                isConnected && castState.deviceName != null -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(LocalAccent.current)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            stringResource(R.string.cast_connected_format, castState.deviceName),
                            color = LocalAccent.current,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                else -> {
                    Text(
                        stringResource(R.string.cast_play_speakers),
                        color = CanopyColors.Muted,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
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
    val castState = rememberCurrentCastState()
    val isConnected = castState.isConnected
    val isConnecting = castState.isConnecting
    val shape = RoundedCornerShape(16.dp)

    val infiniteTransition = rememberInfiniteTransition(label = "CastConnectPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.40f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "PulseAlpha",
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "PulseScale",
    )

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
                    Modifier.size(42.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isConnected -> LocalAccent.current.copy(alpha = 0.20f)
                                isConnecting -> LocalAccent.current.copy(alpha = 0.15f * pulseAlpha)
                                else -> CanopyColors.Canvas
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                isConnected -> LocalAccent.current.copy(alpha = 0.45f)
                                isConnecting -> LocalAccent.current.copy(alpha = 0.6f * pulseAlpha)
                                else -> CanopyColors.Rule
                            },
                            CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                if (isConnected || isConnecting) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notification),
                        contentDescription = "Veyra Music",
                        tint = if (isConnected) LocalAccent.current else LocalAccent.current.copy(alpha = pulseAlpha),
                        modifier = Modifier
                            .size(22.dp)
                            .then(
                                if (isConnecting) Modifier.graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                                else Modifier
                            ),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Cast,
                        contentDescription = null,
                        tint = CanopyColors.MutedStrong,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isConnected || isConnecting) "Chromecast • Veyra Music" else "Chromecast",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CanopyColors.Text,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier.size(7.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isConnected -> CanopyColors.Accent
                                        isConnecting -> LocalAccent.current.copy(alpha = pulseAlpha)
                                        else -> CanopyColors.Muted
                                    }
                                )
                    )
                    Spacer(Modifier.width(6.dp))
                    val subtitle = when {
                        isConnecting -> stringResource(R.string.cast_connecting_veyra)
                        isConnected && castState.deviceName != null -> stringResource(R.string.cast_connected_format, castState.deviceName)
                        else -> stringResource(R.string.cast_speakers_displays)
                    }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isConnected || isConnecting) LocalAccent.current else CanopyColors.Muted,
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
