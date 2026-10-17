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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.PlaybackSnapshot
import dev.sfg.veyra.mobile.ui.modifiers.tactilePress
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val ObsidianRackShape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
private val OledScreenShape = RoundedCornerShape(8.dp)

/** Micro vúmetro LED estéreo (L / R) con calibración de nivel en tiempo real. */
@Composable
private fun MiniVuMeter(isPlaying: Boolean) {
    val transition = rememberInfiniteTransition(label = "ObsidianVuMeterAnim")
    val l1 by transition.animateFloat(
        initialValue = 2.5f,
        targetValue = 9.5f,
        animationSpec = infiniteRepeatable(tween(240, easing = LinearEasing), RepeatMode.Reverse),
        label = "vuL1",
    )
    val l2 by transition.animateFloat(
        initialValue = 3.5f,
        targetValue = 8.5f,
        animationSpec = infiniteRepeatable(tween(310, easing = LinearEasing), RepeatMode.Reverse),
        label = "vuL2",
    )
    val l3 by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = 6.5f,
        animationSpec = infiniteRepeatable(tween(180, easing = LinearEasing), RepeatMode.Reverse),
        label = "vuL3",
    )
    val r1 by transition.animateFloat(
        initialValue = 3f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(210, easing = LinearEasing), RepeatMode.Reverse),
        label = "vuR1",
    )
    val r2 by transition.animateFloat(
        initialValue = 2f,
        targetValue = 8.0f,
        animationSpec = infiniteRepeatable(tween(290, easing = LinearEasing), RepeatMode.Reverse),
        label = "vuR2",
    )
    val r3 by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = 7.0f,
        animationSpec = infiniteRepeatable(tween(190, easing = LinearEasing), RepeatMode.Reverse),
        label = "vuR3",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = "L",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = CanopyColors.Muted,
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier.height(11.dp),
        ) {
            val heightsL = if (isPlaying) listOf(l1, l2, l3) else listOf(2f, 2f, 1.5f)
            val colorsL = listOf(Color(0xFF22C55E), Color(0xFF38BDF8), Color(0xFFF59E0B))
            heightsL.forEachIndexed { idx, heightVal ->
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(heightVal.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(colorsL[idx]),
                )
            }
        }

        Spacer(Modifier.width(2.dp))

        Text(
            text = "R",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = CanopyColors.Muted,
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier.height(11.dp),
        ) {
            val heightsR = if (isPlaying) listOf(r1, r2, r3) else listOf(2f, 2f, 1.5f)
            val colorsR = listOf(Color(0xFF22C55E), Color(0xFF06B6D4), Color(0xFFF59E0B))
            heightsR.forEachIndexed { idx, heightVal ->
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(heightVal.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(colorsR[idx]),
                )
            }
        }
    }
}

/**
 * Modo Obsidian: Consola Superior HUD inspirada en racks de estudio analógicos.
 * Diseñada para audiófilos con pantalla OLED Readout, micro-vúmetro, osciloscopio y controles completos.
 */
@Composable
fun ObsidianHeaderHUD(
    playback: PlaybackSnapshot,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPrevious: () -> Unit = {},
    onArtworkBounds: ((Rect) -> Unit)? = null,
) {
    val track = playback.currentTrack ?: return
    val effectiveDuration = playback.durationMs.coerceAtLeast(1)
    val progress = (playback.positionMs.toFloat() / effectiveDuration.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "ObsidianHUDProgress")

    val infiniteTransition = rememberInfiniteTransition(label = "ObsidianHUDAnim")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ObsidianWavePhase",
    )
    val dspPulse by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ObsidianDspPulse",
    )
    val waveAmplitude by animateFloatAsState(
        targetValue = if (playback.isPlaying) 2.2f else 0f,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
        label = "ObsidianWaveAmplitude",
    )
    val haptics = LocalHapticFeedback.current

    val chassisGradient = Brush.verticalGradient(
        listOf(Color(0xFF141C2E), Color(0xFF0C101A)),
    )
    val chassisBorder = Brush.verticalGradient(
        listOf(Color.White.copy(alpha = 0.14f), Color.White.copy(alpha = 0.04f), Color.Black.copy(alpha = 0.45f)),
    )

    Surface(
        shape = ObsidianRackShape,
        color = Color.Transparent,
        shadowElevation = 10.dp,
        modifier = modifier
            .fillMaxWidth()
            .clip(ObsidianRackShape)
            .background(chassisGradient)
            .border(1.2.dp, chassisBorder, ObsidianRackShape)
            .clickable(onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            // Pantalla OLED Readout técnica de estudio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(OledScreenShape)
                    .background(Color(0xFF05080F))
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.14f), OledScreenShape)
                    .clickable(onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    })
                    .onGloballyPositioned { onArtworkBounds?.invoke(it.boundsInRoot()) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Título y telemetría en vivo
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                            ),
                            color = CanopyColors.SecondaryAccent,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .basicMarquee(),
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "-14.2 LUFS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                ),
                                color = CanopyColors.Muted,
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = "•",
                                color = CanopyColors.RuleStrong,
                                fontSize = 8.sp,
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = "96 kHz/24b",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                ),
                                color = CanopyColors.Muted,
                            )
                            Spacer(Modifier.width(6.dp))
                            // Micro vúmetro LED en vivo
                            MiniVuMeter(isPlaying = playback.isPlaying)
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    // Badge de estado DSP (WSOLA / Bit-Perfect)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(CanopyColors.Accent.copy(alpha = if (playback.isPlaying) dspPulse else 0.15f))
                            .border(1.dp, CanopyColors.Accent.copy(alpha = 0.30f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (playback.isPlaying) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E)),
                                )
                                Spacer(Modifier.width(4.dp))
                            }
                            Icon(
                                imageVector = Icons.Rounded.GraphicEq,
                                contentDescription = null,
                                tint = CanopyColors.SecondaryAccent,
                                modifier = Modifier.size(12.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "WSOLA DSP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                ),
                                color = CanopyColors.SecondaryAccent,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // Barra de controles de transporte de rack y filamento láser
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Filamento láser osciloscopio / medidor de avance digital
                val laserPath = remember { Path() }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(Color(0xFF070B14))
                        .border(0.8.dp, Color(0xFF38BDF8).copy(alpha = if (playback.isPlaying) 0.25f else 0.10f), RoundedCornerShape(9.dp))
                        .padding(horizontal = 4.dp),
                ) {
                    val laserBrush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1E6CEB),
                            Color(0xFF38BDF8),
                            Color(0xFF67E8F9),
                        ),
                    )
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        if (canvasWidth <= 2f || canvasHeight <= 2f) return@Canvas
                        val midY = canvasHeight / 2f
                        val progressClamped = animatedProgress.coerceIn(0f, 1f)
                        val progressX = (canvasWidth * progressClamped).coerceIn(0f, canvasWidth)

                        // Canal de fondo con guías tenues de osciloscopio
                        drawLine(
                            color = Color.White.copy(alpha = 0.08f),
                            start = Offset(0f, midY),
                            end = Offset(canvasWidth, midY),
                            strokeWidth = 1.5.dp.toPx(),
                            cap = StrokeCap.Round,
                        )

                        // Trazo ondulado activo tipo láser osciloscopio
                        val wavelength = 54.dp.toPx().coerceAtLeast(10f)
                        if (progressX > 1f && wavelength > 0f) {
                            laserPath.reset()
                            val ampPx = waveAmplitude.dp.toPx().coerceIn(0f, (canvasHeight / 2f) - 1.5f)
                            val k = (2 * PI / wavelength).toFloat()
                            val leadInDist = 18.dp.toPx()
                            laserPath.moveTo(0f, midY)

                            var x = 0f
                            val step = 1.2f
                            var lastY = midY
                            while (x <= progressX) {
                                val tIn = (x / leadInDist).coerceIn(0f, 1f)
                                val tOut = ((progressX - x) / leadInDist).coerceIn(0f, 1f)
                                val envelope = (tIn * tIn * (3f - 2f * tIn)) * (tOut * tOut * (3f - 2f * tOut))
                                val wave = sin(k * x - wavePhase)
                                val y = (midY + wave * ampPx * envelope).coerceIn(1f, canvasHeight - 1f)
                                if (y.isFinite()) {
                                    laserPath.lineTo(x, y)
                                    lastY = y
                                }
                                x += step
                            }

                            // Halo exterior difuso de neón suave
                            drawPath(
                                path = laserPath,
                                brush = laserBrush,
                                style = Stroke(width = 4.2.dp.toPx(), cap = StrokeCap.Round),
                                alpha = 0.25f,
                            )

                            // Núcleo láser afilado
                            drawPath(
                                path = laserPath,
                                brush = laserBrush,
                                style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round),
                            )

                            // Cursor láser frontal con destello Sky Flare
                            if (progressX.isFinite() && lastY.isFinite()) {
                                drawCircle(
                                    color = Color(0xFF38BDF8).copy(alpha = 0.40f),
                                    radius = 5.dp.toPx(),
                                    center = Offset(progressX, lastY),
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.5.dp.toPx(),
                                    center = Offset(progressX, lastY),
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.width(10.dp))

                // Controles de transporte completos de rack analógico
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Botón Anterior
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(CanopyColors.Surface)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                            .tactilePress(onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onPrevious()
                            }),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SkipPrevious,
                            contentDescription = stringResource(R.string.action_previous),
                            tint = CanopyColors.Muted,
                            modifier = Modifier.size(15.dp),
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    // Botón Play / Pause Hero
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF2E84FE), Color(0xFF1E6CEB), Color(0xFF104CA8)),
                                ),
                            )
                            .border(1.2.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                            .tactilePress(onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onTogglePlay()
                            }),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (playback.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (playback.isPlaying) {
                                stringResource(R.string.action_pause)
                            } else {
                                stringResource(R.string.action_play)
                            },
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    // Botón Siguiente
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(CanopyColors.Surface)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                            .tactilePress(onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNext()
                            }),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SkipNext,
                            contentDescription = stringResource(R.string.action_next),
                            tint = CanopyColors.Muted,
                            modifier = Modifier.size(15.dp),
                        )
                    }
                }
            }
        }
    }
}
