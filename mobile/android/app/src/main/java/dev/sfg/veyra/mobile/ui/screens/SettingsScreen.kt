/*
 * Copyright (C) 2026 SFG545
 *
 * This file is part of Veyra Music.
 *
 * Veyra Music es un fork de Orchard (https://github.com/SFG5453/Orchard),
 * mantenido únicamente para consumo personal y sin afiliación con el
 * proyecto original.
 *
 * Veyra Music is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Veyra Music is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Veyra Music. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.sfg.veyra.mobile.ui.screens

import android.content.Context
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import dev.sfg.veyra.mobile.ui.scroll.orchardVerticalScroll as verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import dev.sfg.veyra.connect.BuildConfig
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.MobileChangelog
import dev.sfg.veyra.mobile.MobileUpdateMetadata
import dev.sfg.veyra.mobile.UpdateState
import dev.sfg.veyra.mobile.auth.AuthState
import dev.sfg.veyra.mobile.discord.DiscordAuthState
import dev.sfg.veyra.mobile.discord.GatewayConnectionState
import dev.sfg.veyra.mobile.i18n.AppLanguage
import dev.sfg.veyra.mobile.i18n.AppLocale
import dev.sfg.veyra.mobile.lastfm.LastfmState
import dev.sfg.veyra.mobile.listenbrainz.ListenBrainzState
import dev.sfg.veyra.mobile.model.AudioQuality
import dev.sfg.veyra.mobile.model.MobilePlayerMode
import dev.sfg.veyra.mobile.model.OrchardSettings
import dev.sfg.veyra.mobile.ui.components.OrchardChromeHeight
import dev.sfg.veyra.mobile.ui.components.ReleaseNotesDialog
import dev.sfg.veyra.mobile.ui.components.UpdateDialog
import dev.sfg.veyra.mobile.ui.glass.GlassStyle
import dev.sfg.veyra.mobile.ui.glass.GlassTone
import dev.sfg.veyra.mobile.ui.glass.LocalGlass
import dev.sfg.veyra.mobile.ui.glass.glassPane
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent

@Composable
fun SettingsScreen(
    settings: OrchardSettings,
    auth: AuthState,
    discordAuth: DiscordAuthState = DiscordAuthState.SignedOut,
    discordConnection: GatewayConnectionState = GatewayConnectionState.Disconnected,
    lastfmState: LastfmState = LastfmState.SignedOut,
    listenBrainzState: ListenBrainzState = ListenBrainzState.SignedOut,
    updateState: UpdateState = UpdateState.Idle,
    onSettings: (OrchardSettings) -> Unit,
    onSignIn: () -> Unit,
    onSwitchAccount: () -> Unit,
    onSignOut: () -> Unit,
    onConnectDiscord: () -> Unit = {},
    onDisconnectDiscord: () -> Unit = {},
    onConnectLastfm: () -> Unit = {},
    onCompleteLastfm: () -> Unit = {},
    onDisconnectLastfm: () -> Unit = {},
    onConnectListenBrainz: (String) -> Unit = {},
    onDisconnectListenBrainz: () -> Unit = {},
    onConnectSpotify: () -> Unit = {},
    qobuzStatus: dev.sfg.veyra.mobile.qobuz.QobuzStatus = dev.sfg.veyra.mobile.qobuz.QobuzStatus(),
    onConnectQobuz: () -> Unit = {},
    onDisconnectQobuz: () -> Unit = {},
    onQobuzEnabledChange: (Boolean) -> Unit = {},
    onQobuzQualityChange: (dev.sfg.veyra.mobile.qobuz.QobuzQuality) -> Unit = {},
    onDevices: () -> Unit,
    onWelcome: () -> Unit = {},
    onCheckForUpdates: () -> Unit = {},
    onInstallUpdate: (MobileUpdateMetadata) -> Unit = {},
    onHomeLayout: () -> Unit = {},
    /**
     * Separate from [onSettings] because switching Autoplay off also strips the tracks it added,
     * which only the view model can do.
     */
    onAutoplayEnabled: ((Boolean) -> Unit)? = null,
    cacheSizeBytes: Long = 0L,
    isClearingCache: Boolean = false,
    onClearCache: () -> Unit = {},
    onRefreshCacheSize: () -> Unit = {},
) {
    var showNotesDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onRefreshCacheSize()
    }

    val isGlass = LocalGlass.current.enabled
    val settingsAccent = lerp(LocalAccent.current, Color.White, 0.35f)
    CompositionLocalProvider(LocalAccent provides settingsAccent) {
        Column(Modifier.fillMaxSize().background(if (isGlass) Color.Transparent else CanopyColors.Chrome)) {
            Text(
                stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = CanopyColors.Text,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
            )
            Box(Modifier.fillMaxWidth().height(0.5.dp).background(CanopyColors.Rule))
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 4.dp)) {
                // Account leads: it is the only row whose state the user cannot infer at a glance.
                Spacer(Modifier.height(20.dp))
                AccountCard(auth, onSignIn, onSwitchAccount, onSignOut)

                SectionLabel(stringResource(R.string.settings_section_language))
                SettingsPanel {
                    LanguageRow()
                }

                SectionLabel(stringResource(R.string.settings_section_audio))
                SettingsPanel {
                    QualityRow(settings.audioQuality) { onSettings(settings.copy(audioQuality = it)) }
                    PanelDivider()
                    EqualizerRow(settings, onSettings)
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_show_bitrate),
                        subtitle = stringResource(R.string.settings_show_bitrate_subtitle),
                        checked = settings.showBitrate,
                        onChecked = { onSettings(settings.copy(showBitrate = it)) },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_volume_norm),
                        subtitle = stringResource(R.string.settings_volume_norm_subtitle),
                        checked = settings.volumeNormalizationEnabled,
                        onChecked = { onSettings(settings.copy(volumeNormalizationEnabled = it)) },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_autoplay),
                        subtitle = stringResource(R.string.settings_autoplay_subtitle),
                        checked = settings.autoplayEnabled,
                        onChecked = { enabled ->
                            onAutoplayEnabled?.invoke(enabled) ?: onSettings(settings.copy(autoplayEnabled = enabled))
                        },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_auto_download_on_play),
                        subtitle = stringResource(R.string.settings_auto_download_on_play_subtitle),
                        checked = settings.autoDownloadOnPlay,
                        onChecked = { onSettings(settings.copy(autoDownloadOnPlay = it)) },
                    )
                    PanelDivider()
                    CrossfadeRow(settings, onSettings)
                    PanelDivider()
                    CacheSizeRow(settings, onSettings)
                    PanelDivider()
                    ClearCacheRow(
                        cacheSizeBytes = cacheSizeBytes,
                        isClearing = isClearingCache,
                        onClear = onClearCache,
                    )
                }

                SectionLabel(stringResource(R.string.settings_section_integrations))
                SettingsPanel {
                    DiscordSettingsCard(
                        settings = settings,
                        discordAuth = discordAuth,
                        discordConnection = discordConnection,
                        onSettings = onSettings,
                        onConnect = onConnectDiscord,
                        onDisconnect = onDisconnectDiscord,
                    )
                    PanelDivider()
                    LastfmSettingsCard(
                        state = lastfmState,
                        onConnect = onConnectLastfm,
                        onComplete = onCompleteLastfm,
                        onDisconnect = onDisconnectLastfm,
                    )
                    PanelDivider()
                    ListenBrainzSettingsCard(
                        state = listenBrainzState,
                        onConnect = onConnectListenBrainz,
                        onDisconnect = onDisconnectListenBrainz,
                    )
                    PanelDivider()
                    SpotifySettingsCard(
                        settings = settings,
                        onSettings = onSettings,
                        onConnectSpotify = onConnectSpotify,
                    )
                    PanelDivider()
                    QobuzSettingsCard(
                        status = qobuzStatus,
                        onConnect = onConnectQobuz,
                        onDisconnect = onDisconnectQobuz,
                        onEnabledChange = onQobuzEnabledChange,
                        onQualityChange = onQobuzQualityChange,
                    )
                    PanelDivider()
                    VeyraAccountSettingsCard(
                        settings = settings,
                        onSettings = onSettings,
                    )
                }

                SectionLabel(stringResource(R.string.settings_section_appearance))
                SettingsPanel {
                    PlayerModeRow(settings.playerMode) { onSettings(settings.copy(playerMode = it)) }
                    PanelDivider()
                    ActionRow(
                        title = stringResource(R.string.settings_home_layout),
                        subtitle = stringResource(R.string.settings_home_layout_subtitle),
                        onClick = onHomeLayout,
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_player_gestures),
                        subtitle = stringResource(R.string.settings_player_gestures_subtitle),
                        checked = settings.playerGesturesEnabled,
                        onChecked = { onSettings(settings.copy(playerGesturesEnabled = it)) },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_system_colors),
                        subtitle = stringResource(R.string.settings_system_colors_subtitle),
                        checked = settings.useSystemColors,
                        onChecked = { onSettings(settings.copy(useSystemColors = it)) },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_animated_artwork),
                        subtitle = stringResource(R.string.settings_animated_artwork_subtitle),
                        checked = settings.animatedArtwork,
                        onChecked = { onSettings(settings.copy(animatedArtwork = it)) },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_download_artwork),
                        subtitle = stringResource(R.string.settings_download_artwork_subtitle),
                        checked = settings.downloadAnimatedArtwork,
                        onChecked = { onSettings(settings.copy(downloadAnimatedArtwork = it)) },
                    )
                    PanelDivider()
                    ToggleRow(
                        title = stringResource(R.string.settings_animated_bg),
                        subtitle = stringResource(R.string.settings_animated_bg_subtitle),
                        checked = settings.animatedBackground,
                        onChecked = { onSettings(settings.copy(animatedBackground = it)) },
                    )
                }

                SectionLabel(stringResource(R.string.settings_section_experimental))
                SettingsPanel {
                    ToggleRow(
                        title = stringResource(R.string.settings_frosted_glass),
                        subtitle = stringResource(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                R.string.settings_frosted_glass_subtitle
                            } else {
                                R.string.settings_frosted_glass_legacy_subtitle
                            }
                        ),
                        checked = settings.frostedGlass,
                        onChecked = { onSettings(settings.copy(frostedGlass = it)) },
                    )
                }

                SectionLabel(stringResource(R.string.settings_section_devices))
                SettingsPanel {
                    ActionRow(
                        title = stringResource(R.string.settings_manage_devices),
                        subtitle = stringResource(R.string.settings_manage_devices_subtitle),
                        onClick = onDevices,
                    )
                    PanelDivider()
                    ChromecastSettingsRow()
                }

                SectionLabel(stringResource(R.string.settings_section_updates))
                SettingsPanel {
                    val availableUpdate = (updateState as? UpdateState.Available)?.metadata
                    if (availableUpdate != null) {
                        ActionRow(
                            title = stringResource(R.string.settings_update_available, availableUpdate.version),
                            subtitle = if (availableUpdate.codename.isNotBlank()) {
                                "\"${availableUpdate.codename}\" • ${stringResource(R.string.settings_tap_to_install)}"
                            } else {
                                stringResource(R.string.settings_tap_to_install)
                            },
                            onClick = { onInstallUpdate(availableUpdate) },
                        )
                        PanelDivider()
                        ActionRow(
                            title = stringResource(R.string.settings_release_notes),
                            subtitle = stringResource(R.string.settings_release_notes_version, availableUpdate.version),
                            onClick = { showNotesDialog = true },
                        )
                    } else {
                        val cleanVersionName = BuildConfig.VERSION_NAME.replace("-debug", "").replace("debug", "").trim()
                        ActionRow(
                            title = stringResource(R.string.settings_release_notes),
                            subtitle = if (!BuildConfig.UPDATER_ENABLED) {
                                stringResource(R.string.settings_updates_disabled, cleanVersionName)
                            } else if (settings.betaChannelEnabled) {
                                stringResource(
                                    R.string.upd_beta_status_format,
                                    cleanVersionName,
                                    stringResource(R.string.widget_ready),
                                )
                            } else {
                                stringResource(R.string.settings_up_to_date, cleanVersionName)
                            },
                            onClick = { showNotesDialog = true },
                        )
                        if (BuildConfig.UPDATER_ENABLED) {
                            PanelDivider()
                            ActionRow(
                                title = stringResource(R.string.settings_check_updates),
                                subtitle = stringResource(R.string.settings_check_updates_subtitle),
                                onClick = onCheckForUpdates,
                            )
                        }
                    }
                    if (BuildConfig.UPDATER_ENABLED) {
                        PanelDivider()
                        ToggleRow(
                            title = stringResource(R.string.settings_beta_channel),
                            subtitle = stringResource(R.string.settings_beta_channel_subtitle),
                            checked = settings.betaChannelEnabled,
                            onChecked = {
                                val updated = settings.copy(betaChannelEnabled = it)
                                onSettings(updated)
                            },
                        )
                    }
                }

                SectionLabel(stringResource(R.string.settings_section_guide))
                SettingsPanel {
                    ActionRow(
                        title = stringResource(R.string.settings_welcome_guide),
                        subtitle = stringResource(R.string.settings_welcome_guide_subtitle),
                        onClick = onWelcome,
                    )
                }

                val cleanVersionName = BuildConfig.VERSION_NAME.replace("-debug", "").replace("debug", "").trim()
                val versionText = if (BuildConfig.CODENAME.isNotBlank()) {
                    stringResource(
                        R.string.upd_version_footer_codename,
                        cleanVersionName,
                        BuildConfig.CODENAME,
                    )
                } else {
                    stringResource(R.string.upd_version_footer, cleanVersionName)
                }
                Text(
                    versionText,
                    color = CanopyColors.Eyebrow,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(OrchardChromeHeight))
            }
        }
    }

    if (showNotesDialog) {
        val available = (updateState as? UpdateState.Available)?.metadata
        if (available != null) {
            UpdateDialog(
                state = updateState,
                onInstall = {
                    showNotesDialog = false
                    onInstallUpdate(it)
                },
                onDismiss = { showNotesDialog = false },
            )
        } else {
            ReleaseNotesDialog(
                version = BuildConfig.VERSION_NAME,
                codename = BuildConfig.CODENAME,
                releaseNotes = MobileChangelog.CURRENT_RELEASE_NOTES,
                onDismiss = { showNotesDialog = false },
            )
        }
    }
}

/** Signed-in identity and the sign in / out affordance, given more weight than a plain row. */
@Composable
private fun AccountCard(
    auth: AuthState,
    onSignIn: () -> Unit,
    onSwitchAccount: () -> Unit,
    onSignOut: () -> Unit,
) {
    val isGlass = LocalGlass.current.enabled
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .shadow(elevation = if (isGlass) 4.dp else 10.dp, shape = SettingsPanelShape, spotColor = Color(0x66000000), ambientColor = Color(0x33000000))
            .then(
                if (isGlass) {
                    Modifier.glassPane(SettingsPanelShape, GlassTone.PANEL)
                } else {
                    Modifier
                        .clip(SettingsPanelShape)
                        .background(Brush.verticalGradient(listOf(Color(0xFF151D2D), Color(0xFF0F1420))))
                        .border(1.dp, Brush.verticalGradient(listOf(Color(0x28FFFFFF), Color(0x0AFFFFFF))), SettingsPanelShape)
                }
            ),
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val avatarUrl = (auth as? AuthState.SignedIn)?.avatarUrl.orEmpty()
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF080C14))
                        .border(1.dp, Color(0x24FFFFFF), CircleShape)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                        )
                    } else {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = null,
                            tint = LocalAccent.current,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                }
                Spacer(Modifier.size(16.dp))
                Column(Modifier.weight(1f)) {
                    when (auth) {
                        AuthState.Restoring -> AccountText(
                            stringResource(R.string.acct_restoring_session),
                            stringResource(R.string.acct_one_moment),
                        )
                        AuthState.Authorizing -> AccountText(
                            stringResource(R.string.acct_completing_sign_in),
                            stringResource(R.string.acct_one_moment),
                        )
                        AuthState.SignedOut -> AccountText(
                            stringResource(R.string.acct_no_session),
                            stringResource(R.string.acct_no_session_desc),
                        )
                        is AuthState.SignedIn -> AccountText(
                            auth.displayName,
                            stringResource(R.string.acct_signed_in_label),
                        )
                        is AuthState.Error -> AccountText(
                            stringResource(R.string.acct_sign_in_error),
                            auth.message,
                            titleColor = CanopyColors.Danger,
                        )
                    }
                }
            }

            when (auth) {
                AuthState.SignedOut -> {
                    PanelDivider()
                    AccountButton(stringResource(R.string.action_sign_in), onSignIn, primary = true)
                }
                is AuthState.Error -> {
                    PanelDivider()
                    AccountButton(stringResource(R.string.action_retry), onSignIn, primary = true)
                }
                is AuthState.SignedIn -> {
                    PanelDivider()
                    AccountButton(stringResource(R.string.action_switch_account), onSwitchAccount, primary = true)
                    PanelDivider()
                    AccountButton(stringResource(R.string.action_sign_out), onSignOut, primary = false)
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun AccountText(title: String, subtitle: String, titleColor: Color = CanopyColors.Text) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = titleColor,
    )
    Text(
        subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = CanopyColors.Muted,
    )
}

@Composable
private fun AccountButton(label: String, onClick: () -> Unit, primary: Boolean) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            label,
            color = if (primary) LocalAccent.current else CanopyColors.Danger,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
        )
    }
}

/** Accent headings with Sky Flare indicator. */
@Composable
private fun SectionLabel(value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 20.dp, top = 26.dp, bottom = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(width = 3.dp, height = 12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(CanopyColors.SecondaryAccent),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = CanopyColors.Text,
        )
    }
}

private val SettingsPanelShape = RoundedCornerShape(18.dp)

@Composable
private fun SettingsPanel(content: @Composable ColumnScope.() -> Unit) {
    val isGlass = LocalGlass.current.enabled
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .shadow(elevation = if (isGlass) 4.dp else 10.dp, shape = SettingsPanelShape, spotColor = Color(0x66000000), ambientColor = Color(0x33000000))
            .then(
                if (isGlass) {
                    Modifier.glassPane(SettingsPanelShape, GlassTone.PANEL)
                } else {
                    Modifier
                        .clip(SettingsPanelShape)
                        .background(Brush.verticalGradient(listOf(Color(0xFF141C2C), Color(0xFF0F1522))))
                        .border(1.dp, Brush.verticalGradient(listOf(Color(0x24FFFFFF), Color(0x0AFFFFFF))), SettingsPanelShape)
                }
            ),
    ) {
        Column(content = content)
    }
}

/** Hairline groove between rows. */
@Composable
internal fun PanelDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        Color(0x18FFFFFF),
                        Color.Transparent,
                    )
                )
            ),
    )
}

/** Tactile Neumorphic Switch with dish channel and convex disc. */
@Composable
fun NeumorphicSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val thumbOffset by animateFloatAsState(
        targetValue = if (checked) 22f else 0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 420f),
        label = "switchThumb",
    )
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .width(50.dp)
            .height(28.dp)
            .clip(CircleShape)
            .background(Color(0xFF080C14))
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        Color(0x18000000),
                        if (checked) CanopyColors.SecondaryAccent.copy(alpha = 0.35f) else Color(0x18FFFFFF),
                    )
                ),
                CircleShape,
            )
            .then(
                if (onCheckedChange != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onCheckedChange(!checked) }
                } else Modifier
            )
            .padding(2.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(with(density) { thumbOffset.dp.roundToPx() }, 0) }
                .size(24.dp)
                .shadow(elevation = if (checked) 4.dp else 2.dp, shape = CircleShape, spotColor = if (checked) CanopyColors.SecondaryAccent else Color.Black)
                .clip(CircleShape)
                .background(
                    if (checked) {
                        Brush.verticalGradient(listOf(Color(0xFF2E84FE), Color(0xFF104EB6)))
                    } else {
                        Brush.verticalGradient(listOf(Color(0xFF263347), Color(0xFF131B2B)))
                    }
                )
                .border(
                    1.dp,
                    if (checked) Color(0x66FFFFFF) else Color(0x24FFFFFF),
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(
                        if (checked) Color(0xFF38BDF8) else Color(0x4094A3B8)
                    )
            )
        }
    }
}

/** Icon in a tinted rounded tile, shared by every row so the left edge lines up. */
@Composable
internal fun RowIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .background(LocalAccent.current.copy(alpha = 0.14f), RoundedCornerShape(11.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = LocalAccent.current, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onChecked)
            .defaultMinSize(minHeight = 64.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                color = CanopyColors.Text,
            )
            Text(subtitle, color = CanopyColors.Muted, style = MaterialTheme.typography.bodyMedium)
        }
        NeumorphicSwitch(
            checked = checked,
            onCheckedChange = null,
        )
    }
}

@Composable
private fun ActionRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 64.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Column(Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                color = CanopyColors.Text,
            )
            Text(subtitle, color = CanopyColors.Muted, style = MaterialTheme.typography.bodyMedium)
        }
        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = CanopyColors.Muted,
            modifier = Modifier.size(22.dp),
        )
    }
}

/** In-app language as a segmented control; applies process-wide on selection. */
@Composable
private fun LanguageRow() {
    val context = LocalContext.current
    var language by remember { mutableStateOf(AppLocale.current(context)) }
    Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Column {
            Text(
                stringResource(R.string.settings_language),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                color = CanopyColors.Text,
            )
            Text(
                stringResource(R.string.settings_language_subtitle),
                color = CanopyColors.Muted,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(Modifier.height(14.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(Color(0xFF080C14))
                .border(1.dp, Color(0x1AFFFFFF), CircleShape)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            AppLanguage.entries.forEach { option ->
                val isSelected = option == language
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) {
                                Brush.verticalGradient(listOf(Color(0xFF2E84FE), Color(0xFF104EB6)))
                            } else {
                                Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                            }
                        )
                        .then(
                            if (isSelected) {
                                Modifier.border(1.dp, Color(0x66FFFFFF), CircleShape)
                            } else Modifier
                        )
                        .clickable {
                            language = option
                            AppLocale.apply(context, option)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        option.label(context),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else CanopyColors.Muted,
                    )
                }
            }
        }
    }
}

private fun AppLanguage.label(context: Context): String =
    when (this) {
        AppLanguage.SYSTEM -> context.getString(R.string.language_system)
        AppLanguage.ENGLISH -> context.getString(R.string.language_english)
        AppLanguage.SPANISH -> context.getString(R.string.language_spanish)
    }

/** Audio quality as a single segmented control rather than three loose chips. */
@Composable
private fun QualityRow(value: AudioQuality, onChange: (AudioQuality) -> Unit) {
    val context = LocalContext.current
    Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Column(Modifier) {
                Text(
                    stringResource(R.string.settings_audio_quality),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                    color = CanopyColors.Text,
                )
                Text(
                    value.description(context),
                    color = CanopyColors.Muted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(Color(0xFF080C14))
                .border(1.dp, Color(0x1AFFFFFF), CircleShape)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            AudioQuality.entries.forEach { quality ->
                val isSelected = quality == value
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) {
                                Brush.verticalGradient(listOf(Color(0xFF2E84FE), Color(0xFF104EB6)))
                            } else {
                                Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                            }
                        )
                        .then(
                            if (isSelected) {
                                Modifier.border(1.dp, Color(0x66FFFFFF), CircleShape)
                            } else Modifier
                        )
                        .clickable { onChange(quality) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        quality.label(context),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else CanopyColors.Muted,
                    )
                }
            }
        }
    }
}

private fun AudioQuality.label(context: Context): String =
    when (this) {
        AudioQuality.DATA_SAVER -> context.getString(R.string.quality_data_saver)
        AudioQuality.NORMAL -> context.getString(R.string.quality_normal)
        AudioQuality.HIGH -> context.getString(R.string.quality_high)
        AudioQuality.MAX -> context.getString(R.string.quality_max)
    }

private fun AudioQuality.description(context: Context): String =
    when (this) {
        AudioQuality.DATA_SAVER -> context.getString(R.string.quality_data_saver_desc)
        AudioQuality.NORMAL -> context.getString(R.string.quality_normal_desc)
        AudioQuality.HIGH -> context.getString(R.string.quality_high_desc)
        AudioQuality.MAX -> context.getString(R.string.quality_max_desc)
    }

/** Selector de paradigma visual: Grove (Dock flotante neumórfico) vs Obsidian (Rack HUD superior). */
@Composable
private fun PlayerModeRow(value: MobilePlayerMode, onChange: (MobilePlayerMode) -> Unit) {
    val context = LocalContext.current
    Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                    stringResource(R.string.settings_player_mode),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                    color = CanopyColors.Text,
                )
                Text(
                    stringResource(R.string.settings_player_mode_subtitle),
                    color = CanopyColors.Muted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(Color(0xFF080C14))
                .border(1.dp, Color(0x1AFFFFFF), CircleShape)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            MobilePlayerMode.entries.forEach { mode ->
                val isSelected = mode == value
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) {
                                Brush.verticalGradient(listOf(Color(0xFF2E84FE), Color(0xFF104EB6)))
                            } else {
                                Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                            }
                        )
                        .then(
                            if (isSelected) {
                                Modifier.border(1.dp, Color(0x66FFFFFF), CircleShape)
                            } else Modifier
                        )
                        .clickable {
                            if (mode != value) {
                                onChange(mode)
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        mode.label(context),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else CanopyColors.Muted,
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.65f))
                .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (value == MobilePlayerMode.GROVE) Icons.Rounded.MusicNote else Icons.Rounded.GraphicEq,
                contentDescription = null,
                tint = if (value == MobilePlayerMode.GROVE) Color(0xFF38BDF8) else Color(0xFF67E8F9),
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = value.description(context),
                style = MaterialTheme.typography.bodySmall,
                color = CanopyColors.Muted,
            )
        }
    }
}

private fun MobilePlayerMode.label(context: Context): String =
    when (this) {
        MobilePlayerMode.GROVE -> context.getString(R.string.settings_player_mode_grove)
        MobilePlayerMode.OBSIDIAN -> context.getString(R.string.settings_player_mode_obsidian)
    }

private fun MobilePlayerMode.description(context: Context): String =
    when (this) {
        MobilePlayerMode.GROVE -> context.getString(R.string.settings_player_mode_grove_desc)
        MobilePlayerMode.OBSIDIAN -> context.getString(R.string.settings_player_mode_obsidian_desc)
    }
