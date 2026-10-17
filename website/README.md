# Veyra Music — Portal Web Oficial de Descargas Directas

**Sello:** AKASHI DEV  
**Diseño:** Enterprise Slate & Obsidian Industrial  
**Tecnología:** HTML5 semántico, Vanilla CSS3 y ES6 (Zero dependencias, carga instantánea, 0 MB extra de RAM).

---

## Capacidades del Portal

1. **Detección Automática de Sistema Operativo:**
   - Detecta si el visitante navega desde **Android Móvil**, **Smart TV / Android TV**, **Linux x64** o **Windows 10/11**.
   - Resalta un botón Hero con la versión exacta recomendada, tamaño y formato.

2. **Matriz de Descargas Multiplataforma:**
   - **Android Móvil:** APK Universal v1.0.0 (129 MB) + Generador de código QR para escanear y descargar en el teléfono.
   - **Smart TV:** APK Leanback v1.0.0 (129 MB) + Instrucciones rápidas de instalación vía ADB por red.
   - **Linux Desktop:** Standalone Tar.gz v1.0.0 (394 MB) + Comando terminal de descompresión y ejecución inmediata.
   - **Windows Desktop:** Portable Zip v1.0.0 (343 MB) + Instrucciones para ejecutar sin instalación.

3. **Verificador Criptográfico SHA-256 en Vivo:**
   - Herramienta interactiva para validar en el navegador la integridad de cualquier archivo descargado comparándolo con las firmas auténticas de AKASHI DEV.

4. **Visualizador de Espectro / Onda Viva:**
   - Simulación en Canvas de la onda sinusoidal y telemetría DSP con pausa automática al cambiar de pestaña (`visibilitychange`) para preservar la batería y memoria del sistema.

---

## Cómo Probar o Servir Localmente

Para visualizar el portal en tu navegador:

```bash
# Opción 1: Abrir directamente el archivo en tu navegador preferido
xdg-open website/index.html

# Opción 2: Usar cualquier servidor estático ligero en puerto 8080
npm run serve:website
```

---

## Despliegue en la Nube

El directorio `website/` es totalmente autónomo y estático. Puede desplegarse en un solo clic en:
- **Cloudflare Pages:** Establecer el directorio raíz de salida en `website`.
- **GitHub Pages:** Configurar la rama `main` y directorio `/website` (o rama `gh-pages`).
- **Vercel / Netlify:** Despliegue estático sin necesidad de paso de build.
