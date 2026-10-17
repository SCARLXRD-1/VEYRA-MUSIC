package dev.sfg.veyra.mobile.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.EQ_BANDS
import dev.sfg.veyra.mobile.model.EQ_PRESETS
import dev.sfg.veyra.mobile.model.EqualizerConfig
import dev.sfg.veyra.mobile.model.OrchardSettings
import dev.sfg.veyra.mobile.ui.scroll.orchardVerticalScroll as verticalScroll
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import kotlin.math.roundToInt

@Composable
private fun presetLabel(presetId: String): String {
    val resId = when (presetId) {
        "flat" -> R.string.eq_preset_flat
        "bass_boost" -> R.string.eq_preset_bass_boost
        "electronic" -> R.string.eq_preset_electronic
        "rock" -> R.string.eq_preset_rock
        "vocal" -> R.string.eq_preset_vocal
        "acoustic" -> R.string.eq_preset_acoustic
        "bright" -> R.string.eq_preset_bright
        "hiphop" -> R.string.eq_preset_hiphop
        "pop" -> R.string.eq_preset_pop
        "classical" -> R.string.eq_preset_classical
        "rnb" -> R.string.eq_preset_rnb
        else -> null
    }
    return if (resId != null) stringResource(resId) else (EQ_PRESETS.find { it.id == presetId }?.label ?: stringResource(R.string.eq_custom))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EqualizerRow(settings: OrchardSettings, onSettings: (OrchardSettings) -> Unit) {
    var showSheet by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                showSheet = true
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(Modifier.weight(1f).padding(end = 14.dp)) {
            Text(
                stringResource(R.string.eq_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = CanopyColors.Text,
            )
            Text(
                if (settings.equalizerConfig.enabled) {
                    stringResource(R.string.eq_enabled_format, presetLabel(settings.equalizerConfig.presetId))
                } else {
                    stringResource(R.string.eq_disabled)
                },
                color = CanopyColors.Muted,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        dev.sfg.veyra.mobile.ui.components.NeumorphicSwitch(
            checked = settings.equalizerConfig.enabled,
            onCheckedChange = { 
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSettings(settings.copy(
                    equalizerConfig = settings.equalizerConfig.copy(enabled = it)
                )) 
            },
        )
    }

    if (showSheet) {
        EqualizerModalSheet(
            open = showSheet,
            onDismiss = { showSheet = false },
            config = settings.equalizerConfig,
            onConfigChange = { newConfig ->
                onSettings(settings.copy(equalizerConfig = newConfig))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerModalSheet(
    open: Boolean,
    onDismiss: () -> Unit,
    config: EqualizerConfig,
    onConfigChange: (EqualizerConfig) -> Unit,
) {
    if (!open) return
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF131B2B),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        EqualizerContent(
            config = config,
            onConfigChange = onConfigChange,
        )
    }
}

/** Monitor visual de la curva de respuesta paramétrica de 10 bandas de Veyra Music. */
@Composable
private fun EqualizerResponseMonitor(
    config: EqualizerConfig,
    modifier: Modifier = Modifier,
) {
    val monitorShape = RoundedCornerShape(16.dp)
    val cardBrush = remember {
        Brush.verticalGradient(listOf(Color(0xFF0F1624), Color(0xFF090D15)))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(monitorShape)
            .background(cardBrush, monitorShape)
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), monitorShape)
            .padding(top = 10.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF38BDF8))
                )
                Text(
                    text = "PARAMETRIC EQ MONITOR",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp,
                )
            }
            Text(
                text = "${if (config.preampDb > 0) "+" else ""}${config.preampDb.roundToInt()} dB PREAMP",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = Color(0xFF38BDF8),
                letterSpacing = 0.8.sp,
            )
        }

        val eqPath = remember { Path() }
        val eqFillPath = remember { Path() }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(horizontal = 12.dp)
        ) {
            val w = size.width
            val h = size.height
            val padX = 14.dp.toPx()
            val padY = 8.dp.toPx()
            val availW = w - 2 * padX
            val availH = h - 2 * padY
            val baselineY = padY + availH * 0.5f

            // Líneas guía horizontales
            drawLine(
                color = Color.White.copy(alpha = 0.12f),
                start = Offset(padX, baselineY),
                end = Offset(w - padX, baselineY),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = Color.White.copy(alpha = 0.04f),
                start = Offset(padX, baselineY - availH * 0.45f),
                end = Offset(w - padX, baselineY - availH * 0.45f),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = Color.White.copy(alpha = 0.04f),
                start = Offset(padX, baselineY + availH * 0.45f),
                end = Offset(w - padX, baselineY + availH * 0.45f),
                strokeWidth = 1.dp.toPx()
            )

            // Puntos de las 10 bandas
            val points = (0 until 10).map { i ->
                val x = padX + i * (availW / 9f)
                val gain = (config.clampedGains.getOrElse(i) { 0f } + config.clampedPreampDb * 0.35f).coerceIn(-12f, 12f)
                val y = baselineY - (gain / 12f) * (availH * 0.45f)
                Offset(x, y)
            }

            // Líneas verticales de frecuencia
            for (pt in points) {
                drawLine(
                    color = Color.White.copy(alpha = 0.04f),
                    start = Offset(pt.x, padY),
                    end = Offset(pt.x, h - padY),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Spline cúbico continuo
            eqPath.reset()
            eqFillPath.reset()
            eqPath.moveTo(points[0].x, points[0].y)
            eqFillPath.moveTo(points[0].x, baselineY)
            eqFillPath.lineTo(points[0].x, points[0].y)

            for (i in 0 until points.size - 1) {
                val p0 = points.getOrElse(i - 1) { points[i] }
                val p1 = points[i]
                val p2 = points[i + 1]
                val p3 = points.getOrElse(i + 2) { p2 }

                val cp1x = p1.x + (p2.x - p0.x) / 6f
                val cp1y = p1.y + (p2.y - p0.y) / 6f
                val cp2x = p2.x - (p3.x - p1.x) / 6f
                val cp2y = p2.y - (p3.y - p1.y) / 6f

                eqPath.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
                eqFillPath.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
            }
            eqFillPath.lineTo(points.last().x, baselineY)
            eqFillPath.close()

            // Relleno degradado bajo la curva
            drawPath(
                path = eqFillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF38BDF8).copy(alpha = 0.28f),
                        Color(0xFF1E6CEB).copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    startY = padY,
                    endY = baselineY
                )
            )

            // Trazo de la curva de respuesta
            drawPath(
                path = eqPath,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF1E6CEB), Color(0xFF38BDF8))
                ),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Nodos luminosos en cada frecuencia
            for (pt in points) {
                drawCircle(Color(0xFF38BDF8).copy(alpha = 0.35f), radius = 4.5.dp.toPx(), center = pt)
                drawCircle(Color(0xFF38BDF8), radius = 2.8.dp.toPx(), center = pt)
                drawCircle(Color.White, radius = 1.4.dp.toPx(), center = pt)
            }
        }

        // Etiquetas de frecuencias
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            EQ_BANDS.forEach { band ->
                Text(
                    text = band.label,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EqualizerContent(config: EqualizerConfig, onConfigChange: (EqualizerConfig) -> Unit) {
    val haptics = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            stringResource(R.string.eq_sheet_title),
            style = MaterialTheme.typography.headlineMedium,
            color = CanopyColors.Text,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        // Visualizador paramétrico
        EqualizerResponseMonitor(config = config)

        Spacer(Modifier.height(16.dp))

        // Carrusel horizontal de preajustes con píldoras neumórficas
        Text(
            text = "PRESETS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            ),
            color = CanopyColors.Muted,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(EQ_PRESETS.size) { index ->
                val preset = EQ_PRESETS[index]
                val isSelected = config.presetId == preset.id
                Surface(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onConfigChange(
                            config.copy(
                                presetId = preset.id,
                                gains = preset.gains
                            )
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color(0xFF1E6CEB).copy(alpha = 0.22f) else Color(0xFF0F1624),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) Color(0xFF38BDF8).copy(alpha = 0.60f) else Color.White.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier.height(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 14.dp)) {
                        Text(
                            text = presetLabel(preset.id),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Preamp Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.eq_preamp, config.preampDb.roundToInt()), color = CanopyColors.Text)
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF131B2B),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
            ) {
                Text(
                    text = "${if (config.preampDb > 0) "+" else ""}${config.preampDb.roundToInt()} dB",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (config.preampDb != 0f) Color(0xFF38BDF8) else CanopyColors.Muted,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        Slider(
            value = config.preampDb,
            onValueChange = { 
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onConfigChange(config.copy(preampDb = it, presetId = "custom"))
            },
            valueRange = EqualizerConfig.MIN_PREAMP_DB..EqualizerConfig.MAX_PREAMP_DB,
            colors = SliderDefaults.colors(
                thumbColor = LocalAccent.current,
                activeTrackColor = LocalAccent.current,
                inactiveTrackColor = CanopyColors.Canvas
            )
        )

        Spacer(Modifier.height(12.dp))
        
        // Bass Boost Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.eq_bass_boost, (config.bassBoost * 100).roundToInt()), color = CanopyColors.Text)
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF131B2B),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
            ) {
                Text(
                    text = "${(config.bassBoost * 100).roundToInt()}%",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (config.bassBoost > 0) Color(0xFF38BDF8) else CanopyColors.Muted,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        Slider(
            value = config.bassBoost,
            onValueChange = { 
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onConfigChange(config.copy(bassBoost = it, presetId = "custom"))
            },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = LocalAccent.current,
                activeTrackColor = LocalAccent.current,
                inactiveTrackColor = CanopyColors.Canvas
            )
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "10-BAND PARAMETRIC EQ",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            ),
            color = CanopyColors.Muted,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // 10 Band EQ Sliders
        EQ_BANDS.forEachIndexed { index, band ->
            val gain = config.clampedGains.getOrNull(index) ?: 0f
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
            ) {
                Text(
                    text = band.label,
                    color = CanopyColors.Text,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    modifier = Modifier.width(42.dp),
                    textAlign = TextAlign.End
                )
                Spacer(Modifier.width(12.dp))
                Slider(
                    value = gain,
                    onValueChange = { newGain ->
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        val newGains = config.gains.toMutableList()
                        if (newGains.size < 10) {
                            newGains.addAll(List(10 - newGains.size) { 0f })
                        }
                        newGains[index] = newGain
                        onConfigChange(config.copy(gains = newGains, presetId = "custom"))
                    },
                    valueRange = EqualizerConfig.MIN_GAIN_DB..EqualizerConfig.MAX_GAIN_DB,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = LocalAccent.current,
                        activeTrackColor = LocalAccent.current,
                        inactiveTrackColor = CanopyColors.Canvas
                    )
                )
                Spacer(Modifier.width(12.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF0F1624),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                    modifier = Modifier.width(54.dp)
                ) {
                    Text(
                        text = "${if (gain > 0) "+" else ""}${gain.roundToInt()} dB",
                        color = when {
                            gain > 0 -> Color(0xFF38BDF8)
                            gain < 0 -> Color(0xFF94A3B8)
                            else -> CanopyColors.Muted
                        },
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}
