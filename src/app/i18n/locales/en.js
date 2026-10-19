/**
 * Veyra Music — Locales: English (en)
 * AKASHI DEV — Professional B2B / Power-User Grade
 */

export default {
  common: {
    appName: 'Veyra Music',
    signIn: 'Sign in',
    signOut: 'Sign out',
    signedIn: 'Signed in',
    signedOut: 'Signed out',
    continue: 'Continue',
    next: 'Next',
    previous: 'Previous',
    back: 'Back',
    cancel: 'Cancel',
    close: 'Close',
    save: 'Save',
    done: 'Done',
    confirm: 'Confirm',
    search: 'Search',
    settings: 'Settings',
    support: 'Support',
    retry: 'Retry',
    loading: 'Loading…',
    offline: 'Offline',
    online: 'Online',
    openLink: 'Open link',
    copyCode: 'Copy code',
    copied: 'Copied to clipboard',
    on: 'On',
    off: 'Off',
    status: 'Status',
    minimize: 'Minimize',
    maximize: 'Maximize'
  },

  welcome: {
    steps: {
      account: 'Welcome to Veyra Music',
      layout: 'Interface design',
      immersive: 'Artwork background',
      sound: 'Tune playback',
      connected: 'Stay connected',
      finish: 'Ready to listen',
      canopyUpgrade: 'A new look for Veyra Music'
    },
    account: {
      title: 'Welcome to Veyra Music',
      description: 'Sign in first. Veyra Music will keep the main player out of the way until your setup is ready.',
      signInButton: 'Sign in to YouTube Music',
      openingSignIn: 'Opening sign in…',
      signedInAs: 'Signed in as {name}',
      deviceInstructions: 'Enter this code in your browser to authorize your account:'
    },
    layout: {
      title: 'Interface design',
      canopyDescription: 'Veyra Music introduces Obsidian — Slate Industrial, an aerospace suspended architecture with decoupled audio deck. Grove — Neumórfico remains available.',
      description: 'Choose the interface layout that fits you best.',
      groveLabel: 'Grove — Neumórfico',
      groveDesc: 'Two-column browser with full-bleed player.',
      canopyLabel: 'Obsidian — Slate Industrial',
      canopyDesc: 'Unified suspended audio dock with decoupled header console.'
    },
    immersive: {
      title: 'Artwork background',
      description: 'Choose whether Veyra Music should project album artwork behind the player.',
      toggleLabel: 'Immersive backgrounds',
      motion: {
        static: 'Static',
        pan: 'Slow pan',
        dynamic: 'Dynamic'
      }
    },
    sound: {
      title: 'Tune playback',
      description: 'Pick how Veyra Music should move through songs.',
      offTitle: 'Off',
      offDesc: 'Keep songs separate with no crossfade.',
      smartTitle: 'Smart Crossfade',
      smartDesc: 'Intelligent WSOLA beat detection; avoids awkward live, speech, and quiet-track blends.',
      standardTitle: 'Standard',
      standardDesc: 'A simple end-of-song volume blend.',
      autoplayLabel: 'Continuous playback (Autoplay)',
      autoEqLabel: 'Automatic EQ',
      durationLabel: 'Crossfade duration'
    },
    connected: {
      title: 'Stay connected',
      description: 'Choose what Veyra Music shares outside this desktop window.',
      discordLabel: 'Discord Rich Presence',
      pairPhone: 'Pair phone',
      bridgeStatus: 'Bridge: {status}'
    },
    finish: {
      title: 'Ready to listen',
      description: 'Setup is saved. Open Veyra Music and start listening.',
      canopyActive: 'Obsidian — Slate Industrial layout',
      groveActive: 'Grove — Neumórfico',
      backgroundsOn: 'Immersive backgrounds ({motion})',
      backgroundsOff: 'Backgrounds off',
      autoEqActive: 'Automatic EQ on',
      manualEqActive: 'Manual EQ on',
      eqOff: 'EQ off',
      devicesCount: '{count} paired devices',
      blockedAudioNote: 'Your system blocked automatic welcome music. The top-right audio button will start it.'
    },
    actions: {
      openApp: 'Open Veyra Music',
      openingApp: 'Opening Veyra Music…',
      soundStart: 'Start welcome music',
      soundMute: 'Mute welcome music',
      soundUnmute: 'Unmute welcome music'
    }
  },

  nav: {
    home: 'Home',
    discover: 'Discover',
    explore: 'Explore',
    search: 'Search',
    searchPlaceholder: 'Type or press / to Search',
    library: 'Library',
    nowPlaying: 'Now Playing',
    connect: 'Connect',
    settings: 'Settings',
    support: 'Support',
    about: 'About',
    new: 'New',
    radio: 'Radio',
    concerts: 'Concerts',
    queue: 'Queue',
    recentlyAdded: 'Recently Added',
    replay: 'Replay',
    playlists: 'Playlists',
    songs: 'Songs',
    albums: 'Albums',
    artists: 'Artists',
    podcasts: 'Podcasts',
    pins: 'Pins',
    lyrics: 'Lyrics',
    groups: {
      youtubeMusic: 'YouTube Music',
      library: 'Library',
      pins: 'Pins'
    }
  },

  supportUi: {
    reports: 'Your reports', currentIssues: 'Current issues', refreshReports: 'Refresh reports', newReport: 'New report',
    noReports: 'No reports yet.', reportDescription: "Your description is mirrored to Veyra Music's public issue tracker. Replies, diagnostics, and images stay private.",
    type: 'Type', bug: 'Bug', featureRequest: 'Feature request', artistPageRequest: 'Artist page request', feedback: 'Feedback',
    title: 'Title', shortSummary: 'A short summary', whatHappened: 'What happened?',
    describeExpected: 'Tell us what you expected and what Veyra Music did instead.', chooseImage: 'Choose image', captureVeyra: 'Capture Veyra Music',
    imageHelp: 'Paste or drop an image here, up to 5 MiB.', screenshotPreview: 'Screenshot preview', remove: 'Remove',
    includeDiagnostics: 'Include sanitized diagnostics', reviewDiagnostics: 'Review diagnostics', preparingAttachment: 'Preparing the private attachment…',
    diagnosticsNotice: 'Only the data shown below will be attached to this report.', refresh: 'Refresh', githubAttribution: 'GitHub attribution',
    issuePostedAs: 'The public issue will be posted as', enterGithubCode: 'Enter {code} in the GitHub page opened in your browser.',
    connectGithubHelp: 'Connect GitHub to show your account as the author of the public issue.', disconnect: 'Disconnect', connectGithub: 'Connect GitHub',
    privacyNotice: 'Cookies, authentication headers, playback URLs, diagnostics, screenshots, and music account identifiers are never posted publicly.',
    sendReport: 'Send report', reportsBack: 'Reports', publicIssue: 'Public issue', delete: 'Delete', you: 'You',
    replyPlaceholder: 'Reply to Veyra Support', replyScreenshotPreview: 'Reply screenshot preview', attachImage: 'Attach image', sendReply: 'Send reply',
    closedReport: 'This report is closed. Your messages remain available until you delete the report or its one-year retention period ends.',
    welcomeTitle: 'Veyra Support', welcomeDescription: 'Report a problem, suggest something new, or continue a private conversation with the support team.',
    createReport: 'Create a report'
  },

  ui: {
    feedback: { offlineDownloaded: 'Veyra Music is offline. Showing downloaded music.', downloadedOffline: 'Showing downloaded music while Veyra Music is offline.', offlineDownloadUnavailable: 'A downloaded song could not be read. Skipped to the next song.', noRecommendations: 'No more recommendations were found.', autoplayUnavailable: 'Could not load Autoplay recommendations.', playbackStalled: 'Playback stalled while loading this stream.', phonePlaybackFailed: 'The selected phone could not receive playback.', eventDateTba: 'Date to be announced', locationUnavailable: 'Current location is not available on this system.', githubSignInFinish: 'Finish GitHub sign-in in your browser, then return to Veyra Music.', githubDisconnected: 'GitHub disconnected from Veyra Support.', reportDeleted: 'Report and conversation deleted.' },
    analysis: { checkingCloud: 'Checking cloud analysis cache…', allCached: 'All {count} tracks are already analyzed in the cloud.', analyzerUnavailable: 'Local audio analyzer not available.', analyzing: 'Analyzing audio ({completed}/{total})…', completed: 'Done. {count} analyzed ({cached} from cloud, {processed} newly processed).', failed: 'Analysis ended unexpectedly.' },
    timer: { pauseIn: 'Veyra Music will pause in {time}.', pauseAtEnd: 'Veyra Music will pause when this song ends.', hint: 'Pause playback after a set time or when the current song ends.' },
    cacheMessages: { noCacheableTracks: 'Queue has no cacheable audio tracks.', cachingOne: 'Caching 1 song…', cachingMany: 'Caching {count} songs…', cachedOne: 'Cached {count} song.', cachedMany: 'Cached {count} songs.', cachedFailed: 'Cached {cached} of {total}. {failed} failed.', noDownloads: 'There are no downloadable songs here.', alreadyDownloaded: 'These songs are already downloaded.', downloadingOne: 'Downloading 1 song…', downloadingMany: 'Downloading {count} songs…', downloadedOne: 'Downloaded {count} song for offline playback.', downloadedMany: 'Downloaded {count} songs for offline playback.', downloadsFailed: 'Downloaded {downloaded} of {total}. {failed} failed.', downloadReady: 'Ready offline', downloadUnavailable: 'File missing or incomplete', loadingPlaylist: 'Loading the full playlist…', removedCached: 'Removed cached song.', cacheCleared: 'Cleared replay cache. Offline downloads were kept.' },
    lyrics: { pickTrack: 'Pick a track to load lyrics', loading: 'Loading lyrics', unsynced: 'Unsynced lyrics', wordSynced: 'Word-synced lyrics', synced: 'Synced lyrics', unavailable: 'No Lyrics :/' },
    readiness: {
      never: 'Never', onePhonePaired: '1 phone paired.', ready: 'Ready', needsAttention: 'Needs attention', review: 'Review',
      signinTitle: 'Sign in to YouTube Music', accountConnected: 'Connected to your music account.', accountRequired: 'Required for library, search, and playback.', updatesTitle: 'Check updates', updatesDetail: 'Make sure the app can reach release metadata.', appearanceTitle: 'Review appearance', appearanceDetail: 'Theme, accent color, and background motion are ready to tune.', audioTitle: 'Tune the Audio Engine', autoEqReady: 'Automatic EQ is ready to adapt as tracks play.', custom: 'Custom', eqActive: '{preset} EQ is active.', chooseEq: 'Choose automatic EQ, a manual preset, or direct playback.', connectTitle: 'Prepare Veyra Connect', pairedPhones: '{count} phones paired.', pairPhone: 'Generate a QR code and pair a phone on your LAN.', discordTitle: 'Review Discord presence', discordEnabled: 'Discord Rich Presence is enabled.', discordOff: 'Discord Rich Presence is off.', backupTitle: 'Create a backup', lastBackup: 'Last backup: {time}',
      signInBeforeOpen: 'Sign in before opening Veyra Music.', desktopBridgeUnavailable: 'The desktop bridge is unavailable.', cannotOpenApp: 'Could not open Veyra Music.', welcomeUnavailable: 'The desktop welcome window is unavailable.', reopenWelcomeFailed: 'Could not reopen the welcome setup.',
      desktopBridge: 'Desktop bridge', youtubeAccount: 'YouTube Music account', updater: 'Updater', veyraConnect: 'Veyra Connect', discordPresence: 'Discord Rich Presence', localStorage: 'Local storage', audioEngine: 'Audio Engine', audioFormats: 'Audio formats', videoFormats: 'Video formats', signedIn: 'Signed in', waitingBridge: 'Waiting for bridge state', enabled: 'Enabled', disabled: 'Disabled', automaticEq: 'Automatic EQ active', manualEq: 'Manual EQ active', direct: 'Direct mode', bypassed: 'Bypassed', outputOptions: '{count} output options', oneOutputOption: '1 output option', noReportedSupport: 'No reported support',
      diagnosticsRefreshed: 'Diagnostics refreshed.', reportCopied: 'Diagnostic report copied.', copyFailed: 'Copy failed. Export the report instead.', backupDownloaded: 'Backup downloaded.', backupRestored: 'Backup restored. Restart Veyra Music to reload the saved queue and history.'
    },
    pages: { signIn: 'Sign in', more: 'More', pins: 'Pins', queue: 'Queue', history: 'Recently Played', replay: 'Replay', releaseRadar: 'Release Radar', podcasts: 'Podcasts', search: 'Search', support: 'Support', settings: 'Settings', home: 'Home', accountRequired: 'YouTube Music account required', countFrom: '{count} items from {source}', upcomingQueue: 'Upcoming tracks and listening history', playedOnDevice: 'Tracks played on this device', pinnedDescription: 'Songs you want close at hand', replayDescription: 'Your local listening stats', radarDescription: 'New and upcoming albums from followed artists', podcastsDescription: 'Shows and episodes from YouTube Music', searchDescription: 'Pull results from the InnerTube bridge', supportDescription: 'Private reports and replies from Veyra Support', settingsDescription: 'Playback, appearance, and app preferences', homeDescription: 'Your library, mixes, and recent picks', loadLibrary: 'Sign in to load your YouTube Music library' },
    itemTypes: { upcomingAlbum: 'Upcoming Album', podcast: 'Podcast', episode: 'Episode', playlist: 'Playlist', artist: 'Artist', song: 'Song', video: 'Video', liveEvent: 'Live event', album: 'Album', ep: 'EP', single: 'Single' },
    smartMix: { label: 'Smart Crossfade: {from} into {to}', handoff: 'Handoff', mixing: 'Mixing', beatHandoff: '{count}-beat handoff', tempo: 'Tempo {value}%', phraseAligned: 'Phrase aligned' },
    rightPanel: { controls: 'Right panel controls', view: 'Right panel view', openQueue: 'Open queue', queueCount: 'Open queue, {count} tracks', openLyrics: 'Open lyrics', openSettings: 'Open settings', closeNowPlaying: 'Close now playing panel', nowPlaying: 'Now playing' },
    account: { title: 'Veyra Cloud Sync & Account', signedInAs: 'Signed in as {email}. Playlist analysis performed on PC will sync to your mobile devices.', signInHelp: 'Sign in or create an account to sync audio analysis metadata across your PC and mobile devices.', configureHelp: 'Configure your Supabase Cloud instance to enable account syncing.', disclaimer: 'Audio analysis metadata (BPM, musical key, downbeats, cue points) is shared publicly with the Veyra Cloud cache by Track Video ID. No personal listening history, user playlists, or identifying info is included.', email: 'Email address', password: 'Password', signIn: 'Sign In', createAccount: 'Create Account', hideCloudSettings: 'Hide Cloud Settings', customSupabase: 'Custom Supabase Configuration', supabaseUrl: 'https://your-project.supabase.co', supabaseKey: 'Supabase Anon / Public Key', saveCloudSettings: 'Save Cloud Settings', signOut: 'Sign Out' },
    accessibility: {
      startingSignIn: 'Starting sign in', skipNonMusic: 'Skip non-music', skipIntro: 'Skip intro', skipOutro: 'Skip outro', skip: 'Skip',
      explicit: 'Explicit', searchApp: 'Search Veyra Music', searchItems: 'Search songs, albums, artists, pages', closeSearch: 'Close search', searchResults: 'Search results', playAlbum: 'Play album', addAlbumQueue: 'Add album to queue', pinLeadTrack: 'Pin lead track', openAlbum: 'Open album', fullscreenLyrics: 'Lyrics', about: 'About Veyra Music', closeAbout: 'Close About dialog', signInYoutube: 'Sign in to YouTube Music', minimize: 'Minimize', maximize: 'Maximize', close: 'Close', share: 'Share {item}', resizeVideo: 'Resize video', expandVideo: 'Expand video', minimizeVideo: 'Minimize video', finishSignIn: 'Finish sign in to continue.', signedOut: 'Signed out.',
    },
    integrations: {
      spotifyConnectedMessage: 'Connected to Spotify.', spotifyLoginClosed: 'Login closed.', spotifyLoginFailed: 'Login failed.', spotifyDisconnected: 'Disconnected from Spotify.', spotifyDisconnectFailed: 'Disconnect failed.', spotifyCookieSaved: 'Spotify sp_dc cookie saved.', spotifyInvalidCookie: 'Invalid cookie.', spotifyCookieFailure: 'Failed to save cookie.', qobuzStatusUnavailable: 'Could not load Qobuz status.', qobuzConnectedPersistent: 'Qobuz is connected and enabled.', qobuzConnectedTemporary: 'Qobuz is connected for this session. Secure credential storage is unavailable.', qobuzLoginClosed: 'Login closed without connecting.', qobuzLoginFailed: 'Qobuz login failed.', qobuzDisconnected: 'Qobuz disconnected and its local login data was cleared.', qobuzDisconnectFailed: 'Could not disconnect Qobuz.', qobuzBestAvailable: 'Best available', qobuzLossless: 'CD lossless', qobuzHiRes: 'Hi-Res',
      spotifyTitle: 'Spotify Canvas integration', spotifyConnected: 'Connected to Spotify. Canvas animated artwork (.mp4) will be fetched when available.', spotifyHelp: 'Log into Spotify or supply your sp_dc cookie to fetch Spotify Canvas videos when other providers miss.', disconnect: 'Disconnect', spotifyLogin: 'Log in to Spotify', cancel: 'Cancel', enterCookie: 'Enter Cookie', cookiePlaceholder: 'Paste sp_dc cookie string here…', save: 'Save',
      qobuzTitle: 'Qobuz lossless playback', qobuzConnected: 'Use your Qobuz subscription as an optional lossless source. Veyra Music still uses YouTube Music for its catalog and safely falls back when it cannot identify the same recording.', qobuzHelp: "Connect your own Qobuz subscription to add lossless and Hi-Res playback. This uses Qobuz's private web API and may need maintenance when its web player changes.", enabled: 'Enabled', useQobuz: 'Use Qobuz for matching tracks', connectQobuz: 'Connect Qobuz', connecting: 'Connecting…', qobuzQuality: 'Qobuz quality', qobuzQualityHelp: 'Best available asks for up to 24-bit/192 kHz; Qobuz returns the best master your plan allows.',
      lastfmTitle: 'Last.fm scrobbling', lastfmSending: 'Sending now-playing updates and completed listens as {user}.', lastfmApprove: 'Approve Veyra Music on Last.fm, then finish the connection.', lastfmHelp: 'Connect an account to add Veyra Music listening history to Last.fm.', secureStorageUnavailable: 'Secure credential storage is unavailable, so this connection lasts until Veyra Music closes.', finishConnection: 'Finish connection',
      diagnostics: 'Diagnostics', lastRefreshed: 'Last refreshed {time}', checkBeforeRelease: 'Check Veyra Music before release or support.', refresh: 'Refresh', copyReport: 'Copy report', noDiagnostics: 'No diagnostics collected yet.', setup: 'Setup', setupReady: '{progress} ready for Veyra Music', finishSetup: 'Finish setup', refreshDiagnostics: 'Refresh diagnostics', setupComplete: 'Setup complete', reopenChecklist: 'Reopen the checklist any time before packaging or moving to another machine.', reopen: 'Reopen'
    },
    browse: {
      playCollection: 'Play {title}', shuffleCollection: 'Shuffle {title}', downloadFor: 'Download {title}', downloadingFor: 'Downloading {title}', removeDownloadFor: 'Remove download for {title}',
      descriptionActionFor: '{action} for {title}', podcast: 'Podcast', playlist: 'Playlist', downloadingEllipsis: 'Downloading…', removeDownloadButton: 'Remove Download', saving: 'Saving…', subscribeButton: 'Subscribe', unsubscribeButton: 'Unsubscribe', cancel: 'Cancel',
      aboutTitle: 'About {title}', readBio: 'Read bio', readDescription: 'Read full description',
      loading: 'Loading collection…', play: 'Play', shuffle: 'Shuffle', more: 'More', share: 'Share', delete: 'Delete', offlineAnalysis: 'Best Mix Offline Analysis', offlineDescription: 'Best Mix analyzes harmonic keys, tempo, and cue points locally to arrange your music seamlessly.', cancel: 'Cancel', popular: 'Popular', title: 'Title', artist: 'Artist', album: 'Album', time: 'Time', fullDescription: 'Full description', closeDescription: 'Close description', cancelAnalysis: 'Cancel Best Mix analysis', tracks: '{title} tracks', scrollLeft: 'Scroll {section} left', scrollRight: 'Scroll {section} right', scrollVideosLeft: 'Scroll music videos left', scrollVideosRight: 'Scroll music videos right', downloading: 'Downloading', removeDownload: 'Remove download for', download: 'Download', analyzing: 'Analyzing: {percent}%', bestMix: 'Best mix', prepareBestMix: 'Prepare Best Mix', prepareBestMixHint: 'Download the collection and analyze it locally for Best Mix', moreActions: 'More actions for {title}', shareTitle: 'Share {title}', subscribe: 'Subscribe to {title}', unsubscribe: 'Unsubscribe from {title}', typeKeys: 'Type {keys}', openItem: 'Open {item}', songActions: 'Song actions'
    },
    actionWords: { open: 'Open', play: 'Play', shuffle: 'Shuffle', download: 'Download', share: 'Share', remove: 'Remove', delete: 'Delete', close: 'Close', loading: 'Loading…', pin: 'Pin', unpin: 'Unpin', actionsFor: 'Actions for {item}', findTrackIn: 'Find a track in {collection}', findIn: 'Find in {collection}', closeTrackSearch: 'Close track search', queuePreview: 'Queue preview will appear here.' },
    settingsExtras: {
      equalizerMode: 'Equalizer mode',
      systemDefault: 'System default',
      audioEngineReset: 'Audio Engine reset.', audioOutputLabel: 'Audio output {number}', outputListUnavailable: 'Could not list audio output devices.', outputUpdated: 'Output device updated.', outputSelectFailed: 'Could not select that output device.', audioProfileExported: 'Audio profile exported.', audioProfileImported: 'Audio profile imported.',
      audioEngine: 'Audio Engine', audioEngineDescription: "Shape Veyra Music's sound and keep track-level adjustments between sessions.", equalizerCurve: 'Equalizer response curve', autoEqActive: 'Auto EQ active', manualEqActive: 'Manual EQ active', direct: 'Direct', bypassed: 'Bypassed', automaticEq: 'Automatic EQ', autoEqDescription: 'Gently balance bass, mids, and treble as each track plays.', manualEq: 'Manual equalizer', manualEqDescription: 'Use presets or shape the ten-band curve yourself.', hertzGain: '{frequency} hertz gain', preamp: 'EQ preamp', bandWidth: 'Band width', balance: 'Balance', center: 'Center', left: 'Left', right: 'Right', dynamicLeveling: 'Dynamic leveling', dynamicLevelingDescription: 'Reduce sudden volume jumps and control loud peaks.', globalGain: 'Global gain', globalGainDescription: "Trim every track and playback path when Veyra Music's output is too loud or distorts.", trackGain: 'Track gain', rememberedFor: 'Remembered for {title}', playToSetGain: 'Play a track to set its remembered gain.', outputDevice: 'Output device', outputDeviceDescription: 'Route Veyra Music independently when Chromium exposes system outputs.', exportProfile: 'Export profile', importProfile: 'Import profile', resetEngine: 'Reset engine', artistPacks: 'Artist Packs', artistPacksDescription: 'Custom artist pages installed across all packs.', customArtistPages: 'Custom artist pages', artistPagesDescription: 'Use artist pack artwork, layouts, search aliases, and page effects.', installedArtists: '{count} Installed Artists'
    },
    replay: {
      period: 'Replay period', playTop: 'Play top tracks', clear: 'Clear', summary: 'Replay summary', listens: 'Listens', time: 'Time', tracks: 'Tracks', artists: 'Artists', days: 'Days', topTracks: 'Top tracks', trackCount: '{count} tracks', plays: '{count} plays', empty: 'Replay starts counting after a song reaches 30 seconds or halfway through.', artistCount: '{count} artists', albumCount: '{count} albums', noArtists: 'No artists yet.', albums: 'Albums', noAlbums: 'No albums yet.', days30: '30 days', days7: '7 days', allTime: 'All time'
    },
    releaseRadar: {
      summary: 'Release Radar summary', subscriptions: 'Subscriptions', outToday: 'Out today', upcoming: 'Upcoming', refreshing: 'Refreshing', refresh: 'Refresh', info: 'New and upcoming releases from Official Artist Channels you subscribe to on YouTube.', subscribed: 'Subscribed artists', checking: 'Checking subscription releases…', release: 'release', releases: 'releases'
    },
    recent: { history: 'Listening history', trackCount: '{count} tracks', empty: 'Nothing has been played yet.' },
    podcasts: { loading: 'Loading podcasts…', noResults: 'No podcasts were returned.', scrollLeft: 'Scroll {section} left', scrollRight: 'Scroll {section} right' },
    pins: { title: 'Pinned songs', itemCount: '{count} items', unpin: 'Unpin {title}', unpinAction: 'Unpin', empty: 'Right-click any song and choose Pin to keep it here.' },
    home: {
      layoutLibraryDescription: 'Your saved playlists and music.', layoutRecentDescription: 'Tracks played during this session.',
      loading: 'Loading your library…',
      recentlyPlayed: 'Recently Played',
      tracks: 'tracks',
      scrollRecentLeft: 'Scroll recently played left',
      scrollRecentRight: 'Scroll recently played right',
      seeAll: 'See all',
      scrollSectionLeft: 'Scroll {section} left',
      scrollSectionRight: 'Scroll {section} right',
      hiddenTitle: 'Your Home shelves are hidden.',
      hiddenDescription: 'Choose what appears here in Home & sidebar settings.',
      customize: 'Customize Home',
      empty: 'No library items were returned.'
    },
    search: {
      liveNear: 'Live shows near {location}',
      resultsFor: 'Results for “{query}”',
      title: 'Search Veyra Music',
      loadingCategory: 'Loading {category}…',
      updating: 'Updating results as you type…',
      upcomingEvents: '{count} upcoming Ticketmaster events',
      noEvents: 'No upcoming Ticketmaster events found.',
      item: 'item',
      items: 'items',
      acrossCategories: 'results across {count} categories',
      prompt: 'Find songs, albums, artists, videos, and playlists.',
      filterLabel: 'Search result type',
      topMatches: 'Top matches',
      results: 'results',
      filters: { all: 'All', songs: 'Songs', videos: 'Videos', albums: 'Albums', artists: 'Artists', playlists: 'Playlists' },
      shuffleAll: 'Shuffle all',
      song: 'Song',
      albumsEmpty: 'No albums in your library',
      albumsHint: 'Save an album in YouTube Music and it will appear here.',
      artistsEmpty: 'No subscribed artists found',
      artistsHint: 'Subscribe to an Official Artist Channel on YouTube and it will appear here.',
      songsEmpty: 'No songs in your library',
      songsHint: 'Save a song in YouTube Music and it will appear here.',
      nothingYet: 'Nothing here yet',
      libraryHint: 'Items added to your library will appear here.',
      tryLocation: 'Try another city or ZIP code.',
      tryQuery: 'Try a different spelling or a broader filter.',
      resultsUpdate: 'Your results will update here without interrupting your typing.'
    },
    sidebar: {
      compactNavigation: 'Compact navigation',
      playlists: 'Playlists',
      unreadReplies: '{count} unread support replies',
      connection: 'Connection: {status}',
      signIn: 'Sign in',
      accountOptions: 'Account options',
      switchAccount: 'Switch account',
      signOut: 'Sign out',
      openLink: 'Open link',
      copyCode: 'Copy code',
      copyLink: 'Copy link'
    },
    player: {
      repeatOne: 'Repeat one', repeatQueue: 'Repeat queue', repeatOff: 'Repeat off',
      chooseSong: 'Choose a song to begin', computer: 'This Computer', loadingDevices: 'Loading devices…', noOutputDevices: 'No output devices found', connect: 'Veyra Connect', noPhones: 'No phones connected', activeTarget: 'Active target', output: 'Output', listeningParty: 'Listening party',
      nothingPlaying: 'Nothing playing', albumArtwork: 'Album artwork', removeLiked: 'Remove from Liked Songs', addLiked: 'Add to Liked Songs', songActions: 'Song actions', artist: 'Artist', open: 'Open', shuffle: 'Shuffle', shuffleOn: 'Shuffle on', shuffleOff: 'Shuffle off', previousSong: 'Previous song', nextSong: 'Next song', pausePlayback: 'Pause playback', startPlayback: 'Start playback', repeatMode: 'Repeat mode', playbackPosition: 'Playback position', selectOutput: 'Select output device', scrollVolume: 'Scroll to change volume', muteVolume: 'Mute or unmute volume', volumeLevel: 'Volume level', openFullscreen: 'Open fullscreen player', sleepTimer: 'Sleep timer', openSleepTimer: 'Open sleep timer settings', openQueue: 'Open queue panel', openLyrics: 'Open lyrics panel', quickSettings: 'Quick settings', nowPlaying: 'Now Playing', exitFullscreen: 'Exit fullscreen player', nowPlayingControls: 'Now playing controls', songProgress: 'Song progress', previous: 'Previous', next: 'Next', play: 'Play', pause: 'Pause', hideLyrics: 'Hide lyrics', showLyrics: 'Show lyrics', showQueue: 'Show queue', unmute: 'Unmute', mute: 'Mute', volume: 'Volume', queue: 'Queue', upNext: 'Up next', save: 'Save', autoplay: 'Autoplay', closeQueue: 'Close queue', removeFromQueue: 'Remove from queue', reorder: 'Reorder', dragToReorder: 'Drag to reorder', downloadingOffline: 'Downloading offline…', downloadedOffline: 'Downloaded offline', resizeVideo: 'Resize video', expandVideo: 'Expand video', minimizeVideo: 'Minimize video', instrumentalBreak: 'Instrumental break'
    },
    dialogs: {
      newPlaylist: 'New playlist', create: 'Create', checkingPlaylists: 'Checking your playlists…', deletePlaylistWarning: 'This removes the playlist from your YouTube Music library. This cannot be undone.', deletePlaylist: 'Delete playlist',
      copyLink: 'Copy link', openPage: 'Open page', share: 'Share', creatingLink: 'Creating link...',
      findShows: 'Find live shows', showsDescription: 'Search Ticketmaster for music events within 75 miles.', location: 'City, state, or ZIP code', search: 'Search', or: 'or', useLocation: 'Use current location',
      listeningParty: 'Listening Party', roomCode: 'ROOM CODE', host: 'Host', connecting: 'Connecting to room...', yourName: 'YOUR NAME', startParty: 'Start a Party', joinParty: 'Join a Party',
      license: 'License', contributors: 'Contributors', founder: 'Founder', primaryTester: 'Primary Tester', testerMobileDeveloper: 'Tester and Mobile App Developer', updates: 'Veyra Music updates', installed: 'Installed', available: 'Available', source: 'Source', appUpdate: 'App update', checkApp: 'Check app', artistPages: 'Artist pages', importPack: 'Import pack', checkOfficial: 'Check official', showInDownloads: 'Show in Downloads'
    },
    playback: {
      itemCount: '{count} items', restoreQueueFrom: 'Restore queue from {title}',
      upNext: 'Up next', queue: 'Queue', openQueue: 'Open the full queue', open: 'Open', restoreQueue: 'Restore previous queue order', sortQueue: 'Sort the queue by musical compatibility', sortQueueHint: 'Uses BPM, key, energy, loudness, and vocal density', bestMix: 'Best mix', queueOptions: 'Queue options', addToPlaylist: 'Add to playlist', clearQueue: 'Clear queue', queueEmpty: 'The queue is empty', queueEmptyTitle: 'Nothing in the queue yet', queueEmptyHint: 'Play a song to start building your queue.', autoplay: 'Autoplay', findingMusic: 'Finding more music…', keepMusicGoing: 'Keep the music going', toggleAutoplay: 'Toggle Autoplay', lyrics: 'Lyrics', room: 'Room {id}', hosting: 'Hosting', syncedGuest: 'Synced as guest', connected: 'Connected', you: 'You', guest: 'Guest', waitingJoin: 'Waiting for someone to join.', copied: 'Copied', copyInvite: 'Copy invite', endParty: 'End', leaveParty: 'Leave', listener: 'Listener {id}',
      saveAsPlaylist: 'Save as playlist', clear: 'Clear', recentlyPlayed: 'Recently played', items: 'items', nothingPlayed: 'Nothing has been played yet.', history: 'Now playing history', events: 'events', sessionChanges: 'Session changes will appear here.', playNext: 'Play next', addQueue: 'Add to queue', shareSong: 'Share song', removePlaylist: 'Remove from this playlist', openArtist: 'Open artist', openAlbum: 'Open album', lyricsLoading: 'Loading lyrics', lyricsUnsynced: "These lyrics aren't synced to the music.", lyricsUnavailable: 'Lyrics unavailable', queueNoTracks: 'No tracks in this playlist.', loadingMore: 'Loading additional tracks in the background…'
    }
  },

  settings: {
    title: 'Settings',
    subtitle: 'Customize your Veyra Music experience',
    sections: {
      general: 'General',
      appearance: 'Appearance',
      audio: 'Audio & DSP',
      playback: 'Playback',
      connect: 'Connectivity',
      account: 'Account & Cloud',
      about: 'About',
      cache: 'Song Cache',
      dataSaving: 'Data Saving',
      layout: 'Home & sidebar'
    },
    language: {
      label: 'Interface language',
      description: 'Choose your preferred display language',
      esMX: 'Español (México / Latinoamérica)',
      en: 'English (US)'
    },
    ui: {
      timerMinutes: '{minutes} min', streamQualityDescription: { saver: 'Lowest bitrate audio and 480p video.', normal: 'Up to 128 kbps audio and 720p video.', high: 'Best available audio and video.' },
      showInSidebar: 'Show {item} in the sidebar',
      playbackTitle: 'Playback', playbackDescription: 'Control how Veyra Music moves between tracks.',
      autoplay: 'Autoplay', autoplayDescription: 'Keep the queue going with related music.',
      saveQueue: 'Save queue and current song', saveQueueDescription: 'Restore the last queue and song when Veyra Music starts.',
      skipNonMusic: 'Skip non-music sections', skipNonMusicDescription: 'Handle talking, intros, and outros using community markers from SponsorBlock. Sends the video ID of the playing track to sponsor.ajay.app.',
      queueStyle: 'Queue style', queueStyleDescription: 'Up next lists only what is still queued. Continuous also shows what already played, with the current song in place.',
      crossfade: 'Crossfade', crossfadeDescription: 'Blend the end of one track into the next.',
      crossfadeMode: 'Crossfade mode', crossfadeModeDescription: 'Smart listens for outros and avoids awkward speech, live, and quiet-track blends.',
      crossfadeLength: 'Crossfade length', crossfadeLengthDescription: 'How long the transition between tracks lasts.', seconds: 'seconds',
      sleepTimer: 'Sleep timer', endSong: 'End of song', cancel: 'Cancel',
      appearanceTitle: 'Appearance', appearanceDescription: 'Choose how artwork shapes the listening view.',
      textSize: 'Text and interface size', textSizeDescription: 'Scale Veyra Music for readability without changing your display settings.',
      interfaceDesign: 'Interface design', graphicsMode: 'Graphics mode', graphicsModeDescription: 'Automatic uses Electron defaults. Integrated GPU prefers lower-power graphics. Changes require a restart.',
      linuxGpu: 'On Linux, Veyra Music selects the detected integrated adapter directly.', gpuUnavailable: 'Integrated GPU selection is unavailable on this platform.', restartRequired: 'Restart required', restartDescription: 'Restart Veyra Music to apply {mode}.', restartApp: 'Restart Veyra Music',
      immersive: 'Immersive backgrounds', immersiveDescription: 'Use the current artwork to tint and animate the app background.',
      backgroundIntensity: 'Background intensity', backgroundIntensityDescription: 'Choose how strongly the artwork fills the app.', backgroundMotion: 'Background motion', backgroundMotionDescription: 'Freeze the artwork wash without turning off its color.',
      accentColor: 'Accent color', accentDescription: 'Color controls and highlights from the artwork, Veyra Music, or your own choice.', chooseColor: 'Choose color', customAccent: 'Custom accent color', palettePresets: 'Palette presets', paletteColor: 'Color {number} ({hex})',
      theme: 'Theme', themeDescription: 'Use Veyra dark, true black OLED, or follow the operating system.',
      integrations: 'Integrations', integrationsDescription: 'Manage what Veyra Music shares with other desktop apps.', discord: 'Discord Rich Presence', discordDescription: 'Show the current track and artwork on your Discord profile.', youtubeHistory: 'Send listening history to YouTube', youtubeHistoryDescription: 'Tracks played in Veyra Music are added to the signed-in YouTube Music history.',
      connect: 'Veyra Connect', connectDescription: 'Pair a phone on your LAN and approve it before it can control playback.', showCameraQr: 'Show camera QR', pairingLink: 'Pairing link', waitingBridge: 'Waiting for bridge connection.', newQr: 'New QR', copyAppLink: 'Copy app link', copyCameraLink: 'Copy camera link', phoneAddressHelp: "Phone can't reach this address?", networkAlternates: 'This machine has more than one network address. If the QR code does not connect, open one of these on the phone instead.', approvePhone: 'Approve this phone before it can control Veyra Music.', approve: 'Approve', reject: 'Reject', connectedNow: 'Connected now', notConnected: 'Not connected', revoke: 'Revoke', noPhones: 'No phones paired yet.',
      application: 'Application', closeToTray: 'Close to tray', closeToTrayDescription: 'Closing the window keeps Veyra Music playing in the tray instead of quitting. Quit from the tray menu.', updates: 'Updates', updateHint: 'Check for app and artist page updates.', betaChannel: 'Beta channel', betaDescription: 'Get beta builds from GitHub releases instead of the regular channel. Beta builds may be less stable.', keepOldVersions: 'Keep old versions', keepOldVersionsDescription: 'Retain replaced Veyra Music application folders after an update. Turn this off to remove older versions when the new one is activated.', about: 'About Veyra Music', restoreDefaults: 'Restore defaults',
      network: 'Network', networkDescription: 'How Veyra Music reaches the internet.', proxy: 'Ignore system proxy', proxyDescription: "Album art and update checks go through your computer's proxy settings; playback never does. Turn this on if artwork is missing or updates fail with a tunnel or proxy error while music still plays.",
      optionLabels: {
        sponsor: { off: 'Off', button: 'Show button', auto: 'Skip automatically' },
        streamQuality: { saver: 'Saver', normal: 'Normal', high: 'High' },
        queue: { upNext: 'Up next', continuous: 'Continuous' },
        crossfade: { standard: 'Standard', smart: 'Smart' },
        equalizer: { off: 'Off', auto: 'Auto', manual: 'Manual' },
        eqPresets: { flat: 'Flat', bass: 'Bass boost', electronic: 'Electronic', rock: 'Rock', vocal: 'Vocal', acoustic: 'Acoustic', bright: 'Bright' },
        layout: { grove: 'Grove — Neumórfico', canopy: 'Obsidian — Slate Industrial' },
        graphics: { automatic: 'Automatic', integrated: 'Integrated GPU' },
        intensity: { subtle: 'Subtle', balanced: 'Balanced', vivid: 'Vivid' },
        motion: { animated: 'Animated artwork', static: 'Artwork warp' },
        accent: { artwork: 'Artwork', veyra: 'Veyra', orchard: 'Veyra', custom: 'Custom' },
        theme: { dark: 'Dark', oled: 'OLED', system: 'System' }
      },
      layoutDescriptions: {
        grove: 'Grove — Neumórfico: The classic two-column shell with full-bleed player.',
        canopy: 'Obsidian — Slate Industrial: Suspended header studio console, floating modular navigation, and decoupled audio chassis.'
      },
      diagnostics: 'Diagnostics', backup: 'Backup', settingsSections: 'Settings sections', reopenWelcome: 'Reopen welcome setup',
      cacheDescription: 'Manage replay caching and songs downloaded for offline playback.', caching: 'Song caching', cachingDescription: 'Replay cached songs from disk instead of fetching them again.', maximumSize: 'Maximum size', cacheLimitDescription: 'Old replay-cache songs are removed first at this limit. Manual downloads stay until you remove them.', actions: 'Actions', refreshCache: 'Refresh cache', cacheQueue: 'Cache current queue', clearCache: 'Clear song cache', cacheUsage: 'Song cache usage', remove: 'Remove',
      backupDescription: 'Move local Veyra Music settings and listening data between installs.', exportData: 'Export data', exportDescription: 'Pins, preferences, queue state, Replay, session history, and trusted Connect devices.', restoreData: 'Restore data', importData: 'Import backup', restoreDescription: 'Imports a Veyra Music backup into this install. Restart after restoring to fully reload saved playback state.',
      dataSavingDescription: 'Spend less on a metered or slow connection.', streamingQuality: 'Streaming quality', playVideos: 'Play music videos', playVideosDescription: 'Off plays the audio-only album version of every song and hides music video shelves.', animatedArtwork: 'Animated artwork', animatedArtworkDescription: 'Moving covers and Spotify canvases run tens of megabytes each. Off keeps still artwork only.',
      navLayoutDescription: 'Put the music you use most within easy reach. Changes appear immediately.', homeShelves: 'Home shelves', homeShelvesDescription: 'Move shelves into your preferred order or hide them from Home.', sidebar: 'Sidebar', sidebarDescription: 'Arrange links within each section or hide links you do not use.', reset: 'Reset', moveUp: 'Move {item} up', moveDown: 'Move {item} down', showOnHome: 'Show {item} on Home'
    },
    theme: {
      label: 'Theme',
      description: 'Color theme and contrast mode',
      dark: 'Obsidian Slate (Industrial dark)',
      light: 'Clean Canvas (Polished light)',
      auto: 'Sync with system'
    }
  }
};
