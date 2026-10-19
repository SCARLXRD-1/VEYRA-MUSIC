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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Gradient
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.MultipleStop
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.sfg.veyra.mobile.auth.AuthState
import dev.sfg.veyra.mobile.i18n.AppLanguage
import dev.sfg.veyra.mobile.i18n.AppLocale
import dev.sfg.veyra.mobile.model.AudioQuality
import dev.sfg.veyra.mobile.model.OrchardSettings
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.ui.tv.LocalIsTv
import dev.sfg.veyra.mobile.ui.tv.tvFocusable
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import dev.sfg.veyra.connect.R
import kotlin.math.roundToInt

@Composable
internal fun WelcomeHeroHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F1626))
                .border(1.5.dp, LocalAccent.current.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.veyra_logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape),
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
            ),
            color = CanopyColors.Text,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        dev.sfg.veyra.mobile.ui.components.StatusPillBadge(
            label = "STUDIO",
            status = dev.sfg.veyra.mobile.ui.components.StatusPillTone.ACTIVE,
            pulse = true,
            size = dev.sfg.veyra.mobile.ui.components.StatusPillSize.SM,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = CanopyColors.Muted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

@Composable
internal fun WelcomeLanguageSection(initialFocusRequester: FocusRequester? = null) {
    val context = LocalContext.current
    var language by remember { mutableStateOf(AppLocale.current(context)) }

    WelcomeSectionCard(
        title = stringResource(R.string.settings_section_language),
        subtitle = stringResource(R.string.settings_language_subtitle),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(CanopyColors.Canvas, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            WelcomeOptionChip(
                title = stringResource(R.string.language_system),
                subtitle = stringResource(R.string.language_system_desc),
                icon = Icons.Rounded.Language,
                selected = language == AppLanguage.SYSTEM,
                modifier = Modifier.weight(1f),
                focusRequester = initialFocusRequester,
                onClick = {
                    language = AppLanguage.SYSTEM
                    AppLocale.apply(context, AppLanguage.SYSTEM)
                },
            )
            WelcomeOptionChip(
                title = stringResource(R.string.language_english),
                subtitle = stringResource(R.string.language_english_desc),
                icon = Icons.Rounded.Translate,
                selected = language == AppLanguage.ENGLISH,
                modifier = Modifier.weight(1f),
                onClick = {
                    language = AppLanguage.ENGLISH
                    AppLocale.apply(context, AppLanguage.ENGLISH)
                },
            )
            WelcomeOptionChip(
                title = stringResource(R.string.language_spanish),
                subtitle = stringResource(R.string.language_spanish_desc),
                icon = Icons.Rounded.Translate,
                selected = language == AppLanguage.SPANISH,
                modifier = Modifier.weight(1f),
                onClick = {
                    language = AppLanguage.SPANISH
                    AppLocale.apply(context, AppLanguage.SPANISH)
                },
            )
        }
    }
}

@Composable
internal fun WelcomeAuthSection(
    auth: AuthState,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
) {
    WelcomeSectionCard(
        title = stringResource(R.string.welcome_section_auth_title),
        subtitle = stringResource(R.string.welcome_section_auth_subtitle),
    ) {
        val isSignedIn = auth is AuthState.SignedIn
        val avatarUrl = (auth as? AuthState.SignedIn)?.avatarUrl.orEmpty()
        val displayName = (auth as? AuthState.SignedIn)?.displayName.orEmpty().ifBlank { "YouTube Music" }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(LocalAccent.current.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                if (avatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        if (isSignedIn) Icons.Rounded.CheckCircle else Icons.Rounded.Person,
                        contentDescription = null,
                        tint = LocalAccent.current,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isSignedIn) displayName else stringResource(R.string.welcome_auth_not_connected),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CanopyColors.Text,
                )
                Text(
                    text = if (isSignedIn) stringResource(R.string.welcome_auth_signed_in) else stringResource(R.string.welcome_auth_guest),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSignedIn) LocalAccent.current else CanopyColors.Muted,
                )
            }
        }

        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            if (isSignedIn) {
                OutlinedButton(
                    onClick = onSignOut,
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .tvFocusable(
                            shape = CircleShape,
                            scale = 1.035f,
                            onClick = onSignOut,
                        ),
                ) {
                    Text(stringResource(R.string.welcome_auth_sign_out), color = CanopyColors.Danger)
                }
            } else {
                Button(
                    onClick = onSignIn,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LocalAccent.current,
                        contentColor = Color.Black,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .tvFocusable(
                            shape = CircleShape,
                            scale = 1.035f,
                            onClick = onSignIn,
                        ),
                ) {
                    Text(stringResource(R.string.welcome_auth_sign_in), fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
internal fun WelcomeSessionQrSection(
    qrPayload: String,
    status: String,
    onStart: () -> Unit,
    onCancel: () -> Unit,
) {
    WelcomeSectionCard(
        title = "Conectar con el móvil",
        subtitle = "Escanea el QR desde Veyra Music en tu móvil. Ambos dispositivos deben estar en la misma red.",
    ) {
        if (qrPayload.isBlank()) {
            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
                    .tvFocusable(shape = CircleShape, scale = 1.03f, onClick = onStart),
                shape = CircleShape,
            ) { Text("Mostrar QR de sesión") }
        } else {
            val bitmap = remember(qrPayload) {
                val matrix = QRCodeWriter().encode(qrPayload, BarcodeFormat.QR_CODE, 420, 420)
                Bitmap.createBitmap(420, 420, Bitmap.Config.ARGB_8888).also { image ->
                    for (x in 0 until 420) for (y in 0 until 420) {
                        image.setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                    }
                }
            }
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Código QR para vincular la sesión del móvil",
                    modifier = Modifier.padding(12.dp).size(210.dp).clip(RoundedCornerShape(12.dp)),
                )
            }
            Text("El código caduca en 3 minutos y solo puede usarse una vez.", modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), color = CanopyColors.Muted, textAlign = TextAlign.Center)
            Text(
                "La sesión se copia a la TV y permanece abierta en el móvil.",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                color = CanopyColors.Muted,
                textAlign = TextAlign.Center,
            )
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth().padding(16.dp).tvFocusable(shape = CircleShape, scale = 1.03f, onClick = onCancel),
                shape = CircleShape,
            ) { Text("Cancelar") }
        }
        if (status.isNotBlank()) {
            Text(status, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), color = CanopyColors.Muted, textAlign = TextAlign.Center)
        }
    }
}

@Composable
internal fun WelcomeVisualsSection(
    settings: OrchardSettings,
    onUpdateSettings: (OrchardSettings) -> Unit,
) {
    WelcomeSectionCard(
        title = stringResource(R.string.welcome_section_visuals_title),
        subtitle = stringResource(R.string.welcome_section_visuals_subtitle),
    ) {
        WelcomeToggleRow(
            icon = Icons.Rounded.Gradient,
            title = stringResource(R.string.welcome_visuals_animated_bg_title),
            subtitle = stringResource(R.string.welcome_visuals_animated_bg_subtitle),
            checked = settings.animatedBackground,
            onChecked = { onUpdateSettings(settings.copy(animatedBackground = it)) },
        )
        WelcomeDivider()
        WelcomeToggleRow(
            icon = Icons.Rounded.Image,
            title = stringResource(R.string.welcome_visuals_animated_artwork_title),
            subtitle = stringResource(R.string.welcome_visuals_animated_artwork_subtitle),
            checked = settings.animatedArtwork,
            onChecked = { onUpdateSettings(settings.copy(animatedArtwork = it)) },
        )
        WelcomeDivider()
        WelcomeToggleRow(
            icon = Icons.Rounded.Palette,
            title = stringResource(R.string.welcome_visuals_system_colors_title),
            subtitle = stringResource(R.string.welcome_visuals_system_colors_subtitle),
            checked = settings.useSystemColors,
            onChecked = { onUpdateSettings(settings.copy(useSystemColors = it)) },
        )
    }
}

@Composable
internal fun WelcomeCrossfadeSection(
    settings: OrchardSettings,
    onUpdateSettings: (OrchardSettings) -> Unit,
) {
    WelcomeSectionCard(
        title = stringResource(R.string.welcome_section_crossfade_title),
        subtitle = stringResource(R.string.welcome_section_crossfade_subtitle),
    ) {
        WelcomeToggleRow(
            icon = Icons.Rounded.MultipleStop,
            title = stringResource(R.string.welcome_crossfade_title),
            subtitle = if (settings.crossfadeEnabled) {
                stringResource(R.string.welcome_crossfade_enabled_subtitle, settings.crossfadeSeconds)
            } else {
                stringResource(R.string.welcome_crossfade_disabled_subtitle)
            },
            checked = settings.crossfadeEnabled,
            onChecked = { onUpdateSettings(settings.copy(crossfadeEnabled = it)) },
        )

        AnimatedVisibility(visible = settings.crossfadeEnabled) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(R.string.welcome_crossfade_duration),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = CanopyColors.Text,
                    )
                    Text(
                        stringResource(R.string.welcome_crossfade_seconds, settings.crossfadeSeconds),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = LocalAccent.current,
                    )
                }

                if (LocalIsTv.current) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        maxItemsInEachRow = 6,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        (OrchardSettings.MIN_CROSSFADE_SECONDS..OrchardSettings.MAX_CROSSFADE_SECONDS)
                            .forEach { seconds ->
                                val selected = settings.crossfadeSeconds == seconds
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .background(
                                            if (selected) LocalAccent.current.copy(alpha = 0.22f) else CanopyColors.Canvas,
                                            RoundedCornerShape(12.dp),
                                        )
                                        .tvFocusable(
                                            shape = RoundedCornerShape(12.dp),
                                            scale = 1.04f,
                                            onClick = {
                                                onUpdateSettings(settings.copy(crossfadeSeconds = seconds))
                                            },
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "${seconds}s",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (selected) LocalAccent.current else CanopyColors.Text,
                                    )
                                }
                            }
                    }
                } else {
                    Slider(
                        value = settings.crossfadeSeconds.toFloat(),
                        onValueChange = { onUpdateSettings(settings.copy(crossfadeSeconds = it.roundToInt())) },
                        valueRange = OrchardSettings.MIN_CROSSFADE_SECONDS.toFloat()..OrchardSettings.MAX_CROSSFADE_SECONDS.toFloat(),
                        steps = OrchardSettings.MAX_CROSSFADE_SECONDS - OrchardSettings.MIN_CROSSFADE_SECONDS - 1,
                        colors = SliderDefaults.colors(
                            thumbColor = LocalAccent.current,
                            activeTrackColor = LocalAccent.current,
                            inactiveTrackColor = CanopyColors.Canvas,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    stringResource(R.string.welcome_crossfade_type),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = CanopyColors.Text,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CanopyColors.Canvas, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    val smart = settings.smartCrossfade
                    WelcomeOptionChip(
                        title = stringResource(R.string.welcome_crossfade_smart),
                        subtitle = stringResource(R.string.welcome_crossfade_smart_subtitle),
                        icon = Icons.Rounded.AutoAwesome,
                        selected = smart,
                        modifier = Modifier.weight(1f),
                        onClick = { onUpdateSettings(settings.copy(smartCrossfade = true)) },
                    )
                    WelcomeOptionChip(
                        title = stringResource(R.string.welcome_crossfade_standard),
                        subtitle = stringResource(R.string.welcome_crossfade_standard_subtitle),
                        icon = Icons.Rounded.GraphicEq,
                        selected = !smart,
                        modifier = Modifier.weight(1f),
                        onClick = { onUpdateSettings(settings.copy(smartCrossfade = false)) },
                    )
                }
            }
        }
    }
}

@Composable
internal fun WelcomeQualityAndStorageSection(
    settings: OrchardSettings,
    onUpdateSettings: (OrchardSettings) -> Unit,
) {
    WelcomeSectionCard(
        title = stringResource(R.string.welcome_section_quality_title),
        subtitle = stringResource(R.string.welcome_section_quality_subtitle),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RowIcon(Icons.Rounded.GraphicEq)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        stringResource(R.string.welcome_quality_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = CanopyColors.Text,
                    )
                    val qualityName = when (settings.audioQuality) {
                        AudioQuality.DATA_SAVER -> stringResource(R.string.welcome_quality_saver)
                        AudioQuality.NORMAL -> stringResource(R.string.welcome_quality_normal)
                        AudioQuality.HIGH -> stringResource(R.string.welcome_quality_high)
                        AudioQuality.MAX -> stringResource(R.string.welcome_quality_max)
                    }
                    Text(
                        qualityName,
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalAccent.current,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (LocalIsTv.current) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 2,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AudioQuality.entries.forEach { quality ->
                        val selected = quality == settings.audioQuality
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(
                                    if (selected) LocalAccent.current.copy(alpha = 0.22f) else CanopyColors.Canvas,
                                    RoundedCornerShape(14.dp),
                                )
                                .tvFocusable(
                                    shape = RoundedCornerShape(14.dp),
                                    scale = 1.04f,
                                    onClick = { onUpdateSettings(settings.copy(audioQuality = quality)) },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = when (quality) {
                                    AudioQuality.DATA_SAVER -> stringResource(R.string.welcome_quality_saver)
                                    AudioQuality.NORMAL -> stringResource(R.string.welcome_quality_normal)
                                    AudioQuality.HIGH -> stringResource(R.string.welcome_quality_high)
                                    AudioQuality.MAX -> stringResource(R.string.welcome_quality_max)
                                },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) LocalAccent.current else CanopyColors.Text,
                            )
                        }
                    }
                }
            } else Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CanopyColors.Canvas, CircleShape)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                AudioQuality.entries.forEach { quality ->
                    val isSelected = quality == settings.audioQuality
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .background(
                                if (isSelected) LocalAccent.current else Color.Transparent,
                                CircleShape,
                            )
                            .tvFocusable(
                                shape = CircleShape,
                                scale = 1.04f,
                                onClick = { onUpdateSettings(settings.copy(audioQuality = quality)) },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = when (quality) {
                                AudioQuality.DATA_SAVER -> stringResource(R.string.welcome_quality_saver)
                                AudioQuality.NORMAL -> stringResource(R.string.welcome_quality_normal)
                                AudioQuality.HIGH -> stringResource(R.string.welcome_quality_high)
                                AudioQuality.MAX -> stringResource(R.string.welcome_quality_max)
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else CanopyColors.Muted,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            WelcomeDivider()
            Spacer(Modifier.height(12.dp))

            val steps = OrchardSettings.CACHE_SIZE_STEPS_MB
            val index = steps.indexOfFirst { it >= settings.cacheSizeMb }.takeIf { it >= 0 } ?: steps.lastIndex

            Row(verticalAlignment = Alignment.CenterVertically) {
                RowIcon(Icons.Rounded.Storage)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.welcome_cache_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = CanopyColors.Text,
                    )
                    Text(
                        stringResource(R.string.welcome_cache_desc, formatCacheSize(steps[index])),
                        style = MaterialTheme.typography.bodySmall,
                        color = CanopyColors.Muted,
                    )
                }
                Text(
                    formatCacheSize(steps[index]),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = LocalAccent.current,
                )
            }

            if (LocalIsTv.current) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 3,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    steps.forEach { cacheSize ->
                        val selected = steps[index] == cacheSize
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .background(
                                    if (selected) LocalAccent.current.copy(alpha = 0.22f) else CanopyColors.Canvas,
                                    RoundedCornerShape(12.dp),
                                )
                                .tvFocusable(
                                    shape = RoundedCornerShape(12.dp),
                                    scale = 1.04f,
                                    onClick = { onUpdateSettings(settings.copy(cacheSizeMb = cacheSize)) },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = formatCacheSize(cacheSize),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) LocalAccent.current else CanopyColors.Text,
                            )
                        }
                    }
                }
            } else {
                Slider(
                    value = index.toFloat(),
                    onValueChange = { onUpdateSettings(settings.copy(cacheSizeMb = steps[it.roundToInt()])) },
                    valueRange = 0f..steps.lastIndex.toFloat(),
                    steps = steps.size - 2,
                    colors = SliderDefaults.colors(
                        thumbColor = LocalAccent.current,
                        activeTrackColor = LocalAccent.current,
                        inactiveTrackColor = CanopyColors.Canvas,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
