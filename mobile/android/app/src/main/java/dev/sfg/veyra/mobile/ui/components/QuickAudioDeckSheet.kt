/*
 * Copyright (C) 2026 SFG545
 * Copyright (C) 2026 AKASHI DEV
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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.EQ_PRESETS
import dev.sfg.veyra.mobile.model.OrchardSettings
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent

/**
 * Hoja modal DSP Audio Deck que reproduce fielmente QuickAudioDeck.vue de desktop.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAudioDeckSheet(
    open: Boolean,
    onDismiss: () -> Unit,
    settings: OrchardSettings,
    onSettings: (OrchardSettings) -> Unit,
    onOpenEqualizerStudio: () -> Unit,
) {
    if (!open) return

    val eqConfig = settings.equalizerConfig
    val isSmartActive = settings.crossfadeEnabled && settings.smartCrossfade
    val isEqActive = eqConfig.enabled

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CanopyColors.Surface,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
        ) {
            // Cabecera con título e indicador de estado.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = null,
                        tint = LocalAccent.current,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.dsp_deck_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = CanopyColors.Text,
                    )
                }

                val statusText = when {
                    isSmartActive -> stringResource(R.string.dsp_deck_wsola_active)
                    isEqActive -> stringResource(R.string.dsp_deck_eq_active)
                    else -> stringResource(R.string.dsp_deck_direct_active)
                }
                StatusPillBadge(
                    label = statusText,
                    status = if (isSmartActive || isEqActive) StatusPillTone.ACTIVE else StatusPillTone.MUTED,
                    pulse = isSmartActive || isEqActive,
                )
            }

            Spacer(Modifier.height(18.dp))

            // Modo Crossfade.
            Text(
                text = stringResource(R.string.dsp_deck_crossfade_mode),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = CanopyColors.Muted,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                QuickDeckChip(
                    text = stringResource(R.string.dsp_mode_smart),
                    selected = settings.crossfadeEnabled && settings.smartCrossfade,
                    onClick = {
                        onSettings(settings.copy(crossfadeEnabled = true, smartCrossfade = true))
                    },
                    modifier = Modifier.weight(1f),
                )
                QuickDeckChip(
                    text = stringResource(R.string.dsp_mode_standard),
                    selected = settings.crossfadeEnabled && !settings.smartCrossfade,
                    onClick = {
                        onSettings(settings.copy(crossfadeEnabled = true, smartCrossfade = false))
                    },
                    modifier = Modifier.weight(1f),
                )
                QuickDeckChip(
                    text = stringResource(R.string.dsp_mode_direct),
                    selected = !settings.crossfadeEnabled,
                    onClick = {
                        onSettings(settings.copy(crossfadeEnabled = false))
                    },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(20.dp))

            // Ecualizador 10-Band y presets.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.dsp_deck_eq_title),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = CanopyColors.Muted,
                )
                val haptics = LocalHapticFeedback.current
                Box(
                    modifier = Modifier.clickable {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSettings(settings.copy(equalizerConfig = eqConfig.copy(enabled = !isEqActive)))
                    }
                ) {
                    StatusPillBadge(
                        label = if (isEqActive) stringResource(R.string.dsp_deck_eq_active_badge) else stringResource(R.string.dsp_deck_eq_bypass_badge),
                        status = if (isEqActive) StatusPillTone.PRIMARY else StatusPillTone.MUTED,
                        pulse = isEqActive,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Top 5 presets de audio equivalentes a desktop.
                EQ_PRESETS.take(5).forEach { preset ->
                    val isSelected = isEqActive && eqConfig.presetId == preset.id
                    QuickDeckChip(
                        text = preset.label,
                        selected = isSelected,
                        onClick = {
                            onSettings(
                                settings.copy(
                                    equalizerConfig = eqConfig.copy(
                                        enabled = true,
                                        presetId = preset.id,
                                        gains = preset.gains,
                                    )
                                )
                            )
                        },
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Control rotatorio analógico de ganancia Preamp.
            Text(
                text = stringResource(R.string.dsp_deck_preamp_gain),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = CanopyColors.Muted,
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                NeumorphicKnob(
                    value = eqConfig.preampDb,
                    onValueChange = { newVal ->
                        onSettings(
                            settings.copy(
                                equalizerConfig = eqConfig.copy(
                                    preampDb = newVal,
                                    presetId = if (eqConfig.presetId == "flat") "custom" else eqConfig.presetId,
                                )
                            )
                        )
                    },
                    min = -12f,
                    max = 6f,
                    step = 0.5f,
                    defaultValue = 0f,
                    unit = "dB",
                    size = 56.dp,
                )

                Text(
                    text = stringResource(R.string.dsp_deck_preamp_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = CanopyColors.Muted,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                )
            }

            Spacer(Modifier.height(22.dp))

            // Botón hacia el estudio completo de ecualización.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, CanopyColors.Rule, RoundedCornerShape(12.dp))
                    .clickable {
                        onDismiss()
                        onOpenEqualizerStudio()
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Equalizer,
                        contentDescription = null,
                        tint = LocalAccent.current,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.dsp_deck_open_studio),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = CanopyColors.Text,
                    )
                }
            }
        }
    }
}

/** Chip squircle táctil interactivo. */
@Composable
private fun QuickDeckChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) LocalAccent.current.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.04f))
            .border(
                1.dp,
                if (selected) LocalAccent.current.copy(alpha = 0.5f) else CanopyColors.Rule,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            })
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else CanopyColors.Muted,
            maxLines = 1,
        )
    }
}
