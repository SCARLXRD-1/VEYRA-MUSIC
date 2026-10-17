# 🖥️ Veyra Music — Releases Escritorio (Desktop)

**Sello:** AKASHI DEV  
**Arquitectura:** Electron + Vue 3 + C++/Rust Native Audio DSP  
**Objetivo:** Linux, Windows y macOS  

---

## 📦 Formatos de Distribución (v1.0.0 Oficial)

| Sistema Operativo | Formato | Arquitectura | Tamaño | Estado |
| :--- | :--- | :--- | :--- | :--- |
| **Linux** | Standalone Tar.gz (`veyra-v1.0.0-linux-x64.tar.gz`) | `x86_64` | 394 MB | ✅ Compilado y verificado |
| **Windows** | Portable Zip (`veyra-v1.0.0-windows-x64.zip`) | `x64` | 343 MB | ✅ Compilado y verificado |

---

## 🔐 Sumas de Verificación (SHA-256)

```text
380e01b4bcb502bf499145b30d78d0dbc75509245f630aade38aa21ded0d3aac  veyra-v1.0.0-linux-x64.tar.gz
b5116718574bd13e3eff4415658251b2d0ff60d5a91655e5c1bbd253f6c35b29  veyra-v1.0.0-windows-x64.zip
```

---

## ⚡ Especificaciones Técnicas

- **Fidelidad Acústica:** Motor Web Audio enriquecido con addon C++/Rust para Smart Crossfade (planificación WSOLA y detección de transitorios).
- **Aceleración Gráfica:** Renderizado por GPU para visualizador de espectro a 60 FPS y fondo dinámico ambiental con `backdrop-filter: blur(28px)`.
- **Integraciones:** Mpris (Linux), SMTC (Windows), Now Playing (macOS), Discord Rich Presence y Last.fm Scrobbler.
- **Sincronización:** Supabase Cloud + base de datos local SQLite (`node:sqlite`).

---

## 🛠️ Ejecución y Empaquetado

### En Linux:
```bash
tar -xzf veyra-v1.0.0-linux-x64.tar.gz
cd veyra-linux-x64
./veyra.sh
```

### En Windows:
1. Descomprimir `veyra-v1.0.0-windows-x64.zip`.
2. Ejecutar `veyra.exe` o `veyra.cmd`.

### Para Recompilar los Paquetes:
```bash
npm run release:desktop
```
