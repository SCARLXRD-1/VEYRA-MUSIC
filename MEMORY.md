# MEMORY.md — Veyra Music

## Identidad del proyecto

- **Producto:** Veyra Music
- **Sello:** AKASHI DEV
- **Licencia:** AGPL-3.0-or-later

## Sistema de diseño

- Tema oscuro Obsidian Slate: fondo `#0B0F19`, superficies `#131B2B` / `#1E293B`, acento `#1E6CEB` y brillo `#38BDF8`.
- Tema claro Clean Canvas Slate: fondo `#F8FAFC`, superficies `#FFFFFF`, borde `#E2E8F0`.
- Pautas: squircles, bordes hairline, glassmorphism sutil, dock flotante e indicadores de estado compactos.

## Arquitectura y persistencia

- Renderer en Vue 3; proceso de escritorio en Electron; cliente Android y módulos compartidos.
- Persistencia local con almacenamiento `orchard:*` y rutas `userData`; servicios de nube con Supabase y Workers.
- La internacionalización del renderer vive en `src/app/i18n/`, con diccionarios inglés y español de México y selector de idioma.

## Registro de trabajo

### Internacionalización — textos faltantes

- Se migraron textos visibles que aún estaban escritos directamente en inglés a claves de traducción, incluyendo soporte, reproductor, biblioteca, búsqueda, ajustes, diálogos, cola, navegación, cuenta, acciones de reproducción y mensajes de diagnóstico/análisis.
- Se añadieron las traducciones correspondientes en inglés y español de México.
- Se conectaron también cadenas dinámicas emitidas por módulos de reproducción y acciones para que respeten el idioma seleccionado.

### Cliente Móvil v1.0.0 (Cierre y Optimizaciones de Producción)

- **Versión oficial:** `1.0.0` (código de versión 1, codename "AKASHI DEV", namespace `dev.sfg.veyra.connect`).
- **Autenticación e Integraciones:**
  - Login webview nativo con cookies aisladas para Spotify (extracción de `sp_dc` y ClientToken) y Qobuz.
  - Sincronización de nube conectada a la instancia oficial de Supabase de Veyra Music (`furkfhzvvgxlyxukqomm.supabase.co`).
  - Auto-migración en `SharedPreferences` para descartar URLs heredadas de Orchard y redirigir a Veyra.
  - Actualizador móvil vinculado al repositorio oficial de GitHub `SCARLXRD-1/VEYRA-MUSIC`.
- **UI/UX y Paradigmas de Reproducción:**
  - **Grove (Dock Inferior):** Isla flotante squircle con relieve neumórfico, aura reactiva al acento del tema y mini-scrubber con onda sinusoidal activa continua.
  - **Obsidian (HUD Superior):** Consola estilo rack de estudio con vúmetro tri-banda LED en tiempo real, filamento láser osciloscopio y píldora de telemetría WSOLA DSP.
  - **Now Playing:** Scrubber de alta fidelidad con onda sinusoidal elástica (envolvente Tukey sin tirones) y botones hero táctiles.
  - **Fondo dinámico:** Proyección ambiental difuminada de la portada de la canción a pantalla completa con doble orbe lumínico orbital y velo Obsidian contrastado.
- **Optimizaciones de Rendimiento:**
  - Reutilización de objetos `Path` en Compose Canvas para eliminar allocations de memoria por cuadro a 120 FPS en el scrubber, Grove dock, Obsidian HUD y Equalizer Sheet.
  - Gestión proactiva de memoria en Coil ImageLoader con `onTrimMemory` y `onLowMemory` para evitar cierres en segundo plano por el LMK de Android.
  - Minificación R8 y optimización ProGuard activada para APK de producción.

## Hoja de ruta

### 1. Plan de Optimización para la Versión TV (Android TV / Leanback)
- **Navegación D-Pad / Control Remoto:** Enfoque visual neumórfico con anillo de brillo cian (`#38BDF8`), escala reactiva en tarjetas de música y navegación accesible a 3 metros (10-foot UI).
- **Now Playing para Gran Formato:** Carátula en gran escala, fondo ambiental dinámico optimizado para paneles OLED/LED 4K, letras sincronizadas con fuentes legibles a distancia.
- **Optimización de Recursos en Smart TV:** Reducción de resolución de shaders/desenfoques para procesadores de gama de entrada, limitando consumo de RAM a menos de 150 MB y fijando animación a 60 FPS estables.

### 2. Plan para Terminar de Pulir la Versión Desktop
- **Adopción Neumórfica & Obsidian Industrial:** Rediseño de componentes restantes (`HomeView.vue`, `media-card`, `shelf-section`, `EqualizerDialog.vue`) hacia squircles, bordes hairline y paleta Obsidian Slate.
- **Dock de Navegación Flotante:** Barra con radio de `26px`, glassmorphism y micro-indicadores `StatusPillBadge`.
- **Persistencia & Cloud Sync:** Sincronización continua de DSP y metadatos de análisis de audio con Supabase Cloud y SQLite local.

### 3. Estructura de Releases (`releases/`)
- `releases/mobile/`: Especificaciones del APK de producción v1.0.0 (`app-release.apk`), min SDK 31+, arquitecturas `arm64-v8a`/`x86_64` y checksum SHA-256.
- `releases/desktop/`: Binarios para Linux (AppImage/deb), Windows (exe/zip) y macOS (dmg/app).
- `releases/tv/`: Binarios y manifiestos para Android TV / Leanback (D-Pad navigation).

