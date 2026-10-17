# 🚀 Veyra Music — Catálogo y Centro de Distribución de Releases

Bienvenido al centro oficial de empaquetado y distribución de **Veyra Music** desarrollado bajo el sello **AKASHI DEV**.

Este directorio organiza las versiones de producción, especificaciones técnicas, checksums criptográficos e instrucciones de despliegue para cada una de las plataformas soportadas:

---

## 📂 Estructura del Directorio

```text
releases/
├── mobile/      # Cliente Android para Smartphones, Tablets y Plegables (APK)
├── desktop/     # Clientes de Escritorio (Linux AppImage/deb, Windows, macOS)
└── tv/          # Cliente Android TV / Google TV / Fire TV (Leanback APK)
```

---

## 🎯 Plataformas y Canales de Distribución

| Plataforma | Canal / Formato | Versión Actual | Estado |
| :--- | :--- | :--- | :--- |
| **Móvil (Android)** | APK Universal / GitHub Releases | `1.0.0` | **Listo para Producción** |
| **Escritorio (Linux/Win/Mac)** | AppImage, Deb, Exe, Dmg | `1.0.0` | **En optimización de build** |
| **Smart TV (Android TV)** | APK Leanback (D-Pad) | `1.0.0-tv` | **Planificación y pruebas** |

---

## 🔒 Verificación Criptográfica

Para garantizar la integridad y seguridad de los binarios distribuidos por AKASHI DEV, verifique siempre el hash SHA-256 antes de instalar o desplegar cualquier compilación:

```bash
sha256sum <archivo-de-release>
```

Consulte el archivo `README.md` dentro de cada subdirectorio para verificar los hashes oficiales de cada versión.
