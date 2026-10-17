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

import androidx.compose.foundation.layout.*
import dev.sfg.veyra.mobile.ui.scroll.OrchardLazyColumn as LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.sfg.veyra.connect.R
import dev.sfg.veyra.mobile.model.BuiltInHomeSection
import dev.sfg.veyra.mobile.model.HomeSectionConfig
import dev.sfg.veyra.mobile.model.OrchardSettings
import dev.sfg.veyra.mobile.ui.glass.glassFill
import dev.sfg.veyra.mobile.ui.glass.glassPane
import dev.sfg.veyra.mobile.ui.theme.CanopyColors
import dev.sfg.veyra.mobile.ui.theme.LocalAccent
import dev.sfg.veyra.mobile.auth.AuthState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsHomeLayout(
    settings: OrchardSettings,
    auth: AuthState,
    onSettings: (OrchardSettings) -> Unit,
    onBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.homelayout_tab_online),
        stringResource(R.string.homelayout_tab_offline),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_home_layout)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.homelayout_back_cd),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CanopyColors.Chrome,
                    titleContentColor = CanopyColors.Text,
                    navigationIconContentColor = CanopyColors.Text
                )
            )
        },
        containerColor = CanopyColors.Chrome
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = CanopyColors.Chrome,
                contentColor = CanopyColors.Text,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(selectedTabIndex, matchContentSize = true),
                        color = LocalAccent.current
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.SemiBold) },
                        selectedContentColor = LocalAccent.current,
                        unselectedContentColor = CanopyColors.Muted
                    )
                }
            }

            val currentList = if (selectedTabIndex == 0) settings.homeLayoutOnline else settings.homeLayoutOffline

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        stringResource(R.string.settings_home_layout_subtitle),
                        color = CanopyColors.Muted,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                itemsIndexed(currentList) { index, config ->
                    SectionItemRow(
                        config = config,
                        title = config.section.title(auth),
                        isFirst = index == 0,
                        isLast = index == currentList.size - 1,
                        onMoveUp = {
                            val newList = currentList.toMutableList()
                            val temp = newList[index - 1]
                            newList[index - 1] = newList[index]
                            newList[index] = temp
                            if (selectedTabIndex == 0) onSettings(settings.copy(homeLayoutOnline = newList))
                            else onSettings(settings.copy(homeLayoutOffline = newList))
                        },
                        onMoveDown = {
                            val newList = currentList.toMutableList()
                            val temp = newList[index + 1]
                            newList[index + 1] = newList[index]
                            newList[index] = temp
                            if (selectedTabIndex == 0) onSettings(settings.copy(homeLayoutOnline = newList))
                            else onSettings(settings.copy(homeLayoutOffline = newList))
                        },
                        onToggle = { enabled ->
                            val newList = currentList.toMutableList()
                            newList[index] = config.copy(enabled = enabled)
                            if (selectedTabIndex == 0) onSettings(settings.copy(homeLayoutOnline = newList))
                            else onSettings(settings.copy(homeLayoutOffline = newList))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionItemRow(
    config: HomeSectionConfig,
    title: String,
    isFirst: Boolean,
    isLast: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        color = glassFill(CanopyColors.Surface),
        shape = shape,
        modifier = Modifier.fillMaxWidth().glassPane(shape)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = CanopyColors.Text
                )
            }
            IconButton(onClick = onMoveUp, enabled = !isFirst) {
                Icon(Icons.Rounded.ArrowUpward, contentDescription = stringResource(R.string.homelayout_move_up_cd), tint = if (isFirst) CanopyColors.Muted else CanopyColors.Text)
            }
            IconButton(onClick = onMoveDown, enabled = !isLast) {
                Icon(Icons.Rounded.ArrowDownward, contentDescription = stringResource(R.string.homelayout_move_down_cd), tint = if (isLast) CanopyColors.Muted else CanopyColors.Text)
            }
            Switch(
                checked = config.enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = LocalAccent.current,
                    uncheckedThumbColor = CanopyColors.Muted,
                    uncheckedTrackColor = CanopyColors.Canvas,
                ),
            )
        }
    }
}

@Composable
private fun BuiltInHomeSection.title(auth: AuthState): String = when (this) {
        BuiltInHomeSection.YOUR_PLAYLISTS -> stringResource(R.string.library_your_playlists)
        BuiltInHomeSection.SUBSCRIBED_ARTISTS -> stringResource(R.string.homelayout_subscribed_artists)
        BuiltInHomeSection.TOP_SONGS -> if (auth is AuthState.SignedIn) {
            stringResource(R.string.homelayout_listening_review)
        } else {
            stringResource(R.string.homelayout_top_songs)
        }
        BuiltInHomeSection.RECOMMENDATIONS -> stringResource(R.string.homelayout_recommendations)
        BuiltInHomeSection.DOWNLOADED_PLAYLISTS -> stringResource(R.string.homelayout_downloaded_playlists)
        BuiltInHomeSection.DOWNLOADED_ARTISTS -> stringResource(R.string.homelayout_downloaded_artists)
        BuiltInHomeSection.DOWNLOADED_ALBUMS -> stringResource(R.string.homelayout_downloaded_albums)
        BuiltInHomeSection.DOWNLOADED_SONGS -> stringResource(R.string.homelayout_downloaded_songs)
    }
