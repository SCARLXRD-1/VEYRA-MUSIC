# 📺 Veyra Music — Releases Smart TV & Android TV

**Sello:** AKASHI DEV  
**Arquitectura:** Android TV Leanback + Compose for TV  
**Objetivo:** Dispositivos Google TV, Android TV y Amazon Fire TV  

---

## 📦 Paquete actual (v1.0.3)

- **Formato:** APK Híbrido Universal Móvil + Android TV (`veyramusic.apk`)
- **Descarga oficial:** `https://github.com/SCARLXRD-1/VEYRA-MUSIC/releases/download/v1.0.3/veyramusic.apk`
- **SHA-256:** `69129b4c295508b7869444365fe70f5f9c573e3a55db76a05725644524d91da5`
- **Min SDK:** Android 7.0 (API 24)
- **Target SDK:** Android 16 (API 36)
- **Arquitecturas incluidas:** `arm64-v8a` y `armeabi-v7a`
- **Modo de entrada:** Control remoto D-Pad (Up, Down, Left, Right, OK/Center, Back, Play/Pause) y Touch
- **Declaraciones de hardware:**
  - `android.software.leanback` no requerido (admite instalación manual en dispositivos compatibles)
  - `android.hardware.touchscreen` no requerido
  - `android.hardware.type.television` no requerido
  - Banner oficial Leanback: `ic_tv_banner` (320x180 dp)
  - Intent Launcher: `android.intent.category.LEANBACK_LAUNCHER`

La descarga de Smart TV usa el mismo APK universal que Android móvil. Si ya existe una instalación firmada con otra clave, Android puede impedir actualizarla; desinstalarla antes de instalar elimina los datos locales de esa instalación.

---

## 🌟 Características de la Experiencia TV (10-Foot UI) — Estilo YouTube TV

1. **Navegación Espacial Neumórfica (D-Pad):**
   - Indicador de foco con anillo de resplandor Neumórfico de alto contraste (`#1E6CEB` / `#38BDF8`).
   - Micro-escala elástica (`1.08x` / `1.12x`) y elevación z-index reactiva al navegar con las flechas del control.
   - Activación inmediata con tecla OK / Center (`KEYCODE_DPAD_CENTER`, `KEYCODE_ENTER`, `KEYCODE_NUMPAD_ENTER`).
2. **Barra Lateral de Navegación (Rail):**
   - Disposición lateral izquierda fija para pantallas 16:9 de televisor.
   - Alternancia rápida con flechas de dirección entre Home, Buscar, Biblioteca, Conexión y Ajustes.
3. **Controles Multimedia de Hardware:**
   - Soporte nativo para mandos con botones dedicados de reproducción: Play, Pause, Next, Previous, Rewind y Fast Forward.
4. **Tarjetas de Catálogo y Filas de Canciones:**
   - Carruseles horizontales y cuadrículas con desplazamiento espacial continuo optimizado para mando a distancia.
   - Filtros de biblioteca y categorías de búsqueda con selección rápida vía D-Pad.
