/*
 * Copyright (C) 2026 SFG545 / Veyra Music (AKASHI DEV)
 * AGPL-3.0-or-later
 */

package dev.sfg.veyra.mobile.ui.tv

import android.view.KeyEvent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.sfg.veyra.mobile.ui.theme.CanopyColors

/**
 * Modificador universal para elementos navegables por D-Pad en Smart TV.
 * Proporciona escala reactiva, anillo Neumórfico de resplandor e intercepta
 * las pulsaciones del botón OK/Enter del control remoto.
 */
fun Modifier.tvFocusable(
    shape: Shape = RoundedCornerShape(14.dp),
    scale: Float = 1.08f,
    focusBorderWidth: Dp = 2.5.dp,
    focusBorderColor: Color = CanopyColors.SecondaryAccent,
    focusGlowColor: Color = CanopyColors.Accent,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    val animScale by animateFloatAsState(
        targetValue = if (isFocused) scale else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 420f),
        label = "TvFocusScale",
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isFocused) 16.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 420f),
        label = "TvFocusShadow",
    )

    this
        .zIndex(if (isFocused) 20f else 1f)
        .scale(animScale)
        .shadow(
            elevation = shadowElevation,
            shape = shape,
            ambientColor = focusGlowColor,
            spotColor = focusBorderColor,
        )
        .then(
            if (isFocused) {
                Modifier.border(focusBorderWidth, focusBorderColor, shape)
            } else {
                Modifier
            }
        )
        .onFocusChanged { focusState ->
            if (focusState.isFocused != isFocused) {
                isFocused = focusState.isFocused
                if (isFocused) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }
        }
        .onPreviewKeyEvent { keyEvent ->
            if (enabled && keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                when (keyEvent.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER,
                    KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                        onClick?.invoke()
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        }
        .focusable(enabled = enabled)
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = enabled,
                    onClick = onClick,
                )
            } else {
                Modifier
            }
        )
}

/**
 * Variante de foco optimizada para botones de la barra de navegación lateral (Rail).
 */
fun Modifier.tvNavRailFocusable(
    isSelected: Boolean,
    shape: Shape = RoundedCornerShape(16.dp),
    onClick: () -> Unit,
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }

    val animScale by animateFloatAsState(
        targetValue = if (isFocused) 1.12f else if (isSelected) 1.04f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 450f),
        label = "TvNavRailScale",
    )

    val borderColor = when {
        isFocused -> CanopyColors.SecondaryAccent
        isSelected -> Color.White.copy(alpha = 0.25f)
        else -> Color.Transparent
    }

    val borderWidth = if (isFocused) 2.5.dp else 1.dp

    this
        .zIndex(if (isFocused) 25f else 1f)
        .scale(animScale)
        .border(borderWidth, borderColor, shape)
        .onFocusChanged { isFocused = it.isFocused }
        .onPreviewKeyEvent { keyEvent ->
            if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                when (keyEvent.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER,
                    KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                        onClick()
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        }
        .focusable()
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
}
