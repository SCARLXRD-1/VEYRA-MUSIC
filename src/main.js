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
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Veyra Music. If not, see <https://www.gnu.org/licenses/>.
 */

import { createApp } from 'vue';
import { Quasar } from 'quasar';
import '@quasar/extras/material-icons/material-icons.css';
import 'quasar/dist/quasar.css';
import './styles.css';
import App from './App.vue';
import ExplicitBadge from './components/controls/ExplicitBadge.vue';
import DownloadIndicator from './components/controls/DownloadIndicator.vue';
import StatusPillBadge from './components/controls/StatusPillBadge.vue';
import { i18nPlugin } from './app/i18n/index.js';

createApp(App)
  .use(i18nPlugin)
  .component('ExplicitBadge', ExplicitBadge)
  .component('DownloadIndicator', DownloadIndicator)
  .component('StatusPillBadge', StatusPillBadge)
  .use(Quasar, {
    config: {
      brand: {
        primary: '#1E6CEB',
        secondary: '#38BDF8',
        accent: '#38BDF8',
        dark: '#0B0F19',
        negative: '#EF4444',
        warning: '#F59E0B',
        positive: '#10B981',
        info: '#38BDF8'
      }
    }
  })
  .mount('#app');
