# 📱 Veyra Music — Releases Móvil (Android)

**Sello:** AKASHI DEV  
**Package ID:** `dev.sfg.veyra.connect`  
**Objetivo:** Smartphones, Tablets y Plegables (Foldables)  

---

## 📦 Versión Actual: `1.0.0` (Release de Producción)

- **Nombre de archivo:** `app-release.apk`
- **Versión:** `1.0.0`
- **Código de versión:** `1`
- **Tamaño:** `129 MB` (135,160,892 bytes)
- **Min SDK:** Android 12 (API 31)
- **Target SDK:** Android 15 (API 35)
- **Arquitecturas nativas:** `arm64-v8a`, `armeabi-v7a`, `x86_64`
- **Motor DSP nativo:** Earmark / Signalsmith Stretch (C++/Rust)
- **SHA-256:**
  ```text
  41538c2b6b166c892cab7e708a25c7201f9cf735ba21ad80c96bdec5a176a1a3
  ```

---

## 🚀 Novedades de la Versión 1.0.0

1. **Paridad UI/UX Absoluta con Desktop:**
   - **Grove Mode:** Dock inferior flotante squircle (`26.dp`) con canal chiseled y onda sinusoidal activa continua.
   - **Obsidian Mode:** Consola superior HUD estilo rack de estudio con vúmetro tri-banda LED en tiempo real y telemetría WSOLA DSP.
2. **Fondo Dinámico por Canción Reforzado:**
   - Proyección de carátula ambiental difuminada a pantalla completa con doble orbe orbital y velo Obsidian Slate (`#0B0F19`) de alto contraste.
3. **Cero Asignación de Memoria (Zero GC Allocation) a 120 FPS:**
   - Reutilización de trazados `Path` en Compose Canvas en scrubber, dock, HUD y ecualizador.
   - Trimming proactivo de caché Coil (`onTrimMemory` / `onLowMemory`).
4. **Infraestructura Oficial Veyra:**
   - Sincronización en la nube con Supabase oficial (`furkfhzvvgxlyxukqomm.supabase.co`).
   - Actualizador automático enlazado a `SCARLXRD-1/VEYRA-MUSIC`.
   - Login nativo con WebView aislado para Spotify y Qobuz.

---

## 📲 Instrucciones de Instalación (Side-loading)

1. Transfiera el archivo `app-release.apk` al dispositivo móvil.
2. Abra el archivo desde el explorador de archivos y autorice la instalación de aplicaciones de orígenes desconocidos.
3. También puede instalarlo directamente vía USB con Android Debug Bridge (ADB):
   ```bash
   adb install -r app-release.apk
   ```

---

## 🛠️ Compilación Local desde Fuentes

Para compilar el APK release de producción desde la raíz del proyecto:

```bash
cd mobile/android
./gradlew assembleRelease
# El APK se generará en: app/build/outputs/apk/release/app-release.apk
```
