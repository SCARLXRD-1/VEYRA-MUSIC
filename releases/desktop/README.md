# 🖥️ Veyra Music — Releases Escritorio (Desktop)

**Sello:** AKASHI DEV  
**Arquitectura:** Electron + Vue 3 + C++/Rust Native Audio DSP  
**Objetivo:** Linux, Windows y macOS  

---

## 📦 Formatos de Distribución

| Sistema Operativo | Formato | Arquitectura | Estado |
| :--- | :--- | :--- | :--- |
| **Linux** | AppImage / Deb / Tar.gz | `x86_64`, `aarch64` | Preparado para build |
| **Windows** | Instalador NSIS (.exe) / Portable (.zip) | `x64` | Preparado para build |
| **macOS** | DMG / App Zip | `Apple Silicon (arm64)`, `Intel (x64)` | Preparado para build |

---

## ⚡ Especificaciones Técnicas

- **Fidelidad Acústica:** Motor Web Audio enriquecido con addon C++/Rust para Smart Crossfade (planificación WSOLA y detección de transitorios).
- **Aceleración Gráfica:** Renderizado por GPU para visualizador de espectro a 60 FPS y fondo dinámico ambiental con `backdrop-filter: blur(28px)`.
- **Integraciones:** Mpris (Linux), SMTC (Windows), Now Playing (macOS), Discord Rich Presence y Last.fm Scrobbler.
- **Sincronización:** Supabase Cloud + base de datos local SQLite (`node:sqlite`).

---

## 🛠️ Compilación Local de Binarios de Producción

Para compilar el frontend y los paquetes de escritorio:

```bash
# 1. Compilar addons nativos y bundle de producción
npm run build

# 2. Empaquetar binarios para la plataforma actual
npm run release
```
