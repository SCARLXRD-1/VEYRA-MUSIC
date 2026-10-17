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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeMute
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.audio.rememberSystemVolume
import kotlin.math.roundToInt

/** Thickness of the volume track line. */
private val TRACK_HEIGHT = 5.dp

/**
 * Deslizador interactivo de volumen del dispositivo, enlazado en tiempo real con el volumen
 * de medios del sistema Android, o que controla el volumen de un dispositivo Veyra Connect
 * remoto cuando el destino es reproducción remota.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceVolumeSlider(
    enabled: Boolean,
    isRemote: Boolean = false,
    remoteVolume: Float = 1f,
    onRemoteVolumeChange: (Float) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val systemVolume = rememberSystemVolume()
    var draggingVolume by remember { mutableStateOf<Float?>(null) }
    val displayVolume = draggingVolume ?: if (isRemote) remoteVolume else systemVolume.volume.toFloat()
    val tint = Color.White.copy(alpha = if (enabled) 0.70f else 0.25f)
    val minVal = if (isRemote) 0f else systemVolume.minVolume.toFloat()
    val maxVal = if (isRemote) 1f else systemVolume.maxVolume.toFloat()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable(enabled = enabled) {
                    if (isRemote) onRemoteVolumeChange(0f) else systemVolume.mute()
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.VolumeMute,
                contentDescription = stringResource(R.string.playerx_mute_cd),
                tint = tint,
                modifier = Modifier.size(18.dp),
            )
        }
        Slider(
            value = displayVolume.coerceIn(minVal, maxVal),
            onValueChange = { next ->
                draggingVolume = next
                if (isRemote) {
                    onRemoteVolumeChange(next.coerceIn(0f, 1f))
                } else {
                    systemVolume.setVolume(next.roundToInt())
                }
            },
            onValueChangeFinished = {
                draggingVolume = null
            },
            valueRange = minVal..maxVal,
            enabled = enabled,
            // The fill edge is the position indicator, so no separate thumb is drawn.
            // Dragging anywhere along the track still works.
            thumb = { Box(Modifier.size(0.dp)) },
            track = { sliderState ->
                val rangeSpan = (sliderState.valueRange.endInclusive - sliderState.valueRange.start).coerceAtLeast(1f)
                val fraction = ((sliderState.value - sliderState.valueRange.start) / rangeSpan).coerceIn(0f, 1f)
                // Canal empotrado neumórfico con degradado interior
                val grooveShape = RoundedCornerShape(percent = 50)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(grooveShape)
                        .background(Color(0xFF090D18))
                        .border(
                            1.dp,
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.75f), Color.White.copy(alpha = 0.08f)),
                            ),
                            grooveShape,
                        ),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (fraction > 0.001f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .fillMaxHeight()
                                .clip(grooveShape)
                                .background(
                                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF1E6CEB).copy(alpha = if (enabled) 0.90f else 0.40f),
                                            Color(0xFF38BDF8).copy(alpha = if (enabled) 1.0f else 0.45f),
                                        ),
                                    ),
                                ),
                        )
                    }
                }
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White.copy(alpha = 0.85f),
                inactiveTrackColor = Color.White.copy(alpha = 0.20f),
            ),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
                .height(24.dp),
        )
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable(enabled = enabled) {
                    if (isRemote) onRemoteVolumeChange((remoteVolume + 0.1f).coerceAtMost(1f))
                    else systemVolume.stepUp()
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = stringResource(R.string.playerx_volume_up_cd),
                tint = tint,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
