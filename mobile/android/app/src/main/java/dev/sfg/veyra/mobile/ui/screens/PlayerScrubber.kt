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

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.PlaybackSnapshot
import dev.sfg.veyra.mobile.model.TransitionMarker
import dev.sfg.veyra.mobile.ui.components.durationText
import dev.sfg.veyra.mobile.ui.components.rememberRainbowBrush
import dev.sfg.veyra.mobile.ui.components.rememberTransitionGlow
import dev.sfg.veyra.mobile.ui.components.transitionProgress
import dev.sfg.veyra.mobile.ui.components.transitionStyleLabelLocalized

/**
 * Expressive scrubber progress bar with active dragging state,
 * buffered range indicator, and high-precision duration readouts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScrubber(
    playback: PlaybackSnapshot,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
    /** The planned transition out of this track, drawn as a band on the track. Null when none. */
    transition: TransitionMarker? = null,
    /** Raw overlap progress when the visible track has already changed to the incoming song. */
    mixProgress: Float? = null,
    showBitrate: Boolean = false,
    bitrateKbps: Int = 0,
    isQobuz: Boolean = false,
) {
    val marker = transition?.takeIf {
        it.trackId.isNotBlank() && it.trackId == playback.currentTrack?.id && it.startMs > 0 && it.startMs < playback.durationMs
    }
    // One eased number drives every transition affordance: the glow, the sweep, and the readout.
    val glow = rememberTransitionGlow(mixProgress ?: transitionProgress(playback, marker))
    val rainbow = rememberRainbowBrush()
    // Effective duration ends where the transition starts, subtracting the transition time & unplayed tail
    val duration = (marker?.startMs ?: playback.durationMs).coerceAtLeast(1)
    val buffered = playback.bufferedPositionMs.coerceIn(0, duration)
    var dragging by remember(playback.currentTrack?.id) { mutableStateOf(false) }
    var dragPosition by remember(playback.currentTrack?.id) { mutableFloatStateOf(0f) }

    val interactionSource = remember { MutableInteractionSource() }
    val isUserDragging by interactionSource.collectIsDraggedAsState()
    val isUserPressed by interactionSource.collectIsPressedAsState()
    val isInteracting = dragging || isUserDragging || isUserPressed

    val currentPosition = if (isInteracting) {
        dragPosition
    } else {
        playback.positionMs.toFloat().coerceIn(0f, duration.toFloat())
    }

    val trackHeight by animateDpAsState(
        targetValue = if (isInteracting) 8.dp else 4.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "ScrubberTrackHeight",
    )

    val thumbVisibleSize by animateDpAsState(
        targetValue = if (isInteracting) 14.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "ScrubberThumbVisibleSize",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "ScrubberWaveMotion")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ScrubberWavePhase",
    )
    val targetWaveAmplitude = if (playback.isPlaying) 2.4f else 0f
    val waveAmplitude by animateFloatAsState(
        targetValue = targetWaveAmplitude,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
        label = "ScrubberWaveAmplitude",
    )
    val density = LocalDensity.current
    val haptics = LocalHapticFeedback.current

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp),
            contentAlignment = Alignment.Center,
        ) {
            Slider(
                value = currentPosition,
                onValueChange = {
                    dragging = true
                    dragPosition = it
                },
                onValueChangeFinished = {
                    dragging = false
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSeek(dragPosition.toLong())
                },
                valueRange = 0f..duration.toFloat(),
                interactionSource = interactionSource,
                thumb = {
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (thumbVisibleSize > 0.5.dp) {
                            Box(
                                modifier = Modifier
                                    .size(thumbVisibleSize)
                                    .shadow(6.dp, CircleShape, spotColor = Color(0xFF38BDF8))
                                    .background(Color.White, CircleShape),
                            )
                        }
                    }
                },
                track = { sliderState ->
                    val rangeSpan = (sliderState.valueRange.endInclusive - sliderState.valueRange.start).coerceAtLeast(1f)
                    val fraction = ((sliderState.value - sliderState.valueRange.start) / rangeSpan).coerceIn(0f, 1f)
                    val bufferFraction = if (duration > 0) {
                        (buffered.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    val wavePath = remember { Path() }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp),
                        ) {
                            val trackW = size.width
                            val centerY = size.height / 2f
                            val endX = (trackW * fraction).coerceIn(0f, trackW)
                            val strokeWidth = trackHeight.toPx()

                            // 1. Guía inactiva de fondo con canal empotrado neumórfico
                            drawLine(
                                color = Color(0xFF080C14).copy(alpha = 0.85f),
                                start = Offset(0f, centerY),
                                end = Offset(trackW, centerY),
                                strokeWidth = strokeWidth + 2.5.dp.toPx(),
                                cap = StrokeCap.Round,
                            )
                            drawLine(
                                color = Color.White.copy(alpha = 0.15f),
                                start = Offset(0f, centerY),
                                end = Offset(trackW, centerY),
                                strokeWidth = strokeWidth,
                                cap = StrokeCap.Round,
                            )

                            // 2. Trazo de buffer
                            if (bufferFraction > 0.001f) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.24f),
                                    start = Offset(0f, centerY),
                                    end = Offset(trackW * bufferFraction, centerY),
                                    strokeWidth = strokeWidth,
                                    cap = StrokeCap.Round,
                                )
                            }

                            // 3. Onda sinusoidal activa de alta fidelidad ultra-fluida
                            if (endX > 0.5f) {
                                wavePath.reset()
                                wavePath.moveTo(0f, centerY)
                                if (waveAmplitude > 0.05f && endX > 10f) {
                                    val wavelengthPx = with(density) { 54.dp.toPx() }
                                    val ampPx = with(density) { waveAmplitude.dp.toPx() }
                                    val stepPx = 1.2f
                                    val k = (2 * Math.PI / wavelengthPx).toFloat()
                                    val leadInPx = with(density) { 20.dp.toPx() }
                                    var x = stepPx
                                    while (x < endX) {
                                        val tIn = (x / leadInPx).coerceIn(0f, 1f)
                                        val tOut = ((endX - x) / leadInPx).coerceIn(0f, 1f)
                                        val env = (tIn * tIn * (3f - 2f * tIn)) * (tOut * tOut * (3f - 2f * tOut))
                                        val waveVal = kotlin.math.sin(k * x - wavePhase)
                                        val y = centerY + waveVal * (ampPx * env)
                                        wavePath.lineTo(x, y)
                                        x += stepPx
                                    }
                                }
                                wavePath.lineTo(endX, centerY)

                                val electricBrush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF1E6CEB),
                                        Color(0xFF2275F8),
                                        Color(0xFF38BDF8),
                                    ),
                                    startX = 0f,
                                    endX = endX.coerceAtLeast(1f),
                                )

                                // Resplandor exterior (bloom suave)
                                drawPath(
                                    path = wavePath,
                                    brush = electricBrush,
                                    style = Stroke(width = strokeWidth + 3.dp.toPx(), cap = StrokeCap.Round),
                                    alpha = 0.22f,
                                )

                                // Trazo principal
                                drawPath(
                                    path = wavePath,
                                    brush = if (glow > 0.01f) rainbow else electricBrush,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                )

                                // Cabezal iluminado de la onda
                                drawCircle(
                                    color = Color(0xFF38BDF8).copy(alpha = 0.50f),
                                    radius = 4.5.dp.toPx(),
                                    center = Offset(endX, centerY),
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.5.dp.toPx(),
                                    center = Offset(endX, centerY),
                                )
                            }
                        }
                    }
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Time readouts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = durationText(currentPosition.toLong()),
                color = Color.White.copy(alpha = 0.62f),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Medium,
                ),
            )
            if (glow > 0.01f) {
                val styleDesc = transition?.style?.takeIf { it.isNotBlank() }?.let { transitionStyleLabelLocalized(it) }
                val shifting = stringResource(R.string.playerx_transition_label)
                val labelText = if (styleDesc != null) "$shifting • $styleDesc" else shifting
                Text(
                    text = labelText,
                    modifier = Modifier.alpha(glow),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.SemiBold,
                        brush = rainbow,
                    ),
                )
            } else if (showBitrate && bitrateKbps > 0) {
                val qobuzActive = isQobuz || playback.currentTrack?.isQobuz == true
                dev.sfg.veyra.mobile.ui.components.LosslessBadge(
                    showBitrate = showBitrate,
                    bitrateKbps = bitrateKbps,
                    isQobuz = qobuzActive,
                )
            }
            val remainingMs = (duration - currentPosition.toLong()).coerceAtLeast(0)
            Text(
                text = "-${durationText(remainingMs)}",
                color = Color.White.copy(alpha = 0.62f),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}
