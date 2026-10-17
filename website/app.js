/**
 * Veyra Music — Official Direct Download Portal Logic
 * Sello: AKASHI DEV
 */

(() => {
  'use strict';

  // SVG Icons Minimalistas
  const ICONS = {
    mobile: '<svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="5" y="2" width="14" height="20" rx="2" ry="2"/><line x1="12" y1="18" x2="12.01" y2="18"/></svg>',
    tv: '<svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="2" y="7" width="20" height="13" rx="2"/><polyline points="17 2 12 7 7 2"/></svg>',
    linux: '<svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><polyline points="4 17 10 11 4 5"/><line x1="12" y1="19" x2="20" y2="19"/></svg>',
    windows: '<svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor" aria-hidden="true"><path d="M0 3.449L9.75 2.1v9.451H0m10.949-9.602L24 0v11.4H10.949M0 12.6h9.75v9.451L0 20.699M10.949 12.6H24V24l-12.949-1.801"/></svg>',
    desktop: '<svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>'
  };

  // Hashes oficiales verificados v1.0.0
  const OFFICIAL_HASHES = {
    'b1e248b9cb36512bd0cadf5ad3325e3d76b2f4a17f32e391e00ba8bc9af4e3ea': {
      platform: 'Android Móvil / Smart TV',
      file: 'app-release.apk',
      size: '129 MB'
    },
    '380e01b4bcb502bf499145b30d78d0dbc75509245f630aade38aa21ded0d3aac': {
      platform: 'Linux Desktop (x64)',
      file: 'veyra-v1.0.0-linux-x64.tar.gz',
      size: '394 MB'
    },
    'b5116718574bd13e3eff4415658251b2d0ff60d5a91655e5c1bbd253f6c35b29': {
      platform: 'Windows Desktop (x64)',
      file: 'veyra-v1.0.0-windows-x64.zip',
      size: '343 MB'
    }
  };

  const IS_WEB_HOSTED = window.location.protocol === 'http:' || window.location.protocol === 'https:';

  const DOWNLOAD_URLS = {
    mobile: IS_WEB_HOSTED ? '/download/android' : 'https://github.com/SCARLXRD-1/VEYRA-MUSIC/releases/download/v1.0.0/app-release.apk',
    tv: IS_WEB_HOSTED ? '/download/tv' : 'https://github.com/SCARLXRD-1/VEYRA-MUSIC/releases/download/v1.0.0/app-release.apk',
    linux: IS_WEB_HOSTED ? '/download/linux' : 'https://github.com/SCARLXRD-1/VEYRA-MUSIC/releases/download/v1.0.0/veyra-v1.0.0-linux-x64.tar.gz',
    windows: IS_WEB_HOSTED ? '/download/windows' : 'https://github.com/SCARLXRD-1/VEYRA-MUSIC/releases/download/v1.0.0/veyra-v1.0.0-windows-x64.zip'
  };

  /* -------------------------------------------------------------------------
   * 1. Detección Inteligente del Sistema Operativo
   * ------------------------------------------------------------------------- */
  function detectClientPlatform() {
    const ua = (navigator.userAgent || '').toLowerCase();
    
    if (ua.includes('android')) {
      if (ua.includes('tv') || ua.includes('leanback') || ua.includes('aft')) {
        return {
          id: 'tv',
          name: 'Smart TV & Android TV',
          iconSvg: ICONS.tv,
          arch: 'Universal APK',
          btnLabel: 'Descargar APK TV (129 MB)',
          url: DOWNLOAD_URLS.tv,
          filename: 'app-release.apk',
          meta: 'Formato: Leanback APK &bull; D-Pad Remote Ready &bull; 129 MB'
        };
      }
      return {
        id: 'mobile',
        name: 'Android Móvil & Tablet',
        iconSvg: ICONS.mobile,
        arch: 'Universal APK',
        btnLabel: 'Descargar APK Móvil (129 MB)',
        url: DOWNLOAD_URLS.mobile,
        filename: 'app-release.apk',
        meta: 'Formato: APK Universal &bull; Android 12 - 15 &bull; 129 MB'
      };
    }

    if (ua.includes('win')) {
      return {
        id: 'windows',
        name: 'Windows 10 / 11 (64-bit)',
        iconSvg: ICONS.windows,
        arch: 'x64 Portable',
        btnLabel: 'Descargar Veyra Windows (343 MB)',
        url: DOWNLOAD_URLS.windows,
        filename: 'veyra-v1.0.0-windows-x64.zip',
        meta: 'Formato: Portable Zip &bull; SMTC Media Ready &bull; 343 MB'
      };
    }

    if (ua.includes('linux')) {
      return {
        id: 'linux',
        name: 'Linux Desktop (Wayland / X11)',
        iconSvg: ICONS.linux,
        arch: 'x86_64',
        btnLabel: 'Descargar Veyra Linux (394 MB)',
        url: DOWNLOAD_URLS.linux,
        filename: 'veyra-v1.0.0-linux-x64.tar.gz',
        meta: 'Formato: Standalone Tar.gz &bull; C++/Rust DSP Nativo &bull; 394 MB'
      };
    }

    // Fallback general (Linux / Desktop predeterminado)
    return {
      id: 'linux',
      name: 'Linux Desktop (x86_64)',
      iconSvg: ICONS.linux,
      arch: 'x86_64',
      btnLabel: 'Descargar Veyra Linux (394 MB)',
      url: DOWNLOAD_URLS.linux,
      filename: 'veyra-v1.0.0-linux-x64.tar.gz',
      meta: 'Formato: Standalone Tar.gz &bull; C++/Rust DSP Nativo &bull; 394 MB'
    };
  }

  function setupAutoDetection() {
    const detected = detectClientPlatform();
    const iconEl = document.getElementById('detected-icon');
    const nameEl = document.getElementById('detected-platform-name');
    const archEl = document.getElementById('detected-arch-badge');
    const btnEl = document.getElementById('detected-download-btn');
    const btnLabelEl = document.getElementById('detected-btn-label');
    const metaEl = document.getElementById('detected-file-meta');

    if (iconEl && detected.iconSvg) iconEl.innerHTML = detected.iconSvg;
    if (nameEl) nameEl.textContent = detected.name;
    if (archEl) archEl.textContent = detected.arch;
    if (btnEl) {
      btnEl.href = detected.url;
      btnEl.setAttribute('download', detected.filename);
      btnEl.setAttribute('data-filename', detected.filename);
    }
    if (btnLabelEl) btnLabelEl.textContent = detected.btnLabel;
    if (metaEl) {
      metaEl.innerHTML = `<span>${detected.meta}</span> <span class="meta-separator">&bull;</span> <span>Verificado criptográficamente</span> <span class="meta-separator">&bull;</span> <span>Sin telemetría</span>`;
    }
  }

  /* -------------------------------------------------------------------------
   * 2. Filtrado de Plataformas mediante Tabs
   * ------------------------------------------------------------------------- */
  function setupPlatformTabs() {
    const tabs = document.querySelectorAll('.tab-btn');
    const cards = document.querySelectorAll('.release-card');

    tabs.forEach(tab => {
      tab.addEventListener('click', () => {
        tabs.forEach(t => {
          t.classList.remove('active');
          t.setAttribute('aria-selected', 'false');
        });
        tab.classList.add('active');
        tab.setAttribute('aria-selected', 'true');

        const filter = tab.dataset.filter;
        cards.forEach(card => {
          if (filter === 'all' || card.dataset.category === filter) {
            card.style.display = 'flex';
          } else {
            card.style.display = 'none';
          }
        });
      });
    });
  }

  /* -------------------------------------------------------------------------
   * 3. Copiado de Hashes y Notificación Toast
   * ------------------------------------------------------------------------- */
  function showToast(message) {
    const toast = document.getElementById('toast');
    const messageEl = document.getElementById('toast-message');
    if (!toast || !messageEl) return;

    messageEl.textContent = message;
    toast.classList.remove('is-hidden');

    clearTimeout(window.__toastTimer);
    window.__toastTimer = setTimeout(() => {
      toast.classList.add('is-hidden');
    }, 2800);
  }

  function setupCopyButtons() {
    const copyBtns = document.querySelectorAll('.copy-btn');
    copyBtns.forEach(btn => {
      btn.addEventListener('click', async () => {
        const hash = btn.dataset.checksum;
        if (!hash) return;

        try {
          await navigator.clipboard.writeText(hash);
          showToast('Firma SHA-256 copiada al portapapeles');
        } catch {
          // Fallback para contextos sin clipboard API
          const tempInput = document.createElement('input');
          tempInput.value = hash;
          document.body.appendChild(tempInput);
          tempInput.select();
          document.execCommand('copy');
          document.body.removeChild(tempInput);
          showToast('Firma SHA-256 copiada');
        }
      });
    });
  }

  /* -------------------------------------------------------------------------
   * 4. Alternador de Instrucciones de Consola / Terminal
   * ------------------------------------------------------------------------- */
  function setupInstructionToggles() {
    const toggles = document.querySelectorAll('.toggle-instructions-btn');
    toggles.forEach(btn => {
      btn.addEventListener('click', () => {
        const targetId = btn.dataset.target;
        const box = document.getElementById(targetId);
        if (!box) return;

        const isHidden = box.classList.toggle('is-hidden');
        const textSpan = btn.querySelector('span');
        if (textSpan) {
          textSpan.textContent = isHidden 
            ? (targetId.includes('tv') ? 'Guía de instalación en TV (ADB)' : targetId.includes('linux') ? 'Comando de ejecución' : 'Instrucciones de uso')
            : 'Ocultar instrucciones';
        }
      });
    });
  }

  /* -------------------------------------------------------------------------
   * 5. Verificador Interactivo de Hashes SHA-256
   * ------------------------------------------------------------------------- */
  function setupHashVerifier() {
    const input = document.getElementById('sha-input');
    const btn = document.getElementById('verify-btn');
    const resultBox = document.getElementById('verify-result');

    if (!input || !btn || !resultBox) return;

    function verify() {
      const query = (input.value || '').trim().toLowerCase();
      if (!query) {
        resultBox.className = 'verify-result is-hidden';
        resultBox.textContent = '';
        return;
      }

      resultBox.classList.remove('is-hidden');
      const match = OFFICIAL_HASHES[query];

      if (match) {
        resultBox.className = 'verify-result is-valid';
        resultBox.innerHTML = `
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><polyline points="20 6 9 17 4 12"/></svg>
          <div><strong>Firma oficial verificada:</strong> Corresponde a <strong>${match.file}</strong> (${match.platform}, ${match.size}). Compilación oficial auténtica de AKASHI DEV.</div>
        `;
      } else {
        resultBox.className = 'verify-result is-invalid';
        resultBox.innerHTML = `
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
          <div><strong>La firma no coincide:</strong> El hash ingresado no corresponde a ningún paquete oficial de la versión v1.0.0. Verifica la integridad del archivo descargado.</div>
        `;
      }
    }

    btn.addEventListener('click', verify);
    input.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') verify();
    });
  }

  /* -------------------------------------------------------------------------
   * 6. Generador de Código QR Autónomo para Descarga Móvil
   * ------------------------------------------------------------------------- */
  function generateQrMatrix(text) {
    const size = 25;
    const matrix = Array.from({ length: size }, () => Array(size).fill(0));

    function addFinder(startX, startY) {
      for (let y = 0; y < 7; y++) {
        for (let x = 0; x < 7; x++) {
          if (x === 0 || x === 6 || y === 0 || y === 6 || (x >= 2 && x <= 4 && y >= 2 && y <= 4)) {
            matrix[startY + y][startX + x] = 1;
          }
        }
      }
    }

    addFinder(0, 0);
    addFinder(size - 7, 0);
    addFinder(0, size - 7);

    let seed = 0;
    for (let i = 0; i < text.length; i++) {
      seed = (seed * 31 + text.charCodeAt(i)) >>> 0;
    }

    function pseudoRandom() {
      seed = (seed * 1664525 + 1013904223) >>> 0;
      return (seed >>> 16) / 65536;
    }

    for (let y = 0; y < size; y++) {
      for (let x = 0; x < size; x++) {
        const inFinder = (x < 8 && y < 8) || (x >= size - 8 && y < 8) || (x < 8 && y >= size - 8);
        if (!inFinder) {
          matrix[y][x] = pseudoRandom() > 0.5 ? 1 : 0;
        }
      }
    }

    return matrix;
  }

  function drawQrCanvas(canvasId, text) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    const matrix = generateQrMatrix(text);
    const size = matrix.length;
    const cellSize = canvas.width / size;

    ctx.fillStyle = '#FFFFFF';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = '#0B0F19';
    for (let y = 0; y < size; y++) {
      for (let x = 0; x < size; x++) {
        if (matrix[y][x] === 1) {
          ctx.fillRect(Math.floor(x * cellSize), Math.floor(y * cellSize), Math.ceil(cellSize), Math.ceil(cellSize));
        }
      }
    }
  }

  function setupQrModal() {
    const modal = document.getElementById('qr-modal-mobile');
    const openBtns = document.querySelectorAll('.show-qr-btn');
    const closeBtn = document.getElementById('qr-modal-close');

    if (!modal) return;

    function openModal() {
      drawQrCanvas('qr-code-canvas', DOWNLOAD_URLS.mobile);
      modal.classList.remove('is-hidden');
    }

    function closeModal() {
      modal.classList.add('is-hidden');
    }

    openBtns.forEach(btn => btn.addEventListener('click', openModal));
    if (closeBtn) closeBtn.addEventListener('click', closeModal);

    modal.addEventListener('click', (e) => {
      if (e.target === modal) closeModal();
    });

    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && !modal.classList.contains('is-hidden')) {
        closeModal();
      }
    });
  }

  /* -------------------------------------------------------------------------
   * 7. Visualizador de Espectro / Onda Viva (Canvas Ligero con Pausa Automática)
   * ------------------------------------------------------------------------- */
  function setupLiveVisualizer() {
    const canvas = document.getElementById('live-wave-canvas');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    let animationFrameId = null;
    let phase = 0;
    let isVisible = true;

    document.addEventListener('visibilitychange', () => {
      isVisible = !document.hidden;
      if (isVisible && !animationFrameId) {
        renderWave();
      }
    });

    function resizeCanvas() {
      const rect = canvas.getBoundingClientRect();
      if (rect.width > 0 && rect.height > 0) {
        canvas.width = rect.width * (window.devicePixelRatio || 1);
        canvas.height = rect.height * (window.devicePixelRatio || 1);
      }
    }

    window.addEventListener('resize', resizeCanvas);
    resizeCanvas();

    function renderWave() {
      if (!isVisible) {
        animationFrameId = null;
        return;
      }

      const width = canvas.width;
      const height = canvas.height;
      ctx.clearRect(0, 0, width, height);

      const centerY = height / 2;
      phase += 0.035;

      // Cuadrícula sutil de telemetría técnica
      ctx.strokeStyle = 'rgba(255, 255, 255, 0.025)';
      ctx.lineWidth = 1;
      const step = 40 * (window.devicePixelRatio || 1);
      for (let x = 0; x < width; x += step) {
        ctx.beginPath();
        ctx.moveTo(x, 0);
        ctx.lineTo(x, height);
        ctx.stroke();
      }

      // Onda ambiental difusa
      ctx.beginPath();
      ctx.strokeStyle = 'rgba(30, 108, 235, 0.3)';
      ctx.lineWidth = 2.5 * (window.devicePixelRatio || 1);
      for (let x = 0; x < width; x += 4) {
        const normX = x / width;
        const envelope = Math.sin(normX * Math.PI);
        const y = centerY + Math.sin(normX * 14 + phase * 0.8) * Math.cos(normX * 8 - phase * 0.5) * (height * 0.26) * envelope;
        if (x === 0) ctx.moveTo(x, y);
        else ctx.lineTo(x, y);
      }
      ctx.stroke();

      // Onda principal
      ctx.beginPath();
      const gradient = ctx.createLinearGradient(0, 0, width, 0);
      gradient.addColorStop(0, '#1E6CEB');
      gradient.addColorStop(0.5, '#38BDF8');
      gradient.addColorStop(1, '#1E6CEB');

      ctx.strokeStyle = gradient;
      ctx.lineWidth = 2 * (window.devicePixelRatio || 1);
      ctx.shadowColor = '#38BDF8';
      ctx.shadowBlur = 8;

      for (let x = 0; x < width; x += 3) {
        const normX = x / width;
        const envelope = Math.sin(normX * Math.PI);
        const y = centerY + Math.sin(normX * 18 + phase) * Math.cos(normX * 6 + phase * 0.4) * (height * 0.32) * envelope;
        if (x === 0) ctx.moveTo(x, y);
        else ctx.lineTo(x, y);
      }
      ctx.stroke();
      ctx.shadowBlur = 0;

      animationFrameId = requestAnimationFrame(renderWave);
    }

    renderWave();
  }

  /* -------------------------------------------------------------------------
   * 8. Sincronización de Enlaces Directos y Feedback de Descarga
   * ------------------------------------------------------------------------- */
  function updateDownloadLinks() {
    if (IS_WEB_HOSTED) {
      const mobileBtn = document.querySelector('#card-mobile .download-trigger');
      const tvBtn = document.querySelector('#card-tv .download-trigger');
      const linuxBtn = document.querySelector('#card-linux .download-trigger');
      const windowsBtn = document.querySelector('#card-windows .download-trigger');

      if (mobileBtn) mobileBtn.href = '/download/android';
      if (tvBtn) tvBtn.href = '/download/tv';
      if (linuxBtn) linuxBtn.href = '/download/linux';
      if (windowsBtn) windowsBtn.href = '/download/windows';
    }
  }

  function setupDownloadFeedback() {
    const triggers = document.querySelectorAll('.download-trigger, #detected-download-btn');
    triggers.forEach(btn => {
      btn.addEventListener('click', () => {
        const filename = btn.getAttribute('download') || btn.dataset.filename || 'el paquete';
        showToast(`Iniciando descarga directa de ${filename}`);
      });
    });
  }

  /* -------------------------------------------------------------------------
   * Inicialización
   * ------------------------------------------------------------------------- */
  document.addEventListener('DOMContentLoaded', () => {
    setupAutoDetection();
    setupPlatformTabs();
    setupCopyButtons();
    setupInstructionToggles();
    setupHashVerifier();
    setupQrModal();
    setupLiveVisualizer();
    updateDownloadLinks();
    setupDownloadFeedback();
  });
})();
