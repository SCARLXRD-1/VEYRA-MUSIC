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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.OrchardSettings
import dev.sfg.veyra.mobile.settings.CacheManager
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import kotlin.math.roundToInt

/** Renders a megabyte count the way a listener thinks about storage. */
internal fun formatCacheSize(megabytes: Int): String =
    if (megabytes >= 1024) {
        val gigabytes = megabytes / 1024f
        if (gigabytes == gigabytes.toInt().toFloat()) "${gigabytes.toInt()} GB" else "%.1f GB".format(gigabytes)
    } else {
        "$megabytes MB"
    }

/**
 * Límite superior de la caché de streams en disco.
 *
 * Veyra Music guarda pistas completas en lugar de solo lo que va por delante del cabezal de
 * reproducción, así que vale la pena exponerlo: decide cuánto de una sesión de escucha
 * sobrevive para reproducirse al instante, y marca la diferencia entre unos pocos álbumes y
 * una biblioteca.
 */
@Composable
internal fun CacheSizeRow(settings: OrchardSettings, onSettings: (OrchardSettings) -> Unit) {
    val steps = OrchardSettings.CACHE_SIZE_STEPS_MB
    // The slider moves between stops rather than over megabytes, so the value is always a round
    // size and the control has somewhere obvious to land.
    val index = steps.indexOfFirst { it >= settings.cacheSizeMb }.takeIf { it >= 0 } ?: steps.lastIndex

    Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Column(Modifier.weight(1f).padding(end = 14.dp)) {
                Text(
                    stringResource(R.string.settings_cache_size),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = CanopyColors.Text,
                )
                Text(
                    stringResource(R.string.cache_keep_limit_format, formatCacheSize(steps[index])),
                    color = CanopyColors.Muted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Text(
                formatCacheSize(steps[index]),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = LocalAccent.current,
            )
        }
        Spacer(Modifier.height(6.dp))
        Slider(
            value = index.toFloat(),
            onValueChange = { onSettings(settings.copy(cacheSizeMb = steps[it.roundToInt()])) },
            valueRange = 0f..steps.lastIndex.toFloat(),
            steps = steps.size - 2,
            colors = SliderDefaults.colors(
                thumbColor = LocalAccent.current,
                activeTrackColor = LocalAccent.current,
                inactiveTrackColor = CanopyColors.Canvas,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Row(Modifier.fillMaxWidth()) {
            Text(
                formatCacheSize(steps.first()),
                color = CanopyColors.Eyebrow,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                formatCacheSize(steps.last()),
                color = CanopyColors.Eyebrow,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Text(
            stringResource(R.string.cache_limit_hint),
            color = CanopyColors.Eyebrow,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

/**
 * Row displaying current cache size with an action to clear all temporary caches.
 * Prompts with an alert dialog before wiping stream cache, Coil image cache, and temporary files.
 */
@Composable
internal fun ClearCacheRow(
    cacheSizeBytes: Long,
    isClearing: Boolean,
    onClear: () -> Unit,
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    val formattedSize = remember(cacheSizeBytes) { CacheManager.formatStorageSize(cacheSizeBytes) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isClearing) { showConfirmDialog = true }
            .defaultMinSize(minHeight = 64.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
        ) {
            Text(
                stringResource(R.string.settings_clear_cache),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                color = CanopyColors.Text,
            )
            Text(
                stringResource(R.string.cache_row_subtitle_format, formattedSize),
                color = CanopyColors.Muted,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if (isClearing) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = LocalAccent.current,
            )
        } else {
            Surface(
                onClick = { showConfirmDialog = true },
                shape = RoundedCornerShape(16.dp),
                color = if (cacheSizeBytes > 0) CanopyColors.Danger.copy(alpha = 0.14f) else CanopyColors.Canvas,
                contentColor = if (cacheSizeBytes > 0) CanopyColors.Danger else CanopyColors.Muted,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        stringResource(R.string.cache_clear_action),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    stringResource(R.string.cache_clear_confirm_title),
                    color = CanopyColors.Text,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        stringResource(R.string.cache_clear_confirm_body_format, formattedSize),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CanopyColors.Text,
                    )
                    Text(
                        stringResource(R.string.cache_clear_confirm_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = CanopyColors.Muted,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        onClear()
                    },
                ) {
                    Text(
                        stringResource(R.string.cache_clear_action),
                        color = CanopyColors.Danger,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(stringResource(R.string.action_cancel), color = CanopyColors.Muted)
                }
            },
            containerColor = CanopyColors.Surface,
            shape = RoundedCornerShape(16.dp),
        )
    }
}
