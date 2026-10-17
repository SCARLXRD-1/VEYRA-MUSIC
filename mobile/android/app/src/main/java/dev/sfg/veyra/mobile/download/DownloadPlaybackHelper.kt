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

package dev.sfg.veyra.mobile.download

import android.net.Uri
import dev.sfg.veyra.mobile.playback.ResolvedStream

/** Helper for resolving downloaded audio files into playback streams. */
object DownloadPlaybackHelper {
    /**
     * Resolves a local stream for [videoId] if it exists in [downloadManager].
     *
     * @return [ResolvedStream] pointing to `file:///...` path, or `null` if not downloaded.
     */
    fun resolveOfflineStream(videoId: String, downloadManager: DownloadManager): ResolvedStream? {
        val file = downloadManager.getDownloadedFile(videoId) ?: return null
        val item = downloadManager.downloads.value[videoId]
        val fileUri = Uri.fromFile(file).toString()
        val mime = item?.mimeType?.ifBlank { "audio/webm" } ?: "audio/webm"
        
        return ResolvedStream(
            url = fileUri,
            mimeType = mime,
            expiresAtMs = Long.MAX_VALUE,
            bitrateKbps = 320,
        )
    }
}
