# 📱 Veyra Music — Releases Móvil (Android)

**Sello:** AKASHI DEV  
**Package ID:** `dev.sfg.veyra.mobile`
**Objetivo:** Android móvil, Android TV y Google TV

---

## 📦 Versión Actual: `1.0.2` (APK universal)

- **Nombre de archivo:** `veyramusic.apk`
- **Versión:** `1.0.2`
- **Código de versión:** `3`
- **Tamaño:** `162 MiB` (170,014,594 bytes)
- **Min SDK:** Android 7 (API 24)
- **Target SDK:** Android 16 (API 36)
- **Arquitecturas nativas del release:** `arm64-v8a`, `armeabi-v7a`
- **Motor DSP nativo:** Earmark / Signalsmith Stretch (C++/Rust)
- **SHA-256:**
  ```text
  69129b4c295508b7869444365fe70f5f9c573e3a55db76a05725644524d91da5
  ```

---

## 🚀 Novedades de la Versión 1.0.2

1. **Efecto Cristal Esmerilado Universal:** Neo-glassmorphism reactivo en tiempo real con shaders de alto rendimiento.
2. **Claridad de Cuenta Veyra (Cloud Sync):** Conmutador claro entre Iniciar sesión y Crear cuenta, con explicaciones claras de sincronización PC y opcionalidad.
3. **Auto-reproducción Continua Offline:** Reproducción secuencial ininterrumpida entre todas las canciones descargadas en local.
4. **Notas de Versión Bilingües Dinámicas:** Detección automática de idioma (español e inglés).

---

## 📲 Instrucciones de Instalación (Side-loading)

1. Descargue `veyramusic.apk` desde el enlace oficial de la versión 1.0.2 y transfiera el archivo al dispositivo.
2. Abra el archivo desde el explorador de archivos y autorice la instalación de aplicaciones de orígenes desconocidos.
3. También puede instalarlo directamente vía USB con Android Debug Bridge (ADB):
   ```bash
   adb install -r veyramusic.apk
   ```

---

## 🛠️ Compilación Local desde Fuentes

Para compilar el APK release de producción desde la raíz del proyecto:

```bash
cd mobile/android
# El APK generado por Gradle en app/build/outputs/apk/release/app-release.apk
# se distribuye oficialmente con el nombre veyramusic.apk
```
