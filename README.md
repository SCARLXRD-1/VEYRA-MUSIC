<div align="center">
  <img src="public/veyra-logo.png" alt="Logotipo de Veyra Music" width="130" height="130">

  # Veyra Music

  ### Fork Personal de Orchard por AKASHI DEV
  *Mejoras visuales y diseño UI/UX adaptados al gusto personal de AKASHI DEV*

  *Smart Crossfade • Best Mix Armónico • EQ de 10 Bandas • Veyra Connect LAN • Cliente Android • Listening Parties*

  <p align="center">
    <a href="https://github.com/SCARLXRD-1/VEYRA-MUSIC/releases"><img src="https://img.shields.io/github/v/release/SCARLXRD-1/VEYRA-MUSIC?style=for-the-badge&color=1E6CEB&label=Versi%C3%B3n" alt="Última versión"></a>
    <a href="LICENSE"><img src="https://img.shields.io/badge/Licencia-AGPL--3.0--or--later-blue.svg?style=for-the-badge&color=0B0F19" alt="Licencia"></a>
    <a href="https://github.com/SCARLXRD-1/VEYRA-MUSIC"><img src="https://img.shields.io/badge/Plataformas-Windows%20%7C%20Linux%20%7C%20macOS%20%7C%20Android-informational?style=for-the-badge&color=38BDF8" alt="Plataformas"></a>
    <img src="https://img.shields.io/badge/Tests-651%20Aprobadas-2ea44f?style=for-the-badge" alt="Pruebas">
  </p>

  <p align="center">
    <a href="#-descargas"><b>Descargas</b></a> •
    <a href="#-dos-interfaces-emblem%C3%A1ticas"><b>Interfaces Grove & Obsidian</b></a> •
    <a href="#-caracter%C3%ADsticas-principales"><b>Características</b></a> •
    <a href="#-veyra-mobile-para-android"><b>App Móvil Android</b></a> •
    <a href="#-compilaci%C3%B3n-desde-el-c%C3%B3digo-fuente"><b>Compilación</b></a> •
    <a href="#-reconocimiento-open-source-y-fork"><b>Créditos y Licencia</b></a>
  </p>
</div>

---

## 📖 Visión General

> [!IMPORTANT]
> **Veyra Music** es un **fork independiente de [Orchard](https://github.com/SFG5453/Orchard)** (creado por **SFG5453**), mantenido bajo el sello de **AKASHI DEV** únicamente para consumo personal y sin afiliación con el proyecto original.
>
> Las modificaciones realizadas sobre el software original corresponden a **mejoras visuales, estéticas y de diseño UI/UX adaptadas al gusto personal del usuario AKASHI DEV** (incorporación de Neumorfismo de alta fidelidad, la estética Obsidian / Enterprise Slate, diseño táctil para la versión móvil de Android y soporte en español), preservando intacta la sólida arquitectura de ingeniería y las capacidades técnicas del software original.

Combina una arquitectura en **Vue 3** y **Electron** con un motor de audio de bajo nivel en **Rust (Earmark)** y algoritmos de machine learning. Ofrece transiciones automáticas con beat-matching estilo DJ, ecualización paramétrica con perfiles Auto-EQ, sincronización local mediante **Veyra Connect**, sesiones colaborativas entre pares (*Listening Parties*) y una aplicación nativa para Android en **Jetpack Compose**.

> [!NOTE]
> Veyra Music se conecta de forma segura a YouTube Music mediante solicitudes InnerTube respaldadas por sesión de navegador. Es un desarrollo independiente bajo el sello **AKASHI DEV** y no está afiliado ni respaldado por Google ni YouTube.

---

## 🎨 Dos Interfaces Emblemáticas

Veyra Music permite al usuario elegir entre dos paradigmas visuales de ingeniería pulida:

### 🌿 1. Modo Grove (Isla Flotante Neumórfica)
*Diseñado para ergonomía moderna, fluidez visual y respuesta táctil.*
* **Dock Suspendido Inferior:** Barra con curvatura superelíptica (*squircle* de `26.dp`) que flota elegantemente sobre el contenido.
* **Botones Convexos con `TactilePress`:** Relieve tridimensional en reposo que conmuta a bajorrelieve (*inset shadow*) al presionar, brindando respuesta háptica y micro-rebote físico.
* **Barra de Avance con Onda Sonora Viva (`NeumorphicWaveSlider`):** Renderizado vectorial en tiempo real que simula la energía de la música mediante curvas Bezier activas.
* **Quick Audio Deck:** Consola desplegable al pulsar la píldora DSP para ajustar en segundos el Crossfade, presets de ecualización y ganancia analógica de Preamp.

### 🌑 2. Modo Obsidian — Slate Industrial (Consola Superior HUD)
*Inspirado en equipos de audio analógicos de estudio montados en rack (estilo Linear / Teenage Engineering).*
* **Marco Superior de Rack:** Chasis industrial en tono *Obsidian Deep* (`#0B0F19`) con biseles de hardware integrados en la zona superior de la ventana o *safe-area* móvil.
* **Pantalla OLED Readout en Vivo:** Monitoreo técnico en tiempo real de decibelios pico (*True Peak dB*), sonoridad (*-14.2 LUFS*), frecuencia (*96.0 kHz / 24-bit*) y estado del motor DSP (*WSOLA / Direct*).
* **Filamento Láser:** Medidor horizontal de señal de audio con degradado *Sky Flare* (`#38BDF8`) a *Azul Eléctrico* (`#1E6CEB`).

---

## ⚡ Características Principales

| **Smart AutoMix Crossfade** | **Best Mix (Inteligencia de Colas)** | **Motor de Audio Audiófilo** |
| :--- | :--- | :--- |
| Transiciones sincronizadas al compás y frases musicales con WSOLA en Rust, intercambio progresivo de frecuencias graves y barridos de filtro. | Ordenación armónica inteligente de cualquier lista mediante la rueda Camelot y el análisis de tempo BPM. | Ecualizador paramétrico y gráfico de 10 bandas, perfiles Auto-EQ para cientos de audífonos, nivelación de volumen y visualizador espectral en tiempo real. |

| **Veyra Connect LAN** | **Privacidad & Replay Local** | **Integración Nativa** |
| :--- | :--- | :--- |
| Transferencia de reproducción y control remoto encriptado entre tu PC y la app de Android sin salir de la red local. | Estadísticas anuales en el dispositivo (artistas, canciones y minutos escuchados), letras sincronizadas y radar de lanzamientos. | Discord Rich Presence dinámico con carátulas animadas, Last.fm / ListenBrainz scrobbling y atajos globales multimedia. |

---

## 📱 Veyra Mobile para Android

La aplicación móvil nativa (`mobile/android`) traslada la experiencia completa de escritorio a tu teléfono:

* **Arquitectura:** Desarrollada en Kotlin con **Jetpack Compose** y librerías nativas en C++/Rust para análisis de audio local.
* **Soporte de Ambos Modos:** Alterna en tiempo real entre el **Dock Flotante Grove** y la **Consola HUD Obsidian** desde *Ajustes ➔ Apariencia*.
* **Optimizado para Teléfonos y Plegables:** Soporte completo de pantalla completa, gestos ergonómicos y riel de navegación para dispositivos tipo Foldable.
* **Idioma Español Integrado:** Traducción completa al español de México (`es-MX`).

### Instalación Rápida del APK
El archivo instalable precompilado se encuentra directamente disponible en el repositorio:
```bash
# Ubicación del instalador listo para usar:
veyra-music-debug.apk

# Instalación por USB con ADB:
adb install -r veyra-music-debug.apk
```

---

## 📥 Descargas y Plataformas

| Plataforma | Formatos de Paquete | Arquitecturas |
| :--- | :--- | :--- |
| **Linux** | Debian (`.deb`), RPM (`.rpm`), Flatpak (`.flatpak`), Arch Linux (`.pkg.tar.zst`), AppImage / portable | x64, arm64 |
| **Windows** | Instalador NSIS (`.exe`), archivo portable (`.zip`) | x64, arm64 |
| **macOS** | Paquetes firmados / archivos de aplicación (`.zip`) | Apple Silicon (arm64), Intel (x64) |
| **Android** | Paquete APK compilado (`.apk`) | arm64-v8a, x86_64 |

Accede a la sección de [Releases de GitHub](https://github.com/SCARLXRD-1/VEYRA-MUSIC/releases) para descargar los paquetes oficiales para tu sistema operativo.

---

## 🛠️ Compilación desde el Código Fuente

### Requisitos del Sistema
* **Node.js:** Versión v22.x o v24.x LTS y **npm**
* **Rust:** Toolchain estable (`rustc` y `cargo`)
* **Compilador C/C++:** `gcc` / `clang` y herramientas de desarrollo para addons nativos
* **Android SDK & NDK** *(únicamente si compilas el APK móvil)*: NDK 28+ y CMake 3.22+

### 1. Clonar el Repositorio
```bash
git clone https://github.com/SCARLXRD-1/VEYRA-MUSIC.git
cd VEYRA-MUSIC
```

### 2. Instalar Dependencias de Node
```bash
npm ci
```

### 3. Compilar Módulos Nativos de Audio
```bash
npm run build:native
```

### 4. Iniciar en Modo Desarrollo (Escritorio)
```bash
npm run dev
```
*Inicia Vite en `http://127.0.0.1:5173`, compila los addons de Rust y lanza Electron.*

### 5. Compilar la Versión de Producción de Escritorio
```bash
npm run build
```

### 6. Ejecutar la Suite de Pruebas
```bash
npm test
```
*Ejecuta la suite integral de pruebas unitarias y de integración (651 pruebas pasando limpiamente).*

### 7. Compilar la Aplicación Android (APK)
```bash
cd mobile/android
./gradlew assembleDebug
```
*El APK generado se guardará en `mobile/android/app/build/outputs/apk/debug/app-debug.apk`.*

---

## 📁 Arquitectura del Repositorio

```text
VEYRA-MUSIC/
├── src/                         Renderer en Vue 3 y Quasar (Componentes, Vistas, Estilos)
│   ├── app/                     Lógica de apariencia, catálogo, reproducción y red
│   ├── audio/                   Web Audio API, WSOLA, EQ de 10 bandas y analizadores
│   └── components/              Dock Grove, HUD Obsidian, WaveSlider, Modales y Controles
├── electron/                    Proceso principal de Electron
│   ├── main/                    Ciclo de vida, menús y ventanas de la aplicación
│   ├── preload/                 Puente sandboxed IPC seguro
│   ├── audio/                   Servicios de análisis y comunicación con addons nativos
│   └── connect/                 Servidor WebSocket seguro para Veyra Connect LAN
├── native-audio-rust/           Motor de procesamiento acústico en Rust (DSP, Earmark, N-API)
├── mobile/android/              Cliente nativo para Android (Kotlin + Jetpack Compose)
│   ├── app/src/main/            Interfaz Compose (GroveFloatingDock, ObsidianHeaderHUD)
│   └── core/                    Bindings UniFFI con el motor nativo Earmark
├── veyra-packages/              Administrador de paquetes y empaquetador para Linux y Windows
├── workers/                     Microservicios en Cloudflare Workers para Listening Parties
└── test/                        Suite de 651 pruebas automatizadas (node:test)
```

---

## 🤝 Reconocimiento Open Source y Fork

* **Naturaleza del Proyecto:** Veyra Music es un **fork independiente de [Orchard](https://github.com/SFG5453/Orchard)**, software de código abierto desarrollado originalmente por [SFG5453](https://github.com/SFG5453).
* **Propósito y Alcance:** Es mantenido únicamente para **consumo y uso personal por AKASHI DEV**, sin afiliación oficial con el proyecto original. Las modificaciones realizadas son fundamentalmente **mejoras visuales, estéticas, ergonomía móvil y rediseño de interfaz UI/UX adaptados al gusto personal del usuario AKASHI DEV**.
* **Reconocimiento y Apoyo al Creador Original:** Todo el mérito de la arquitectura base, motor de audio y capacidades del sistema corresponde a SFG5453. Te invitamos a visitar, dar una estrella y apoyar el repositorio matriz: 👉 **[github.com/SFG5453/Orchard](https://github.com/SFG5453/Orchard)**.
* **Licencia:** Distribuido bajo los términos íntegros de la licencia **GNU Affero General Public License v3.0 or later** ([AGPL-3.0-or-later](LICENSE)). Consulta también [LICENSE-NOTICE.txt](LICENSE-NOTICE.txt).
* **Metadatos y Modelos:** Detección de BPM cortesía de [GetSongBPM](https://getsongbpm.com). Modelos de seguimiento de compás basados en [Beat This!](https://github.com/CPJKU/beat_this).

---

<div align="center">
  <sub>Desarrollado con dedicación bajo el sello de <b>AKASHI DEV</b> • Comunidad Veyra Music</sub>
</div>
