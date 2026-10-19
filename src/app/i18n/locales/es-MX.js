/**
 * Veyra Music — Locales: Español (es-MX)
 * AKASHI DEV — Grado Profesional B2B / Power-User
 */

export default {
  common: {
    appName: 'Veyra Music',
    signIn: 'Iniciar sesión',
    signOut: 'Cerrar sesión',
    signedIn: 'Sesión iniciada',
    signedOut: 'Sesión cerrada',
    continue: 'Continuar',
    next: 'Siguiente',
    previous: 'Anterior',
    back: 'Atrás',
    cancel: 'Cancelar',
    close: 'Cerrar',
    save: 'Guardar',
    done: 'Listo',
    confirm: 'Confirmar',
    search: 'Buscar',
    settings: 'Ajustes',
    support: 'Soporte',
    retry: 'Reintentar',
    loading: 'Cargando…',
    offline: 'Sin conexión',
    online: 'En línea',
    openLink: 'Abrir enlace',
    copyCode: 'Copiar código',
    copied: 'Copiado al portapapeles',
    on: 'Activado',
    off: 'Desactivado',
    status: 'Estado',
    minimize: 'Minimizar',
    maximize: 'Maximizar'
  },

  welcome: {
    steps: {
      account: 'Bienvenido a Veyra Music',
      layout: 'Diseño de interfaz',
      immersive: 'Fondo de portada',
      sound: 'Ajustes de reproducción',
      connected: 'Mantente conectado',
      finish: 'Listo para escuchar',
      canopyUpgrade: 'Un nuevo diseño para Veyra Music'
    },
    account: {
      title: 'Bienvenido a Veyra Music',
      description: 'Inicia sesión primero. Veyra Music mantendrá el reproductor principal en espera hasta que tu configuración esté lista.',
      signInButton: 'Iniciar sesión en YouTube Music',
      openingSignIn: 'Abriendo inicio de sesión…',
      signedInAs: 'Conectado como {name}',
      deviceInstructions: 'Ingresa este código en tu navegador para autorizar la cuenta:'
    },
    layout: {
      title: 'Diseño de interfaz',
      canopyDescription: 'Veyra Music incorpora Obsidian — Slate Industrial, una arquitectura suspendida con consola en cabecera y navegación flotante. Grove — Neumórfico sigue disponible como explorador clásico.',
      description: 'Elige el estilo de interfaz que mejor se adapte a tu flujo de trabajo.',
      groveLabel: 'Grove — Neumórfico',
      groveDesc: 'Explorador clásico en dos columnas con reproductor integrado a sangre completa.',
      canopyLabel: 'Obsidian — Slate Industrial',
      canopyDesc: 'Muelle de audio suspendido de precisión industrial con consola en cabecera y navegación modular flotante.'
    },
    immersive: {
      title: 'Fondo de portada',
      description: 'Elige si Veyra Music debe proyectar el arte del álbum en reproducción tras la interfaz.',
      toggleLabel: 'Fondos inmersivos reactivos',
      motion: {
        static: 'Estático',
        pan: 'Paneo suave',
        dynamic: 'Dinámico'
      }
    },
    sound: {
      title: 'Ajustes de reproducción',
      description: 'Personaliza cómo fluyen las canciones y la transición entre pistas.',
      offTitle: 'Sin crossfade',
      offDesc: 'Mantiene las canciones separadas sin solapamiento.',
      smartTitle: 'Smart Crossfade',
      smartDesc: 'Detección inteligente de beats con WSOLA; evita solapar directos o pistas habladas.',
      standardTitle: 'Estándar',
      standardDesc: 'Fundido cruzado clásico de volumen al final de cada canción.',
      autoplayLabel: 'Reproducción continua (Autoplay)',
      autoEqLabel: 'Ecualización automática (Auto-EQ)',
      durationLabel: 'Duración del crossfade'
    },
    connected: {
      title: 'Mantente conectado',
      description: 'Controla qué información sincroniza Veyra Music fuera de esta ventana.',
      discordLabel: 'Presencia enriquecida en Discord (RPC)',
      pairPhone: 'Vincular teléfono',
      bridgeStatus: 'Puente LAN: {status}'
    },
    finish: {
      title: 'Listo para escuchar',
      description: 'Tu configuración ha sido guardada. Abre Veyra Music y comienza a disfrutar.',
      canopyActive: 'Diseño Obsidian — Slate Industrial',
      groveActive: 'Grove — Neumórfico',
      backgroundsOn: 'Fondos inmersivos ({motion})',
      backgroundsOff: 'Fondos inmersivos desactivados',
      autoEqActive: 'Auto-EQ activo',
      manualEqActive: 'EQ manual',
      eqOff: 'EQ apagado',
      devicesCount: '{count} dispositivos vinculados',
      blockedAudioNote: 'Tu sistema bloqueó la reproducción automática de bienvenida. Usa el botón de sonido superior para iniciarla.'
    },
    actions: {
      openApp: 'Abrir Veyra Music',
      openingApp: 'Abriendo Veyra Music…',
      soundStart: 'Iniciar música de bienvenida',
      soundMute: 'Silenciar música de bienvenida',
      soundUnmute: 'Reactivar música de bienvenida'
    }
  },

  nav: {
    home: 'Inicio',
    discover: 'Descubrir',
    explore: 'Explorar',
    search: 'Buscar',
    searchPlaceholder: 'Escribe o presiona / para buscar',
    library: 'Tu biblioteca',
    nowPlaying: 'Reproduciendo',
    connect: 'Conectar',
    settings: 'Ajustes',
    support: 'Soporte',
    about: 'Acerca de',
    new: 'Novedades',
    radio: 'Radio',
    concerts: 'Conciertos',
    queue: 'Cola de reproducción',
    recentlyAdded: 'Añadido recientemente',
    replay: 'Replay anual',
    playlists: 'Listas de reproducción',
    songs: 'Canciones',
    albums: 'Álbumes',
    artists: 'Artistas',
    podcasts: 'Pódcasts',
    pins: 'Fijados',
    lyrics: 'Letras',
    groups: {
      youtubeMusic: 'YouTube Music',
      library: 'Biblioteca',
      pins: 'Fijados'
    }
  },

  supportUi: {
    reports: 'Tus reportes', currentIssues: 'Problemas actuales', refreshReports: 'Actualizar reportes', newReport: 'Nuevo reporte',
    noReports: 'Todavía no hay reportes.', reportDescription: 'La descripción se publica en el rastreador público de incidencias de Veyra Music. Las respuestas, los diagnósticos y las imágenes permanecen privados.',
    type: 'Tipo', bug: 'Error', featureRequest: 'Solicitud de función', artistPageRequest: 'Solicitud de página de artista', feedback: 'Comentarios',
    title: 'Título', shortSummary: 'Un resumen breve', whatHappened: '¿Qué ocurrió?',
    describeExpected: 'Cuéntanos qué esperabas y qué hizo Veyra Music.', chooseImage: 'Elegir imagen', captureVeyra: 'Capturar Veyra Music',
    imageHelp: 'Pega o suelta una imagen aquí, de hasta 5 MiB.', screenshotPreview: 'Vista previa de la captura', remove: 'Quitar',
    includeDiagnostics: 'Incluir diagnósticos anonimizados', reviewDiagnostics: 'Revisar diagnósticos', preparingAttachment: 'Preparando el archivo adjunto privado…',
    diagnosticsNotice: 'Solo se adjuntarán a este reporte los datos que aparecen a continuación.', refresh: 'Actualizar', githubAttribution: 'Atribución de GitHub',
    issuePostedAs: 'La incidencia pública se publicará como', enterGithubCode: 'Ingresa {code} en la página de GitHub que se abrió en tu navegador.',
    connectGithubHelp: 'Conecta GitHub para mostrar tu cuenta como autora de la incidencia pública.', disconnect: 'Desconectar', connectGithub: 'Conectar GitHub',
    privacyNotice: 'Nunca se publican cookies, encabezados de autenticación, URL de reproducción, diagnósticos, capturas ni identificadores de cuentas de música.',
    sendReport: 'Enviar reporte', reportsBack: 'Reportes', publicIssue: 'Incidencia pública', delete: 'Eliminar', you: 'Tú',
    replyPlaceholder: 'Responder a Soporte de Veyra', replyScreenshotPreview: 'Vista previa de la captura de respuesta', attachImage: 'Adjuntar imagen', sendReply: 'Enviar respuesta',
    closedReport: 'Este reporte está cerrado. Tus mensajes seguirán disponibles hasta que lo elimines o termine el periodo de conservación de un año.',
    welcomeTitle: 'Soporte de Veyra', welcomeDescription: 'Reporta un problema, sugiere una mejora o continúa una conversación privada con el equipo de soporte.',
    createReport: 'Crear un reporte'
  },

  ui: {
    feedback: { offlineDownloaded: 'Veyra Music está sin conexión. Se muestran las canciones descargadas.', downloadedOffline: 'Se muestra la música descargada porque Veyra Music está sin conexión.', offlineDownloadUnavailable: 'No se pudo leer una canción descargada. Se omitió y continuará la siguiente.', noRecommendations: 'No se encontraron más recomendaciones.', autoplayUnavailable: 'No se pudieron cargar las recomendaciones automáticas.', playbackStalled: 'La reproducción se detuvo al cargar esta transmisión.', phonePlaybackFailed: 'El teléfono seleccionado no pudo recibir la reproducción.', eventDateTba: 'Fecha por anunciar', locationUnavailable: 'Este sistema no tiene acceso a la ubicación actual.', githubSignInFinish: 'Termina de iniciar sesión en GitHub desde el navegador y vuelve a Veyra Music.', githubDisconnected: 'Se desconectó GitHub de Soporte de Veyra.', reportDeleted: 'Se eliminaron el reporte y la conversación.' },
    analysis: { checkingCloud: 'Buscando análisis en la caché de nube…', allCached: 'Las {count} canciones ya tienen análisis en la nube.', analyzerUnavailable: 'El analizador de audio local no está disponible.', analyzing: 'Analizando audio ({completed}/{total})…', completed: 'Listo. Se analizaron {count} canciones ({cached} desde la nube y {processed} nuevas).', failed: 'El análisis terminó inesperadamente.' },
    timer: { pauseIn: 'Veyra Music se pausará en {time}.', pauseAtEnd: 'Veyra Music se pausará al terminar esta canción.', hint: 'Pausa la reproducción después de cierto tiempo o al terminar la canción actual.' },
    cacheMessages: { noCacheableTracks: 'La cola no tiene canciones de audio que se puedan guardar en caché.', cachingOne: 'Guardando 1 canción en caché…', cachingMany: 'Guardando {count} canciones en caché…', cachedOne: 'Se guardó {count} canción en caché.', cachedMany: 'Se guardaron {count} canciones en caché.', cachedFailed: 'Se guardaron {cached} de {total}. Fallaron {failed}.', noDownloads: 'No hay canciones descargables aquí.', alreadyDownloaded: 'Estas canciones ya están descargadas.', downloadingOne: 'Descargando 1 canción…', downloadingMany: 'Descargando {count} canciones…', downloadedOne: 'Se descargó {count} canción para escuchar sin conexión.', downloadedMany: 'Se descargaron {count} canciones para escuchar sin conexión.', downloadsFailed: 'Se descargaron {downloaded} de {total}. Fallaron {failed}.', downloadReady: 'Lista para escuchar sin conexión', downloadUnavailable: 'Archivo faltante o incompleto', loadingPlaylist: 'Cargando la lista de reproducción completa…', removedCached: 'Se quitó la canción de la caché.', cacheCleared: 'Se vació la caché de Replay. Se conservaron las descargas sin conexión.' },
    lyrics: { pickTrack: 'Elige una canción para cargar las letras', loading: 'Cargando letras', unsynced: 'Letras sin sincronizar', wordSynced: 'Letras sincronizadas por palabra', synced: 'Letras sincronizadas', unavailable: 'Letras no disponibles' },
    readiness: {
      never: 'Nunca', onePhonePaired: '1 teléfono vinculado.', ready: 'Listo', needsAttention: 'Requiere atención', review: 'Revisar',
      signinTitle: 'Iniciar sesión en YouTube Music', accountConnected: 'Conectado a tu cuenta de música.', accountRequired: 'Se requiere para usar la biblioteca, la búsqueda y la reproducción.', updatesTitle: 'Buscar actualizaciones', updatesDetail: 'Verifica que la aplicación pueda consultar los datos de versiones.', appearanceTitle: 'Revisar apariencia', appearanceDetail: 'Puedes ajustar el tema, el color de acento y el movimiento del fondo.', audioTitle: 'Configurar el motor de audio', autoEqReady: 'La ecualización automática se adaptará mientras se reproducen las canciones.', custom: 'Personalizado', eqActive: 'Ecualizador {preset} activo.', chooseEq: 'Elige ecualización automática, un preajuste manual o reproducción directa.', connectTitle: 'Configurar Veyra Connect', pairedPhones: '{count} teléfonos vinculados.', pairPhone: 'Genera un código QR y vincula un teléfono en tu red local.', discordTitle: 'Revisar presencia de Discord', discordEnabled: 'La presencia enriquecida de Discord está activada.', discordOff: 'La presencia enriquecida de Discord está desactivada.', backupTitle: 'Crear copia de seguridad', lastBackup: 'Última copia: {time}',
      signInBeforeOpen: 'Inicia sesión antes de abrir Veyra Music.', desktopBridgeUnavailable: 'El puente de escritorio no está disponible.', cannotOpenApp: 'No se pudo abrir Veyra Music.', welcomeUnavailable: 'La ventana de bienvenida de escritorio no está disponible.', reopenWelcomeFailed: 'No se pudo volver a abrir la configuración inicial.',
      desktopBridge: 'Puente de escritorio', youtubeAccount: 'Cuenta de YouTube Music', updater: 'Actualizador', veyraConnect: 'Veyra Connect', discordPresence: 'Presencia enriquecida de Discord', localStorage: 'Almacenamiento local', audioEngine: 'Motor de audio', audioFormats: 'Formatos de audio', videoFormats: 'Formatos de video', signedIn: 'Sesión iniciada', waitingBridge: 'Esperando el estado del puente', enabled: 'Activado', disabled: 'Desactivado', automaticEq: 'Ecualización automática activa', manualEq: 'Ecualizador manual activo', direct: 'Modo directo', bypassed: 'Desactivado', outputOptions: '{count} opciones de salida', oneOutputOption: '1 opción de salida', noReportedSupport: 'Sin compatibilidad reportada',
      diagnosticsRefreshed: 'Diagnósticos actualizados.', reportCopied: 'Informe de diagnóstico copiado.', copyFailed: 'No se pudo copiar. Exporta el informe.', backupDownloaded: 'Copia de seguridad descargada.', backupRestored: 'Copia restaurada. Reinicia Veyra Music para volver a cargar la cola y el historial guardados.'
    },
    pages: { signIn: 'Iniciar sesión', more: 'Más', pins: 'Fijados', queue: 'Cola', history: 'Reproducido recientemente', replay: 'Replay', releaseRadar: 'Radar de estrenos', podcasts: 'Podcasts', search: 'Buscar', support: 'Soporte', settings: 'Ajustes', home: 'Inicio', accountRequired: 'Se requiere una cuenta de YouTube Music', countFrom: '{count} elementos de {source}', upcomingQueue: 'Próximas canciones e historial de escucha', playedOnDevice: 'Canciones reproducidas en este equipo', pinnedDescription: 'Canciones que quieres tener a la mano', replayDescription: 'Tus estadísticas locales de escucha', radarDescription: 'Álbumes nuevos y próximos de artistas que sigues', podcastsDescription: 'Programas y episodios de YouTube Music', searchDescription: 'Busca resultados en el puente InnerTube', supportDescription: 'Reportes privados y respuestas de Soporte de Veyra', settingsDescription: 'Ajustes de reproducción, apariencia y aplicación', homeDescription: 'Tu biblioteca, mezclas y selecciones recientes', loadLibrary: 'Inicia sesión para cargar tu biblioteca de YouTube Music' },
    itemTypes: { upcomingAlbum: 'Próximo álbum', podcast: 'Podcast', episode: 'Episodio', playlist: 'Lista de reproducción', artist: 'Artista', song: 'Canción', video: 'Video', liveEvent: 'Evento en vivo', album: 'Álbum', ep: 'EP', single: 'Sencillo' },
    smartMix: { label: 'Smart Crossfade: {from} a {to}', handoff: 'Transición', mixing: 'Mezclando', beatHandoff: 'Transición de {count} tiempos', tempo: 'Tempo {value}%', phraseAligned: 'Frase alineada' },
    rightPanel: { controls: 'Controles del panel lateral', view: 'Vista del panel lateral', openQueue: 'Abrir cola', queueCount: 'Abrir cola, {count} canciones', openLyrics: 'Abrir letras', openSettings: 'Abrir ajustes', closeNowPlaying: 'Cerrar panel de reproducción', nowPlaying: 'En reproducción' },
    account: { title: 'Cuenta y sincronización de Veyra Cloud', signedInAs: 'Sesión iniciada como {email}. El análisis de listas realizado en PC se sincronizará con tus dispositivos móviles.', signInHelp: 'Inicia sesión o crea una cuenta para sincronizar los metadatos de análisis de audio entre tu PC y tus dispositivos móviles.', configureHelp: 'Configura tu instancia de Supabase Cloud para habilitar la sincronización de la cuenta.', disclaimer: 'Los metadatos del análisis de audio (BPM, tonalidad, tiempos fuertes y puntos de entrada) se comparten públicamente con la caché de Veyra Cloud mediante el ID del video. No se incluyen el historial de escucha personal, las listas de reproducción ni información que te identifique.', email: 'Correo electrónico', password: 'Contraseña', signIn: 'Iniciar sesión', createAccount: 'Crear cuenta', hideCloudSettings: 'Ocultar ajustes de nube', customSupabase: 'Configuración personalizada de Supabase', supabaseUrl: 'https://tu-proyecto.supabase.co', supabaseKey: 'Clave pública anónima de Supabase', saveCloudSettings: 'Guardar ajustes de nube', signOut: 'Cerrar sesión' },
    accessibility: {
      startingSignIn: 'Iniciando sesión', skipNonMusic: 'Omitir sección sin música', skipIntro: 'Omitir introducción', skipOutro: 'Omitir cierre', skip: 'Omitir',
      explicit: 'Contenido explícito', searchApp: 'Buscar en Veyra Music', searchItems: 'Buscar canciones, álbumes, artistas y páginas', closeSearch: 'Cerrar búsqueda', searchResults: 'Resultados de búsqueda', playAlbum: 'Reproducir álbum', addAlbumQueue: 'Agregar álbum a la cola', pinLeadTrack: 'Fijar canción principal', openAlbum: 'Abrir álbum', fullscreenLyrics: 'Letras', about: 'Acerca de Veyra Music', closeAbout: 'Cerrar ventana Acerca de', signInYoutube: 'Iniciar sesión en YouTube Music', minimize: 'Minimizar', maximize: 'Maximizar', close: 'Cerrar', share: 'Compartir {item}', resizeVideo: 'Cambiar tamaño del video', expandVideo: 'Ampliar video', minimizeVideo: 'Minimizar video', finishSignIn: 'Termina de iniciar sesión para continuar.', signedOut: 'Sesión cerrada.',
    },
    integrations: {
      spotifyConnectedMessage: 'Conectado a Spotify.', spotifyLoginClosed: 'Se cerró el inicio de sesión.', spotifyLoginFailed: 'No se pudo iniciar sesión.', spotifyDisconnected: 'Se desconectó Spotify.', spotifyDisconnectFailed: 'No se pudo desconectar.', spotifyCookieSaved: 'Se guardó la cookie sp_dc de Spotify.', spotifyInvalidCookie: 'La cookie no es válida.', spotifyCookieFailure: 'No se pudo guardar la cookie.', qobuzStatusUnavailable: 'No se pudo cargar el estado de Qobuz.', qobuzConnectedPersistent: 'Qobuz está conectado y activado.', qobuzConnectedTemporary: 'Qobuz está conectado durante esta sesión. El almacenamiento seguro de credenciales no está disponible.', qobuzLoginClosed: 'Se cerró el inicio de sesión sin conectar.', qobuzLoginFailed: 'No se pudo iniciar sesión en Qobuz.', qobuzDisconnected: 'Se desconectó Qobuz y se borraron los datos de inicio de sesión locales.', qobuzDisconnectFailed: 'No se pudo desconectar Qobuz.', qobuzBestAvailable: 'Mejor disponible', qobuzLossless: 'Calidad de CD sin pérdida', qobuzHiRes: 'Alta resolución',
      spotifyTitle: 'Integración con Spotify Canvas', spotifyConnected: 'Conectado a Spotify. Se descargarán portadas animadas de Canvas (.mp4) cuando estén disponibles.', spotifyHelp: 'Inicia sesión en Spotify o proporciona tu cookie sp_dc para buscar videos de Spotify Canvas cuando otros proveedores no los encuentren.', disconnect: 'Desconectar', spotifyLogin: 'Iniciar sesión en Spotify', cancel: 'Cancelar', enterCookie: 'Ingresar cookie', cookiePlaceholder: 'Pega aquí la cadena de la cookie sp_dc…', save: 'Guardar',
      qobuzTitle: 'Reproducción sin pérdida con Qobuz', qobuzConnected: 'Usa tu suscripción de Qobuz como fuente opcional sin pérdida. Veyra Music mantiene el catálogo de YouTube Music y vuelve a esa fuente si no puede identificar la misma grabación.', qobuzHelp: 'Conecta tu suscripción de Qobuz para añadir reproducción sin pérdida y Hi-Res. Usa la API web privada de Qobuz y podría requerir mantenimiento si cambia su reproductor web.', enabled: 'Activado', useQobuz: 'Usar Qobuz para canciones coincidentes', connectQobuz: 'Conectar Qobuz', connecting: 'Conectando…', qobuzQuality: 'Calidad de Qobuz', qobuzQualityHelp: 'La mejor calidad solicita hasta 24 bits/192 kHz; Qobuz ofrece el mejor máster permitido por tu plan.',
      lastfmTitle: 'Scrobbling de Last.fm', lastfmSending: 'Enviando actualizaciones de reproducción y escuchas completas como {user}.', lastfmApprove: 'Autoriza Veyra Music en Last.fm y luego termina la conexión.', lastfmHelp: 'Conecta una cuenta para agregar tu historial de escucha de Veyra Music a Last.fm.', secureStorageUnavailable: 'El almacenamiento seguro de credenciales no está disponible; la conexión durará hasta que cierres Veyra Music.', finishConnection: 'Terminar conexión',
      diagnostics: 'Diagnósticos', lastRefreshed: 'Última actualización: {time}', checkBeforeRelease: 'Revisa Veyra Music antes de publicarla o contactar a soporte.', refresh: 'Actualizar', copyReport: 'Copiar informe', noDiagnostics: 'Todavía no se recopilaron diagnósticos.', setup: 'Configuración inicial', setupReady: '{progress} listo para Veyra Music', finishSetup: 'Terminar configuración', refreshDiagnostics: 'Actualizar diagnósticos', setupComplete: 'Configuración completa', reopenChecklist: 'Vuelve a abrir esta lista cuando quieras antes de empaquetar o cambiar de equipo.', reopen: 'Volver a abrir'
    },
    browse: {
      playCollection: 'Reproducir {title}', shuffleCollection: 'Reproducir {title} en aleatorio', downloadFor: 'Descargar {title}', downloadingFor: 'Descargando {title}', removeDownloadFor: 'Quitar descarga de {title}',
      descriptionActionFor: '{action} de {title}', podcast: 'Podcast', playlist: 'Lista de reproducción', downloadingEllipsis: 'Descargando…', removeDownloadButton: 'Quitar descarga', saving: 'Guardando…', subscribeButton: 'Seguir', unsubscribeButton: 'Dejar de seguir', cancel: 'Cancelar',
      aboutTitle: 'Acerca de {title}', readBio: 'Leer biografía', readDescription: 'Leer descripción completa',
      loading: 'Cargando colección…', play: 'Reproducir', shuffle: 'Aleatorio', more: 'Más', share: 'Compartir', delete: 'Eliminar', offlineAnalysis: 'Análisis local de Mezcla ideal', offlineDescription: 'Mezcla ideal analiza tonalidades, tempo y puntos de entrada localmente para organizar la música sin interrupciones.', cancel: 'Cancelar', popular: 'Popular', title: 'Título', artist: 'Artista', album: 'Álbum', time: 'Duración', fullDescription: 'Descripción completa', closeDescription: 'Cerrar descripción', cancelAnalysis: 'Cancelar análisis de Mezcla ideal', tracks: 'Canciones de {title}', scrollLeft: 'Desplazar {section} a la izquierda', scrollRight: 'Desplazar {section} a la derecha', scrollVideosLeft: 'Desplazar videos musicales a la izquierda', scrollVideosRight: 'Desplazar videos musicales a la derecha', downloading: 'Descargando', removeDownload: 'Quitar descarga de', download: 'Descargar', analyzing: 'Analizando: {percent}%', bestMix: 'Mezcla ideal', prepareBestMix: 'Preparar Mezcla ideal', prepareBestMixHint: 'Descarga la colección y analízala localmente para crear una Mezcla ideal', moreActions: 'Más acciones para {title}', shareTitle: 'Compartir {title}', subscribe: 'Seguir a {title}', unsubscribe: 'Dejar de seguir a {title}', typeKeys: 'Escribe {keys}', openItem: 'Abrir {item}', songActions: 'Acciones de la canción'
    },
    actionWords: { open: 'Abrir', play: 'Reproducir', shuffle: 'Aleatorio', download: 'Descargar', share: 'Compartir', remove: 'Quitar', delete: 'Eliminar', close: 'Cerrar', loading: 'Cargando…', pin: 'Fijar', unpin: 'Desfijar', actionsFor: 'Acciones de {item}', findTrackIn: 'Buscar una canción en {collection}', findIn: 'Buscar en {collection}', closeTrackSearch: 'Cerrar búsqueda de canciones', queuePreview: 'La vista previa de la cola aparecerá aquí.' },
    settingsExtras: {
      equalizerMode: 'Modo de ecualizador',
      systemDefault: 'Predeterminado del sistema',
      audioEngineReset: 'Se restableció el motor de audio.', audioOutputLabel: 'Salida de audio {number}', outputListUnavailable: 'No se pudieron enumerar las salidas de audio.', outputUpdated: 'Se actualizó el dispositivo de salida.', outputSelectFailed: 'No se pudo seleccionar ese dispositivo de salida.', audioProfileExported: 'Se exportó el perfil de audio.', audioProfileImported: 'Se importó el perfil de audio.',
      audioEngine: 'Motor de audio', audioEngineDescription: 'Configura el sonido de Veyra Music y conserva los ajustes por canción entre sesiones.', equalizerCurve: 'Curva de respuesta del ecualizador', autoEqActive: 'Auto-EQ activo', manualEqActive: 'Ecualizador manual activo', direct: 'Directo', bypassed: 'Desactivado', automaticEq: 'Ecualización automática', autoEqDescription: 'Equilibra suavemente graves, medios y agudos mientras suena cada canción.', manualEq: 'Ecualizador manual', manualEqDescription: 'Usa preajustes o ajusta la curva de diez bandas.', hertzGain: 'Ganancia en {frequency} hercios', preamp: 'Preamplificación del ecualizador', bandWidth: 'Ancho de banda', balance: 'Balance', center: 'Centro', left: 'Izquierda', right: 'Derecha', dynamicLeveling: 'Nivelación dinámica', dynamicLevelingDescription: 'Reduce los cambios repentinos de volumen y controla los picos.', globalGain: 'Ganancia global', globalGainDescription: 'Ajusta todas las canciones y rutas de reproducción si el volumen de Veyra Music es excesivo o se distorsiona.', trackGain: 'Ganancia de canción', rememberedFor: 'Guardada para {title}', playToSetGain: 'Reproduce una canción para ajustar su ganancia guardada.', outputDevice: 'Dispositivo de salida', outputDeviceDescription: 'Usa una salida independiente para Veyra Music cuando Chromium detecte dispositivos del sistema.', exportProfile: 'Exportar perfil', importProfile: 'Importar perfil', resetEngine: 'Restablecer motor', artistPacks: 'Paquetes de artistas', artistPacksDescription: 'Páginas personalizadas de artistas instaladas en todos los paquetes.', customArtistPages: 'Páginas personalizadas de artistas', artistPagesDescription: 'Usa portadas, diseños, alias de búsqueda y efectos de los paquetes de artistas.', installedArtists: '{count} artistas instalados'
    },
    replay: {
      period: 'Periodo de Replay', playTop: 'Reproducir canciones principales', clear: 'Borrar', summary: 'Resumen de Replay', listens: 'Reproducciones', time: 'Tiempo', tracks: 'Canciones', artists: 'Artistas', days: 'Días', topTracks: 'Canciones principales', trackCount: '{count} canciones', plays: '{count} reproducciones', empty: 'Replay comienza a contar cuando una canción llega a los 30 segundos o a la mitad.', artistCount: '{count} artistas', albumCount: '{count} álbumes', noArtists: 'Todavía no hay artistas.', albums: 'Álbumes', noAlbums: 'Todavía no hay álbumes.', days30: '30 días', days7: '7 días', allTime: 'Todo el tiempo'
    },
    releaseRadar: {
      summary: 'Resumen de estrenos', subscriptions: 'Suscripciones', outToday: 'Se estrenan hoy', upcoming: 'Próximos', refreshing: 'Actualizando', refresh: 'Actualizar', info: 'Estrenos nuevos y próximos de los canales oficiales de artistas que sigues en YouTube.', subscribed: 'Artistas seguidos', checking: 'Buscando estrenos de tus suscripciones…', release: 'estreno', releases: 'estrenos'
    },
    recent: { history: 'Historial de reproducción', trackCount: '{count} canciones', empty: 'Todavía no se ha reproducido nada.' },
    podcasts: { loading: 'Cargando podcasts…', noResults: 'No se encontraron podcasts.', scrollLeft: 'Desplazar {section} a la izquierda', scrollRight: 'Desplazar {section} a la derecha' },
    pins: { title: 'Canciones fijadas', itemCount: '{count} elementos', unpin: 'Desfijar {title}', unpinAction: 'Desfijar', empty: 'Haz clic derecho en una canción y elige Fijar para conservarla aquí.' },
    home: {
      layoutLibraryDescription: 'Tus listas de reproducción y música guardadas.', layoutRecentDescription: 'Canciones reproducidas durante esta sesión.',
      loading: 'Cargando tu biblioteca…',
      recentlyPlayed: 'Reproducido recientemente',
      tracks: 'canciones',
      scrollRecentLeft: 'Desplazar reproducciones recientes a la izquierda',
      scrollRecentRight: 'Desplazar reproducciones recientes a la derecha',
      seeAll: 'Ver todo',
      scrollSectionLeft: 'Desplazar {section} a la izquierda',
      scrollSectionRight: 'Desplazar {section} a la derecha',
      hiddenTitle: 'Las secciones de Inicio están ocultas.',
      hiddenDescription: 'Elige qué aparece aquí en los ajustes de Inicio y barra lateral.',
      customize: 'Personalizar Inicio',
      empty: 'No se encontraron elementos en la biblioteca.'
    },
    search: {
      liveNear: 'Conciertos cerca de {location}',
      resultsFor: 'Resultados para “{query}”',
      title: 'Buscar en Veyra Music',
      loadingCategory: 'Cargando {category}…',
      updating: 'Actualizando resultados mientras escribes…',
      upcomingEvents: '{count} próximos eventos de Ticketmaster',
      noEvents: 'No se encontraron próximos eventos de Ticketmaster.',
      item: 'elemento',
      items: 'elementos',
      acrossCategories: 'resultados en {count} categorías',
      prompt: 'Busca canciones, álbumes, artistas, videos y listas de reproducción.',
      filterLabel: 'Tipo de resultados de búsqueda',
      topMatches: 'Mejores coincidencias',
      results: 'resultados',
      filters: { all: 'Todo', songs: 'Canciones', videos: 'Videos', albums: 'Álbumes', artists: 'Artistas', playlists: 'Listas de reproducción' },
      shuffleAll: 'Reproducir en aleatorio',
      song: 'Canción',
      albumsEmpty: 'No hay álbumes en tu biblioteca',
      albumsHint: 'Guarda un álbum en YouTube Music y aparecerá aquí.',
      artistsEmpty: 'No se encontraron artistas seguidos',
      artistsHint: 'Sigue un canal oficial de artista en YouTube y aparecerá aquí.',
      songsEmpty: 'No hay canciones en tu biblioteca',
      songsHint: 'Guarda una canción en YouTube Music y aparecerá aquí.',
      nothingYet: 'Todavía no hay contenido',
      libraryHint: 'Los elementos que agregues a tu biblioteca aparecerán aquí.',
      tryLocation: 'Prueba con otra ciudad o código postal.',
      tryQuery: 'Prueba otra ortografía o un filtro más amplio.',
      resultsUpdate: 'Tus resultados aparecerán aquí mientras escribes.'
    },
    sidebar: {
      compactNavigation: 'Navegación compacta',
      playlists: 'Listas de reproducción',
      unreadReplies: '{count} respuestas de soporte sin leer',
      connection: 'Conexión: {status}',
      signIn: 'Iniciar sesión',
      accountOptions: 'Opciones de cuenta',
      switchAccount: 'Cambiar de cuenta',
      signOut: 'Cerrar sesión',
      openLink: 'Abrir enlace',
      copyCode: 'Copiar código',
      copyLink: 'Copiar enlace'
    },
    player: {
      repeatOne: 'Repetir canción', repeatQueue: 'Repetir cola', repeatOff: 'Sin repetición',
      chooseSong: 'Elige una canción para comenzar', computer: 'Este equipo', loadingDevices: 'Cargando dispositivos…', noOutputDevices: 'No se encontraron dispositivos de salida', connect: 'Veyra Connect', noPhones: 'No hay teléfonos conectados', activeTarget: 'Dispositivo activo', output: 'Salida', listeningParty: 'Sala de escucha',
      nothingPlaying: 'No hay nada en reproducción', albumArtwork: 'Portada del álbum', removeLiked: 'Quitar de canciones que te gustan', addLiked: 'Agregar a canciones que te gustan', songActions: 'Acciones de la canción', artist: 'Artista', open: 'Abrir', shuffle: 'Reproducción aleatoria', shuffleOn: 'Aleatorio activado', shuffleOff: 'Aleatorio desactivado', previousSong: 'Canción anterior', nextSong: 'Canción siguiente', pausePlayback: 'Pausar reproducción', startPlayback: 'Iniciar reproducción', repeatMode: 'Modo de repetición', playbackPosition: 'Posición de reproducción', selectOutput: 'Seleccionar dispositivo de salida', scrollVolume: 'Desplaza para cambiar el volumen', muteVolume: 'Silenciar o activar el sonido', volumeLevel: 'Nivel de volumen', openFullscreen: 'Abrir reproductor a pantalla completa', sleepTimer: 'Temporizador de apagado', openSleepTimer: 'Abrir ajustes del temporizador', openQueue: 'Abrir panel de cola', openLyrics: 'Abrir panel de letras', quickSettings: 'Ajustes rápidos', nowPlaying: 'En reproducción', exitFullscreen: 'Salir de pantalla completa', nowPlayingControls: 'Controles de reproducción', songProgress: 'Progreso de la canción', previous: 'Anterior', next: 'Siguiente', play: 'Reproducir', pause: 'Pausar', hideLyrics: 'Ocultar letras', showLyrics: 'Mostrar letras', showQueue: 'Mostrar cola', unmute: 'Activar sonido', mute: 'Silenciar', volume: 'Volumen', queue: 'Cola', upNext: 'A continuación', save: 'Guardar', autoplay: 'Reproducción automática', closeQueue: 'Cerrar cola', removeFromQueue: 'Quitar de la cola', reorder: 'Reordenar', dragToReorder: 'Arrastra para reordenar', downloadingOffline: 'Descargando para escuchar sin conexión…', downloadedOffline: 'Descargado para escuchar sin conexión', resizeVideo: 'Cambiar tamaño del video', expandVideo: 'Ampliar video', minimizeVideo: 'Minimizar video', instrumentalBreak: 'Pausa instrumental'
    },
    dialogs: {
      newPlaylist: 'Nueva lista de reproducción', create: 'Crear', checkingPlaylists: 'Comprobando tus listas de reproducción…', deletePlaylistWarning: 'Esto quitará la lista de reproducción de tu biblioteca de YouTube Music. Esta acción no se puede deshacer.', deletePlaylist: 'Eliminar lista de reproducción',
      copyLink: 'Copiar enlace', openPage: 'Abrir página', share: 'Compartir', creatingLink: 'Creando enlace…',
      findShows: 'Buscar conciertos', showsDescription: 'Busca eventos musicales en Ticketmaster en un radio de 120 km.', location: 'Ciudad, estado o código postal', search: 'Buscar', or: 'o', useLocation: 'Usar ubicación actual',
      listeningParty: 'Sala de escucha', roomCode: 'CÓDIGO DE SALA', host: 'Anfitrión', connecting: 'Conectando a la sala…', yourName: 'TU NOMBRE', startParty: 'Iniciar sala', joinParty: 'Unirse a una sala',
      license: 'Licencia', contributors: 'Colaboradores', founder: 'Fundador', primaryTester: 'Probador principal', testerMobileDeveloper: 'Probador y desarrollador de la app móvil', updates: 'Actualizaciones de Veyra Music', installed: 'Instalada', available: 'Disponible', source: 'Origen', appUpdate: 'Actualización de la aplicación', checkApp: 'Buscar actualizaciones', artistPages: 'Páginas de artistas', importPack: 'Importar paquete', checkOfficial: 'Buscar actualizaciones oficiales', showInDownloads: 'Mostrar en Descargas'
    },
    playback: {
      itemCount: '{count} elementos', restoreQueueFrom: 'Restaurar cola desde {title}',
      upNext: 'A continuación', queue: 'Cola', openQueue: 'Abrir la cola completa', open: 'Abrir', restoreQueue: 'Restaurar el orden anterior de la cola', sortQueue: 'Ordenar la cola por compatibilidad musical', sortQueueHint: 'Usa BPM, tonalidad, energía, volumen y densidad vocal', bestMix: 'Mezcla ideal', queueOptions: 'Opciones de la cola', addToPlaylist: 'Agregar a una lista', clearQueue: 'Vaciar cola', queueEmpty: 'La cola está vacía', queueEmptyTitle: 'Todavía no hay canciones en la cola', queueEmptyHint: 'Reproduce una canción para comenzar a armar tu cola.', autoplay: 'Reproducción automática', findingMusic: 'Buscando más música…', keepMusicGoing: 'Seguir reproduciendo', toggleAutoplay: 'Activar o desactivar reproducción automática', lyrics: 'Letras', room: 'Sala {id}', hosting: 'Como anfitrión', syncedGuest: 'Sincronizado como invitado', connected: 'Conectados', you: 'Tú', guest: 'Invitado', waitingJoin: 'Esperando a que alguien se una.', copied: 'Copiado', copyInvite: 'Copiar invitación', endParty: 'Finalizar', leaveParty: 'Salir', listener: 'Oyente {id}',
      saveAsPlaylist: 'Guardar como lista', clear: 'Vaciar', recentlyPlayed: 'Reproducido recientemente', items: 'elementos', nothingPlayed: 'Todavía no se ha reproducido nada.', history: 'Historial de reproducción', events: 'eventos', sessionChanges: 'Los cambios de la sesión aparecerán aquí.', playNext: 'Reproducir a continuación', addQueue: 'Agregar a la cola', shareSong: 'Compartir canción', removePlaylist: 'Quitar de esta lista', openArtist: 'Abrir artista', openAlbum: 'Abrir álbum', lyricsLoading: 'Cargando letras', lyricsUnsynced: 'Estas letras no están sincronizadas con la música.', lyricsUnavailable: 'Letras no disponibles', queueNoTracks: 'No hay canciones en esta lista.', loadingMore: 'Cargando más canciones en segundo plano…'
    }
  },

  settings: {
    title: 'Ajustes',
    subtitle: 'Personaliza tu experiencia en Veyra Music',
    sections: {
      general: 'General',
      appearance: 'Apariencia',
      audio: 'Audio & DSP',
      playback: 'Reproducción',
      connect: 'Conectividad',
      account: 'Cuenta & Nube',
      about: 'Acerca de',
      cache: 'Caché de canciones',
      dataSaving: 'Ahorro de datos',
      layout: 'Inicio y barra lateral'
    },
    language: {
      label: 'Idioma de la interfaz',
      description: 'Selecciona tu idioma preferido para la aplicación',
      esMX: 'Español (México / Latinoamérica)',
      en: 'English (US)'
    },
    ui: {
      timerMinutes: '{minutes} min', streamQualityDescription: { saver: 'Audio con la tasa de bits más baja y video en 480p.', normal: 'Audio de hasta 128 kbps y video en 720p.', high: 'La mejor calidad de audio y video disponible.' },
      showInSidebar: 'Mostrar {item} en la barra lateral',
      playbackTitle: 'Reproducción', playbackDescription: 'Controla cómo Veyra Music enlaza las canciones.',
      autoplay: 'Reproducción automática', autoplayDescription: 'Mantén la cola con música relacionada.',
      saveQueue: 'Guardar cola y canción actual', saveQueueDescription: 'Restaura la última cola y canción al iniciar Veyra Music.',
      skipNonMusic: 'Omitir secciones sin música', skipNonMusicDescription: 'Detecta conversaciones, introducciones y cierres con marcadores comunitarios de SponsorBlock. Envía el ID del video que se reproduce a sponsor.ajay.app.',
      queueStyle: 'Estilo de cola', queueStyleDescription: '“A continuación” muestra solo lo que falta por reproducir. “Continua” también muestra lo ya reproducido y mantiene la canción actual en su lugar.',
      crossfade: 'Fundido cruzado', crossfadeDescription: 'Mezcla el final de una canción con el inicio de la siguiente.',
      crossfadeMode: 'Modo de fundido cruzado', crossfadeModeDescription: 'El modo inteligente detecta los cierres y evita mezclas incómodas con diálogos, grabaciones en vivo y pistas silenciosas.',
      crossfadeLength: 'Duración del fundido cruzado', crossfadeLengthDescription: 'Duración de la transición entre canciones.', seconds: 'segundos',
      sleepTimer: 'Temporizador de apagado', endSong: 'Al terminar la canción', cancel: 'Cancelar',
      appearanceTitle: 'Apariencia', appearanceDescription: 'Elige cómo influye la portada en la vista de reproducción.',
      textSize: 'Tamaño del texto y la interfaz', textSizeDescription: 'Ajusta Veyra Music para facilitar la lectura sin cambiar la configuración de pantalla.',
      interfaceDesign: 'Diseño de interfaz', graphicsMode: 'Modo de gráficos', graphicsModeDescription: 'Automático usa los valores predeterminados de Electron. GPU integrada prefiere gráficos de menor consumo. Los cambios requieren reiniciar.',
      linuxGpu: 'En Linux, Veyra Music selecciona directamente el adaptador integrado detectado.', gpuUnavailable: 'La selección de GPU integrada no está disponible en esta plataforma.', restartRequired: 'Se requiere reiniciar', restartDescription: 'Reinicia Veyra Music para aplicar {mode}.', restartApp: 'Reiniciar Veyra Music',
      immersive: 'Fondos inmersivos', immersiveDescription: 'Usa la portada actual para dar color y animar el fondo de la aplicación.',
      backgroundIntensity: 'Intensidad del fondo', backgroundIntensityDescription: 'Elige cuánto destaca la portada en el fondo.', backgroundMotion: 'Movimiento del fondo', backgroundMotionDescription: 'Congela el efecto de la portada sin desactivar sus colores.',
      accentColor: 'Color de acento', accentDescription: 'Colorea los controles y resaltados según la portada, Veyra Music o tu preferencia.', chooseColor: 'Elegir color', customAccent: 'Color de acento personalizado', palettePresets: 'Colores preestablecidos', paletteColor: 'Color {number} ({hex})',
      theme: 'Tema', themeDescription: 'Usa el tema oscuro de Veyra, negro OLED puro o el tema del sistema.',
      integrations: 'Integraciones', integrationsDescription: 'Administra lo que Veyra Music comparte con otras aplicaciones de escritorio.', discord: 'Presencia enriquecida de Discord', discordDescription: 'Muestra la canción actual y su portada en tu perfil de Discord.', youtubeHistory: 'Enviar historial de reproducción a YouTube', youtubeHistoryDescription: 'Las canciones reproducidas en Veyra Music se agregan al historial de YouTube Music de tu cuenta.',
      connect: 'Veyra Connect', connectDescription: 'Vincula un teléfono en tu red local y apruébalo antes de que pueda controlar la reproducción.', showCameraQr: 'Mostrar código QR de cámara', pairingLink: 'Enlace de vinculación', waitingBridge: 'Esperando conexión con el puente.', newQr: 'Nuevo QR', copyAppLink: 'Copiar enlace de la app', copyCameraLink: 'Copiar enlace de cámara', phoneAddressHelp: '¿Tu teléfono no puede acceder a esta dirección?', networkAlternates: 'Este equipo tiene más de una dirección de red. Si no funciona el código QR, abre uno de estos enlaces desde el teléfono.', approvePhone: 'Aprueba este teléfono antes de permitirle controlar Veyra Music.', approve: 'Aprobar', reject: 'Rechazar', connectedNow: 'Conectado', notConnected: 'Sin conexión', revoke: 'Desvincular', noPhones: 'No hay teléfonos vinculados.',
      application: 'Aplicación', closeToTray: 'Cerrar en la bandeja', closeToTrayDescription: 'Al cerrar la ventana, Veyra Music seguirá reproduciendo desde la bandeja del sistema. Para salir, usa el menú de la bandeja.', updates: 'Actualizaciones', updateHint: 'Buscar actualizaciones de la aplicación y de las páginas de artistas.', betaChannel: 'Canal beta', betaDescription: 'Obtén versiones beta desde GitHub en lugar del canal habitual. Las versiones beta pueden ser menos estables.', keepOldVersions: 'Conservar versiones anteriores', keepOldVersionsDescription: 'Conserva las carpetas de Veyra Music reemplazadas al actualizar. Desactívalo para borrar las versiones anteriores al activar la nueva.', about: 'Acerca de Veyra Music', restoreDefaults: 'Restablecer valores predeterminados',
      network: 'Red', networkDescription: 'Cómo se conecta Veyra Music a internet.', proxy: 'Omitir proxy del sistema', proxyDescription: 'Las portadas y las actualizaciones usan la configuración de proxy del equipo; la reproducción no. Actívalo si faltan portadas o fallan las actualizaciones con un error de túnel o proxy mientras la música sigue reproduciéndose.',
      optionLabels: {
        sponsor: { off: 'Desactivado', button: 'Mostrar botón', auto: 'Omitir automáticamente' },
        streamQuality: { saver: 'Ahorro', normal: 'Normal', high: 'Alta' },
        queue: { upNext: 'A continuación', continuous: 'Continua' },
        crossfade: { standard: 'Estándar', smart: 'Inteligente' },
        equalizer: { off: 'Desactivado', auto: 'Automático', manual: 'Manual' },
        eqPresets: { flat: 'Plano', bass: 'Realce de graves', electronic: 'Electrónica', rock: 'Rock', vocal: 'Voz', acoustic: 'Acústico', bright: 'Brillante' },
        layout: { grove: 'Grove — Neumórfico', canopy: 'Obsidian — Slate Industrial' },
        graphics: { automatic: 'Automático', integrated: 'GPU integrada' },
        intensity: { subtle: 'Sutil', balanced: 'Equilibrada', vivid: 'Intensa' },
        motion: { animated: 'Portada animada', static: 'Deformación de portada' },
        accent: { artwork: 'Portada', veyra: 'Veyra', orchard: 'Veyra', custom: 'Personalizado' },
        theme: { dark: 'Obsidian Slate (Oscuro industrial)', light: 'Clean Canvas (Claro pulido)' }
      },
      layoutDescriptions: {
        grove: 'Grove — Neumórfico: Explorador clásico en dos columnas con reproductor integrado a sangre completa.',
        canopy: 'Obsidian — Slate Industrial: Consola de estudio suspendida en cabecera, navegación modular flotante y chasis de audio desacoplado.'
      },
      diagnostics: 'Diagnósticos', backup: 'Copia de seguridad', settingsSections: 'Secciones de ajustes', reopenWelcome: 'Volver a abrir la configuración inicial',
      cacheDescription: 'Administra la caché de reproducción y las canciones descargadas para escuchar sin conexión.', caching: 'Guardar canciones en caché', cachingDescription: 'Reproduce desde el disco las canciones almacenadas en caché en lugar de descargarlas otra vez.', maximumSize: 'Tamaño máximo', cacheLimitDescription: 'Al llegar al límite, se eliminan primero las canciones de reproducción en caché. Las descargas manuales se conservan hasta que las elimines.', actions: 'Acciones', refreshCache: 'Actualizar caché', cacheQueue: 'Guardar en caché la cola actual', clearCache: 'Vaciar caché de canciones', cacheUsage: 'Uso de la caché de canciones', remove: 'Eliminar',
      backupDescription: 'Transfiere los ajustes locales y los datos de escucha de Veyra Music entre instalaciones.', exportData: 'Exportar datos', exportDescription: 'Fijados, preferencias, estado de la cola, Replay, historial de sesión y dispositivos Connect de confianza.', restoreData: 'Restaurar datos', importData: 'Importar copia', restoreDescription: 'Importa una copia de seguridad de Veyra Music en esta instalación. Reinicia después para volver a cargar por completo el estado de reproducción guardado.',
      dataSavingDescription: 'Reduce el consumo en conexiones medidas o lentas.', streamingQuality: 'Calidad de transmisión', playVideos: 'Reproducir videos musicales', playVideosDescription: 'Si se desactiva, se reproduce la versión de solo audio de cada canción y se ocultan las secciones de videos musicales.', animatedArtwork: 'Portadas animadas', animatedArtworkDescription: 'Las portadas en movimiento y los Canvas de Spotify pueden consumir decenas de megabytes. Si se desactiva, solo se muestra arte estático.',
      navLayoutDescription: 'Ten a mano la música que más usas. Los cambios se aplican al instante.', homeShelves: 'Secciones de Inicio', homeShelvesDescription: 'Reordena las secciones u ocúltalas de Inicio.', sidebar: 'Barra lateral', sidebarDescription: 'Organiza los enlaces de cada sección u oculta los que no uses.', reset: 'Restablecer', moveUp: 'Mover {item} arriba', moveDown: 'Mover {item} abajo', showOnHome: 'Mostrar {item} en Inicio'
    },
    theme: {
      label: 'Tema visual',
      description: 'Estilo cromático y de contraste de la aplicación',
      dark: 'Obsidian Slate (Oscuro industrial)',
      light: 'Clean Canvas (Claro pulido)',
      auto: 'Sincronizar con el sistema'
    }
  }
};
