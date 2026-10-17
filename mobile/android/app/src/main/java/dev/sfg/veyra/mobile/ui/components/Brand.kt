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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.connect.R

import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape

@Composable
fun VeyraMark(
    modifier: Modifier = Modifier,
    contentDescription: String? = "Veyra Music",
) {
    Image(
        painter = painterResource(R.drawable.veyra_logo),
        contentDescription = contentDescription,
        modifier = modifier.clip(CircleShape),
    )
}

@Composable
fun OrchardMark(
    modifier: Modifier = Modifier,
    contentDescription: String? = "Veyra Music",
) = VeyraMark(modifier, contentDescription)

@Composable
fun VeyraWordmark(modifier: Modifier = Modifier) {
    Row(modifier) {
        VeyraMark(Modifier.size(28.dp))
        Spacer(Modifier.width(9.dp))
        Text("Veyra Music", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun OrchardWordmark(modifier: Modifier = Modifier) = VeyraWordmark(modifier)
