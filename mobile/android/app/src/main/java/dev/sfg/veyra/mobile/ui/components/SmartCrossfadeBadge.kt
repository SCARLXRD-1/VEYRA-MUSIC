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

package dev.sfg.veyra.mobile.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.Track
import dev.sfg.veyra.mobile.ui.theme.CanopyColors

/**
 * Returns a human-friendly description for the Smart Crossfade transition style.
 *
 * English-only fallback kept for non-Compose callers; Compose UI should resolve the
 * localized variant through [transitionStyleLabelLocalized].
 */
fun transitionStyleLabel(style: String): String {
    return when (style.lowercase().trim()) {
        "dj_blend" -> "Beat Matched"
        "dj_filter" -> "Filtered Blend"
        "equal_power" -> "Equal Power"
        "gapless" -> "Seamless Handoff"
        "tempo_matched" -> "Tempo Matched"
        "bass_first" -> "Bass Swap"
        "smart" -> "Smart Mix"
        else -> if (style.isNotBlank()) {
            derivedStyleLabel(style)
        } else {
            "Smart Mix"
        }
    }
}

private fun derivedStyleLabel(style: String): String =
    style.replace('_', ' ')
        .split(' ')
        .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }

/**
 * Resource id of the localized Smart Crossfade transition style label,
 * or 0 when the style id is unknown and must be derived from the raw value.
 */
@StringRes
fun transitionStyleLabelRes(style: String): Int = when (style.lowercase().trim()) {
    "dj_blend" -> R.string.scbadge_beat_matched
    "dj_filter" -> R.string.scbadge_filtered_blend
    "equal_power" -> R.string.scbadge_equal_power
    "gapless" -> R.string.scbadge_seamless_handoff
    "tempo_matched" -> R.string.scbadge_tempo_matched
    "bass_first" -> R.string.scbadge_bass_swap
    "smart" -> R.string.scbadge_smart_mix
    else -> 0
}

/**
 * Localized variant of [transitionStyleLabel] for Compose call sites.
 */
@Composable
fun transitionStyleLabelLocalized(style: String): String {
    val resId = transitionStyleLabelRes(style)
    if (resId != 0) return stringResource(resId)
    return if (style.isNotBlank()) {
        derivedStyleLabel(style)
    } else {
        stringResource(R.string.scbadge_smart_mix)
    }
}

/**
 * Mini animated live equalizer graphic with 3 vertical undulating bars.
 */
@Composable
fun SmartCrossfadeWaveform(
    color: Color = Color.White,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "SmartCrossfadeWaveform")
    val phase1 by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(520, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "Bar1",
    )
    val phase2 by transition.animateFloat(
        initialValue = 0.90f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(440, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "Bar2",
    )
    val phase3 by transition.animateFloat(
        initialValue = 0.40f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(610, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "Bar3",
    )

    Row(
        modifier = modifier.height(13.dp),
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .width(2.5.dp)
                .height(13.dp * phase1)
                .clip(CircleShape)
                .background(color),
        )
        Box(
            Modifier
                .width(2.5.dp)
                .height(13.dp * phase2)
                .clip(CircleShape)
                .background(color),
        )
        Box(
            Modifier
                .width(2.5.dp)
                .height(13.dp * phase3)
                .clip(CircleShape)
                .background(color),
        )
    }
}

/**
 * Premium frosted-glass indicator chip showing that Smart Crossfade is actively blending
 * into the upcoming song, complete with live animated waveforms, style chips, and track preview.
 */
@Composable
fun SmartCrossfadeBadge(
    visible: Boolean,
    style: String,
    incomingTrack: Track? = null,
    progress: Float = 0f,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(360)) + expandVertically(tween(360, easing = FastOutSlowInEasing)),
        exit = fadeOut(tween(280)) + shrinkVertically(tween(280)),
        modifier = modifier,
    ) {
        val rainbow = rememberRainbowBrush(spanPx = 360f, periodMs = 4_000)
        val styleLabel = transitionStyleLabelLocalized(style)
        val incomingTitle = incomingTrack?.title.orEmpty()

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.22f),
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.18f),
                        ),
                    ),
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                SmartCrossfadeWaveform(color = Color.White.copy(alpha = 0.92f))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = styleLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.3.sp,
                        brush = rainbow,
                    ),
                )
                if (incomingTitle.isNotBlank()) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier
                            .size(3.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.40f)),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (progress >= 0.5f) {
                            stringResource(R.string.scbadge_from_previous_track)
                        } else {
                            stringResource(R.string.scbadge_into_format, incomingTitle)
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = CanopyColors.Text.copy(alpha = 0.72f),
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
