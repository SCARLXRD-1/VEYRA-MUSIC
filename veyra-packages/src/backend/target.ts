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

export const TARGETS = [
  "linux-x64",
  "linux-arm64",
  "win32-x64",
  "win32-arm64",
  "darwin-x64",
  "darwin-arm64"
] as const;

export type Target = (typeof TARGETS)[number];

type Platform = "linux" | "win32" | "darwin";
type Architecture = "x64" | "arm64";

export function isTarget(value: string): value is Target {
  return (TARGETS as readonly string[]).includes(value);
}

export function normalizeTarget(platform: string, architecture: string): Target {
  if (platform !== "linux" && platform !== "win32" && platform !== "darwin") {
    throw new Error(`Unsupported operating system: ${platform}`);
  }

  const normalizedArchitecture = architecture === "x86_64" || architecture === "amd64"
    ? "x64"
    : architecture === "aarch64"
      ? "arm64"
      : architecture;

  if (normalizedArchitecture !== "x64" && normalizedArchitecture !== "arm64") {
    throw new Error(`Unsupported processor architecture: ${architecture}`);
  }

  return `${platform as Platform}-${normalizedArchitecture as Architecture}`;
}

export function detectTarget(
  platform = process.platform,
  architecture = process.arch,
  environment: NodeJS.ProcessEnv = process.env
): Target {
  // Un proceso x64 puede correr bajo emulación en Windows ARM. Prefiere la arquitectura
  // nativa del SO para que Veyra Music reciba el paquete de addon que su máquina puede ejecutar.
  const windowsArchitecture = platform === "win32"
    ? String(environment.PROCESSOR_ARCHITEW6432 || environment.PROCESSOR_ARCHITECTURE || "").toLowerCase()
    : "";
  const detectedArchitecture = windowsArchitecture.includes("arm64") ? "arm64" : architecture;
  return normalizeTarget(platform, detectedArchitecture);
}
