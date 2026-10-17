# 📺 Veyra Music — Releases Smart TV & Android TV

**Sello:** AKASHI DEV  
**Arquitectura:** Android TV Leanback + Compose for TV  
**Objetivo:** Dispositivos Google TV, Android TV y Amazon Fire TV  

---

## 📦 Especificaciones del Paquete TV

- **Formato:** APK Híbrido Universal Móvil + Android TV (`app-release.apk`)
- **Min SDK:** Android 9.0 (API 28) / Android 10 (API 29)
- **Target SDK:** Android 14 (API 34) / Android 15 (API 35)
- **Modo de entrada:** Control remoto D-Pad (Up, Down, Left, Right, OK/Center, Back, Play/Pause) y Touch
- **Declaraciones de hardware:**
  - `android.software.leanback` = `false` (compatibilidad universal)
  - `android.hardware.touchscreen` = `false` (no requerido)
  - `android.hardware.type.television` = `false`
  - Banner oficial Leanback: `ic_tv_banner` (320x180 dp)
  - Intent Launcher: `android.intent.category.LEANBACK_LAUNCHER`

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
