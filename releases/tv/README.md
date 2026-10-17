# 📺 Veyra Music — Releases Smart TV & Android TV

**Sello:** AKASHI DEV  
**Arquitectura:** Android TV Leanback + Compose for TV  
**Objetivo:** Dispositivos Google TV, Android TV y Amazon Fire TV  

---

## 📦 Especificaciones del Paquete TV

- **Formato:** APK Leanback Universal (`app-tv-release.apk`)
- **Min SDK:** Android 9.0 (API 28) / Android 10 (API 29)
- **Target SDK:** Android 14 (API 34) / Android 15 (API 35)
- **Modo de entrada requerido:** Control remoto D-Pad (Up, Down, Left, Right, OK, Back, Play/Pause)
- **Declaraciones de hardware:**
  - `android.software.leanback` = `true`
  - `android.hardware.touchscreen` = `false`

---

## 🌟 Características de la Experiencia TV (10-Foot UI)

1. **Navegación Espacial Neumórfica (D-Pad):**
   - Indicador de foco con anillo de resplandor Neumórfico de alto contraste (`#1E6CEB` / `#38BDF8`).
   - Micro-escala (`1.08x`) y elevación reactiva en tarjetas de música y controles.
2. **Now Playing en Gran Formato (Ambient Hero):**
   - Diseñado para visualización nítida a 3 metros de distancia.
   - Carátula a gran escala, metadatos en tipografía expandida (28-36px) y letras sincronizadas con desplazamiento vertical suave.
3. **Protección Anti-Burn-in para Paneles OLED:**
   - Desplazamiento periódico imperceptible de píxeles (Pixel Shifter) para evitar retención de imagen en pantallas OLED tras reproducción prolongada.
4. **Perfil "TV Lite" de Bajo Consumo:**
   - Adaptación inteligente a SoC de televisores y dongles HDMI económicos.
   - Sustitución de `backdrop-filter: blur` por gradientes pre-compuestos de bajo coste.
   - Límite de 30 FPS en el analizador de espectro de audio para reservar ciclos de CPU al decodificador de audio bit-perfect.
