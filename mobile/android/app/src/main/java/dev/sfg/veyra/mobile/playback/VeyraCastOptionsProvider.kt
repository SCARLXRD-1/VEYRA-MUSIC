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

package dev.sfg.veyra.mobile.playback

import android.content.Context
import com.google.android.gms.cast.CastMediaControlIntent
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import dev.sfg.veyra.connect.R

/**
 * Proporciona CastOptions para Veyra Music, admitiendo un App ID de Google Cast registrado
 * a medida o recurriendo al Default Media Receiver estándar de Google.
 */
class VeyraCastOptionsProvider : OptionsProvider {
    override fun getCastOptions(context: Context): CastOptions {
        val configured =
            runCatching { context.getString(R.string.cast_app_id).trim() }.getOrNull()

        val appId =
            if (!configured.isNullOrBlank() && configured != "DEC44B9D") {
                configured
            } else {
                CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID
            }

        return CastOptions.Builder()
            .setReceiverApplicationId(appId)
            .setStopReceiverApplicationWhenEndingSession(true)
            .build()
    }

    override fun getAdditionalSessionProviders(context: Context): List<SessionProvider> =
        emptyList()
}
