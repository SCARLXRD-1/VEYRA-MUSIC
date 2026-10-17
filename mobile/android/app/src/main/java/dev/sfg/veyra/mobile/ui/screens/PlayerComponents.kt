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

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.DevicesFold
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Earbuds
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Headset
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Speaker
import androidx.compose.material.icons.rounded.Subtitles
import androidx.compose.material.icons.rounded.TabletAndroid
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.ui.res.stringResource
import dev.sfg.veyra.connect.R
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import dev.sfg.veyra.mobile.ui.modifiers.tactilePress
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.mobile.audio.AudioOutputType
import dev.sfg.veyra.mobile.audio.canReadBluetoothNames
import dev.sfg.veyra.mobile.audio.isFoldableHardware
import dev.sfg.veyra.mobile.audio.isTabletForm
import dev.sfg.veyra.mobile.audio.rememberAudioOutput
import dev.sfg.veyra.mobile.model.PlaybackSnapshot
import dev.sfg.veyra.mobile.model.PlaybackTarget
import dev.sfg.veyra.mobile.model.PlaybackTargetState
import dev.sfg.veyra.mobile.ui.components.TrackActionsPopup
import dev.sfg.veyra.mobile.ui.theme.CanopyColors

@Composable
fun ExplicitBadge(modifier: Modifier = Modifier) {
    Surface(
        color = Color.White.copy(alpha = 0.40f),
        shape = RoundedCornerShape(3.dp),
        modifier = modifier.size(16.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("E", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
    }
}

/** Animated popping favorite button. */
@Composable
fun AnimatedFavoriteButton(liked: Boolean, onLiked: () -> Unit, modifier: Modifier = Modifier) {
    val heartScale by
        animateFloatAsState(
            targetValue = if (liked) 1.15f else 1.0f,
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            label = "HeartScale",
        )

    IconButton(
        onClick = onLiked,
        modifier =
            modifier.size(48.dp).graphicsLayer {
                scaleX = heartScale
                scaleY = heartScale
            },
    ) {
        Icon(
            imageVector = if (liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (liked) {
                stringResource(R.string.playerx_unlike_cd)
            } else {
                stringResource(R.string.playerx_like_cd)
            },
            tint = if (liked) CanopyColors.Favorite else Color.White.copy(alpha = 0.75f),
            modifier = Modifier.size(28.dp),
        )
    }
}

/** Favourite star and overflow button, shared by the full title row and the lyrics header. */
@Composable
fun TrackActionButtons(
    liked: Boolean,
    onLiked: () -> Unit,
    onMore: (() -> Unit)?,
    onShare: (() -> Unit)?,
    onOpenMenu: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val starGradient = if (liked) {
            Brush.verticalGradient(
                listOf(
                    Color(0xFF1E6CEB).copy(alpha = 0.45f),
                    Color(0xFF0F3A78).copy(alpha = 0.60f),
                ),
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color(0xFF151D2D),
                    Color(0xFF0E1422),
                ),
            )
        }
        val starBorder = if (liked) {
            Brush.verticalGradient(
                listOf(
                    Color(0xFF38BDF8).copy(alpha = 0.65f),
                    Color(0xFF1E6CEB).copy(alpha = 0.25f),
                ),
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.12f),
                    Color.Black.copy(alpha = 0.40f),
                ),
            )
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(starGradient)
                .border(1.dp, starBorder, CircleShape)
                .tactilePress(onClick = onLiked),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (liked) Icons.Filled.Star else Icons.Filled.StarOutline,
                contentDescription = if (liked) {
                    stringResource(R.string.playerx_favorited_cd)
                } else {
                    stringResource(R.string.action_favorite)
                },
                tint = if (liked) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(20.dp),
            )
        }

        val moreGradient = Brush.verticalGradient(
            listOf(
                Color(0xFF151D2D),
                Color(0xFF0E1422),
            ),
        )
        val moreBorder = Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.12f),
                Color.Black.copy(alpha = 0.40f),
            ),
        )

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(moreGradient)
                .border(1.dp, moreBorder, CircleShape)
                .tactilePress(onClick = { if (onShare != null) onOpenMenu() else onMore?.invoke() }),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.MoreHoriz,
                contentDescription = stringResource(R.string.playerx_more_options_cd),
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * Top pill drag handle.
 */
@Composable
fun PlayerTopHandle(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onDismiss)
                .padding(top = 14.dp, bottom = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF080C14))
                .border(
                    0.8.dp,
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.8f), Color.White.copy(alpha = 0.08f)),
                    ),
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.65f),
                                Color.White.copy(alpha = 0.35f),
                            ),
                        ),
                    ),
            )
        }
    }
}

/** Track title, artist subtitle, inline explicit badge, star & more buttons. */
@Composable
fun TrackInfoRow(
    track: dev.sfg.veyra.mobile.model.Track,
    liked: Boolean,
    onLiked: () -> Unit,
    onMore: (() -> Unit)? = null,
    onAddToPlaylist: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null,
    // Null when the track carries no album or artist id to open.
    onOpenAlbum: (() -> Unit)? = null,
    onOpenArtist: (() -> Unit)? = null,
    isDownloaded: Boolean = false,
    onDownload: (() -> Unit)? = null,
    onRemoveDownload: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var menuOpen by remember { mutableStateOf(false) }

    if (menuOpen) {
        TrackActionsPopup(
            track = track,
            onDismiss = { menuOpen = false },
            onViewQueue = onMore,
            onAddToPlaylist = onAddToPlaylist,
            onDownload = if (!isDownloaded) onDownload else null,
            onRemoveDownload = if (isDownloaded) onRemoveDownload else null,
            onShare = onShare,
            onViewArtist = onOpenArtist,
            onViewAlbum = onOpenAlbum,
        )
    }

    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Track Title & Artist with smooth transition animation when song changes
        androidx.compose.animation.AnimatedContent(
            targetState = track,
            transitionSpec = {
                (androidx.compose.animation.fadeIn(
                        androidx.compose.animation.core.tween(400, delayMillis = 60)
                    ) +
                        androidx.compose.animation.slideInVertically(
                            androidx.compose.animation.core.tween(400, delayMillis = 60)
                        ) {
                            it / 3
                        })
                    .togetherWith(
                        androidx.compose.animation.fadeOut(
                            androidx.compose.animation.core.tween(200)
                        ) +
                            androidx.compose.animation.slideOutVertically(
                                androidx.compose.animation.core.tween(200)
                            ) {
                                -it / 3
                            }
                    )
            },
            label = "TrackInfoTransition",
            modifier = Modifier.weight(1f).padding(end = 12.dp),
        ) { currentTrack ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = currentTrack.title,
                        style =
                            MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                            ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                        modifier =
                            Modifier.weight(1f, fill = false)
                                .then(
                                    if (onOpenAlbum != null) {
                                        Modifier.clickable(onClick = onOpenAlbum)
                                    } else {
                                        Modifier
                                    }
                                )
                                .basicMarquee(initialDelayMillis = 4000, repeatDelayMillis = 3000),
                    )
                    if (currentTrack.explicit) {
                        Spacer(Modifier.width(6.dp))
                        ExplicitBadge()
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = currentTrack.artist,
                    color = Color.White.copy(alpha = 0.65f),
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier.then(
                                if (onOpenArtist != null) {
                                    Modifier.clickable(onClick = onOpenArtist)
                                } else {
                                    Modifier
                                }
                            )
                            .basicMarquee(initialDelayMillis = 4000, repeatDelayMillis = 3000),
                )
            }
        }

        TrackActionButtons(
            liked = liked,
            onLiked = onLiked,
            onMore = onMore,
            onShare = onShare,
            onOpenMenu = { menuOpen = true },
        )
    }
}

/** Bottom quick destinations bar with player tools, active output route, DSP deck, and queue badge count. */
@Composable
fun PlayerBottomDestinations(
    targets: PlaybackTargetState,
    upcomingCount: Int,
    onLyrics: () -> Unit,
    lyricsActive: Boolean = false,
    onDevices: () -> Unit,
    onQueue: () -> Unit,
    queueActive: Boolean = false,
    onAudioDeck: () -> Unit = {},
    audioDeckActive: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DestinationButton(
            active = lyricsActive,
            onClick = onLyrics,
            contentDescription = if (lyricsActive) {
                stringResource(R.string.playerx_hide_lyrics_cd)
            } else {
                stringResource(R.string.nav_lyrics)
            },
            icon = Icons.Rounded.Subtitles,
        )

        OutputRoutePill(targets = targets, onClick = onDevices)

        DestinationButton(
            active = audioDeckActive,
            onClick = onAudioDeck,
            contentDescription = stringResource(R.string.player_dsp_deck_button),
            icon = Icons.Rounded.Tune,
            badge = if (audioDeckActive) {
                {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF38BDF8)),
                    )
                }
            } else null,
        )

        DestinationButton(
            active = queueActive,
            onClick = onQueue,
            contentDescription = if (queueActive) {
                stringResource(R.string.playerx_hide_queue_cd)
            } else {
                stringResource(R.string.nav_queue)
            },
            icon = Icons.AutoMirrored.Rounded.List,
            badge = if (upcomingCount > 0) {
                {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (queueActive) Color(0xFF38BDF8) else Color(0xFF1E293B))
                            .border(
                                0.8.dp,
                                if (queueActive) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.20f),
                                CircleShape,
                            )
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                    ) {
                        Text(
                            text = if (upcomingCount > 99) "99+" else upcomingCount.toString(),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (queueActive) Color(0xFF080C14) else Color.White,
                        )
                    }
                }
            } else null,
        )
    }
}

@Composable
private fun DestinationButton(
    active: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
    icon: ImageVector,
    badge: (@Composable androidx.compose.foundation.layout.BoxScope.() -> Unit)? = null,
) {
    val discGradient = if (active) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF1E6CEB).copy(alpha = 0.45f),
                Color(0xFF0F3A78).copy(alpha = 0.60f),
            ),
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFF141C2C),
                Color(0xFF0D1422),
            ),
        )
    }
    val discBorder = if (active) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF38BDF8).copy(alpha = 0.65f),
                Color(0xFF1E6CEB).copy(alpha = 0.25f),
            ),
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.10f),
                Color.Black.copy(alpha = 0.35f),
            ),
        )
    }

    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(discGradient)
            .border(1.dp, discBorder, CircleShape)
            .tactilePress(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (badge != null) {
            BadgedBox(badge = badge) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = if (active) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.78f),
                    modifier = Modifier.size(20.dp),
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (active) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.78f),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun OutputRoutePill(targets: PlaybackTargetState, onClick: () -> Unit) {
    val isLocal = targets.selected is PlaybackTarget.LocalPhone
    val output by rememberAudioOutput()

    val activeDeviceName =
        if (isLocal) {
            output.name
        } else {
            targets.devices.firstOrNull { it.isActive }?.name
                ?: stringResource(R.string.playerx_connected_device)
        }
    val context = LocalContext.current
    val routeIcon = if (isLocal) output.type.icon(context) else Icons.Rounded.Cast
    val needsNameAccess = isLocal && output.type.isBluetooth && !context.canReadBluetoothNames()

    val nameAccess =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color(0xFF090D17))
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.16f),
                        Color.White.copy(alpha = 0.04f),
                        Color.Black.copy(alpha = 0.40f),
                    ),
                ),
                CircleShape,
            )
            .tactilePress(onClick = {
                if (needsNameAccess) {
                    nameAccess.launch(Manifest.permission.BLUETOOTH_CONNECT)
                }
                onClick()
            })
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF38BDF8)),
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = routeIcon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.90f),
                modifier = Modifier.size(15.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = activeDeviceName,
                style =
                    MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp,
                    ),
                color = Color.White.copy(alpha = 0.90f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Maps a physical output route to the glyph that reads fastest at 16dp. */
private fun AudioOutputType.icon(context: android.content.Context): ImageVector =
    when (this) {
        AudioOutputType.PHONE_SPEAKER,
        AudioOutputType.EARPIECE ->
            if (context.isFoldableHardware() && context.isTabletForm()) {
                Icons.Rounded.DevicesFold
            } else if (context.isTabletForm()) {
                Icons.Rounded.TabletAndroid
            } else {
                Icons.Rounded.PhoneAndroid
            }
        AudioOutputType.WIRED_HEADPHONES -> Icons.Rounded.Headphones
        AudioOutputType.BLUETOOTH_HEADPHONES -> Icons.Rounded.Headphones
        AudioOutputType.BLUETOOTH_EARBUDS -> Icons.Rounded.Earbuds
        AudioOutputType.BLUETOOTH_SPEAKER -> Icons.Rounded.Speaker
        AudioOutputType.CAR -> Icons.Rounded.DirectionsCar
        AudioOutputType.USB -> Icons.Rounded.Usb
        AudioOutputType.HDMI -> Icons.Rounded.Tv
        AudioOutputType.HEARING_AID -> Icons.Rounded.Hearing
        AudioOutputType.UNKNOWN -> Icons.Rounded.Headset
    }

fun PlaybackSnapshot.playingFromLabel(context: android.content.Context): String {
    val engagement =
        Regex("\\b(?:plays?|views?|listeners?|subscribers?)\\b", RegexOption.IGNORE_CASE)
    return contextTitle.takeIf { it.isNotBlank() && !engagement.containsMatchIn(it) }
        ?: currentTrack?.album?.takeIf { it.isNotBlank() && !engagement.containsMatchIn(it) }
        ?: context.getString(R.string.playerx_your_queue)
}
