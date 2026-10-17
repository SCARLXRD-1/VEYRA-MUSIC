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

#include <jni.h>

#include <exception>
#include <string>

#include "planner/pair_scorer.h"

extern "C" JNIEXPORT jstring JNICALL
Java_dev_sfg_veyra_mobile_playback_smart_NativeBestMixPlanner_nativeInvoke(
    JNIEnv* env, jobject, jstring request) {
  if (request == nullptr) return nullptr;
  const char* chars = env->GetStringUTFChars(request, nullptr);
  if (chars == nullptr) return nullptr;
  std::string input(chars);
  env->ReleaseStringUTFChars(request, chars);
  try {
    const auto parsed = orchard::planner::Json::parse(input);
    const auto result = parsed.contains("analyses")
        ? orchard::planner::SortQueue(parsed.at("analyses"),
                                      parsed.value("initial", orchard::planner::Json(nullptr)))
        : orchard::planner::ScorePair(parsed.at("analysis"), parsed.at("nextAnalysis"));
    return env->NewStringUTF(result.dump().c_str());
  } catch (const std::exception& error) {
    jclass exception = env->FindClass("java/lang/IllegalArgumentException");
    if (exception != nullptr) env->ThrowNew(exception, error.what());
    return nullptr;
  }
}
