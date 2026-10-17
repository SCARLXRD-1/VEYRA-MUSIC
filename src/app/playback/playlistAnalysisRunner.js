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
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Veyra Music. If not, see <https://www.gnu.org/licenses/>.
 */

import { ref } from 'vue';
import { t } from '../i18n/index.js';
import {
  ANALYSIS_PREPARATION_CONCURRENCY,
  ANALYSIS_PRIORITIES
} from '../../audio/crossfade/smartCrossfadeAnalysis.js';
import { syncTrackAnalysisToCloud, fetchBatchCloudAnalysis } from '../../services/cloudAnalysisSync.js';

function persistCloudAnalysis(trackId, analysis) {
  try {
    const analysisBridge = globalThis.veyraAudioAnalysis || globalThis.orchardAudioAnalysis;
    const pending = analysisBridge?.store?.(trackId, analysis);
    void Promise.resolve(pending).catch(() => {});
  } catch {
    // Cloud evidence remains useful for this run when the optional desktop
    // analysis cache is unavailable or refuses a write.
  }
}

export function createPlaylistAnalysisRunner(ctx, {
  fetchCloudAnalysis = fetchBatchCloudAnalysis,
  syncAnalysis = syncTrackAnalysisToCloud,
  storeAnalysis = persistCloudAnalysis,
  concurrency = ANALYSIS_PREPARATION_CONCURRENCY
} = {}) {
  const isAnalyzing = ref(false);
  const progress = ref(0);
  const totalTracks = ref(0);
  const completedTracks = ref(0);
  const currentStatus = ref('');
  let abortController = null;

  function reset() {
    isAnalyzing.value = false;
    progress.value = 0;
    totalTracks.value = 0;
    completedTracks.value = 0;
    currentStatus.value = '';
    abortController = null;
  }

  function cancel() {
    if (abortController) {
      abortController.abort();
    }
    reset();
  }

  /**
   * Analyzes an entire playlist / album of tracks in the background,
   * checking cloud cache first, analyzing missing tracks locally,
   * and uploading newly analyzed tracks to Supabase.
   * If any song fails, it skips that song without failing the whole process.
   *
   * @param {Array<object>} tracks
   * @returns {Promise<{ success: number, failed: number, cached: number }>}
   */
  async function analyzePlaylist(tracks) {
    if (!Array.isArray(tracks) || tracks.length === 0) {
      return { success: 0, failed: 0, cached: 0 };
    }

    if (isAnalyzing.value) {
      cancel();
    }

    const uniqueTracks = Array.from(
      new Map(tracks.filter(t => t?.id).map(t => [t.id, t])).values()
    );

    if (uniqueTracks.length === 0) {
      return { success: 0, failed: 0, cached: 0 };
    }

    isAnalyzing.value = true;
    totalTracks.value = uniqueTracks.length;
    completedTracks.value = 0;
    progress.value = 0;
    currentStatus.value = t('ui.analysis.checkingCloud');

    const controller = new AbortController();
    abortController = controller;
    const signal = controller.signal;

    const stats = { success: 0, failed: 0, cached: 0 };

    try {
      // 1. Check Cloud Cache for existing analysis
      const videoIds = uniqueTracks.map(t => t.id);
      const cloudMetadata = await fetchCloudAnalysis(videoIds);
      if (signal.aborted) return stats;

      const missingTracks = [];
      for (const track of uniqueTracks) {
        if (cloudMetadata.has(track.id)) {
          stats.cached++;
          completedTracks.value++;
          storeAnalysis(track.id, cloudMetadata.get(track.id));
        } else {
          missingTracks.push(track);
        }
      }

      progress.value = completedTracks.value / totalTracks.value;

      if (missingTracks.length === 0) {
        currentStatus.value = t('ui.analysis.allCached', { count: totalTracks.value });
        isAnalyzing.value = false;
        return stats;
      }

      const analyze = ctx.smartCrossfadeAnalyzer?.analyze;
      const resolvePlayable = ctx.resolvePlayableTrack;

      if (typeof analyze !== 'function' || typeof resolvePlayable !== 'function') {
        currentStatus.value = t('ui.analysis.analyzerUnavailable');
        isAnalyzing.value = false;
        return stats;
      }

      // 2. Feed the analyzer concurrently. It applies its own matching bounded
      // queue, so audio preparation can use the desktop without overwhelming it.
      let nextTrackIndex = 0;
      const workerCount = Math.min(
        missingTracks.length,
        Math.max(1, Number(concurrency) || 1)
      );

      async function analyzeNextTrack() {
        while (!signal.aborted) {
          const trackIndex = nextTrackIndex++;
          if (trackIndex >= missingTracks.length) return;

          const track = missingTracks[trackIndex];
          currentStatus.value = t('ui.analysis.analyzing', {
            completed: completedTracks.value,
            total: totalTracks.value
          });

          try {
            const resolved = await resolvePlayable.call(ctx, track).catch(() => null);
            if (signal.aborted) return;
            const streamUrl = resolved?.streamUrl || '';

            if (!streamUrl) {
              stats.failed++;
            } else {
              const durationSeconds = Number(track.durationSeconds || track.duration) || 0;
              const analysis = await analyze.call(
                ctx.smartCrossfadeAnalyzer,
                track.id,
                streamUrl,
                {
                  duration: durationSeconds,
                  priority: ANALYSIS_PRIORITIES.background,
                  signal
                }
              );

              if (analysis && (analysis.bpm > 0 || analysis.analyzedBpm > 0)) {
                stats.success++;
                void syncAnalysis(track.id, analysis);
              } else {
                stats.failed++;
              }
            }
          } catch (err) {
            if (signal.aborted || err?.name === 'AbortError') return;
            // Failure on one song must not stop the batch.
            stats.failed++;
            console.warn(`Analysis skipped for song ${track.id}:`, err);
          }

          completedTracks.value++;
          progress.value = completedTracks.value / totalTracks.value;
        }
      }

      await Promise.all(Array.from({ length: workerCount }, () => analyzeNextTrack()));

      if (!signal.aborted) {
        currentStatus.value = t('ui.analysis.completed', {
          count: stats.success + stats.cached,
          cached: stats.cached,
          processed: stats.success
        });
      }
    } catch (err) {
      if (!signal.aborted) {
        console.error('Playlist analysis runner error:', err);
        currentStatus.value = t('ui.analysis.failed');
      }
    } finally {
      if (abortController === controller) {
        abortController = null;
        isAnalyzing.value = false;
      }
    }

    return stats;
  }

  return {
    isAnalyzing,
    progress,
    totalTracks,
    completedTracks,
    currentStatus,
    analyzePlaylist,
    cancel
  };
}
