<!--
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
-->

<script>
import { ref, onMounted, onUnmounted } from 'vue';
import { supabaseClient } from '../../services/supabaseClient.js';
import { CLOUD_SYNC_DISCLAIMER } from '../../services/cloudAnalysisSync.js';

export default {
  name: 'VeyraAccountSection',
  props: { app: { type: Object, required: true } },
  setup() {
    const isConfigured = ref(supabaseClient.isConfigured());
    const user = ref(supabaseClient.getUser());
    const email = ref('');
    const password = ref('');
    const customUrl = ref(supabaseClient.getUrl());
    const customAnonKey = ref(supabaseClient.getAnonKey());
    const showConfig = ref(!supabaseClient.isConfigured());
    const errorMessage = ref('');
    const statusMessage = ref('');
    const isSubmitting = ref(false);

    let unsubscribe = null;

    onMounted(() => {
      unsubscribe = supabaseClient.onAuthStateChange(({ user: u, configured: c }) => {
        user.value = u;
        isConfigured.value = c;
      });
    });

    onUnmounted(() => {
      if (unsubscribe) unsubscribe();
    });

    function saveConfig() {
      supabaseClient.setConfig({
        url: customUrl.value,
        anonKey: customAnonKey.value
      });
      isConfigured.value = supabaseClient.isConfigured();
      statusMessage.value = 'Configuration saved.';
      setTimeout(() => { statusMessage.value = ''; }, 3000);
    }

    async function handleSignIn() {
      errorMessage.value = '';
      isSubmitting.value = true;
      try {
        await supabaseClient.signInWithPassword(email.value, password.value);
        statusMessage.value = 'Signed in successfully.';
        email.value = '';
        password.value = '';
      } catch (err) {
        errorMessage.value = err.message || 'Sign in failed';
      } finally {
        isSubmitting.value = false;
      }
    }

    async function handleSignUp() {
      errorMessage.value = '';
      isSubmitting.value = true;
      try {
        const result = await supabaseClient.signUp(email.value, password.value);
        if (result?.access_token || supabaseClient.getUser()) {
          statusMessage.value = 'Account created and signed in.';
          email.value = '';
          password.value = '';
        } else {
          statusMessage.value = 'Account created! If confirmation is required, check your email, then sign in with your password.';
          password.value = '';
        }
      } catch (err) {
        errorMessage.value = err.message || 'Sign up failed';
      } finally {
        isSubmitting.value = false;
      }
    }

    async function handleSignOut() {
      await supabaseClient.signOut();
      statusMessage.value = 'Signed out.';
      setTimeout(() => { statusMessage.value = ''; }, 3000);
    }

    return {
      isConfigured,
      user,
      email,
      password,
      customUrl,
      customAnonKey,
      showConfig,
      errorMessage,
      statusMessage,
      isSubmitting,
      disclaimer: CLOUD_SYNC_DISCLAIMER,
      saveConfig,
      handleSignIn,
      handleSignUp,
      handleSignOut
    };
  }
};
</script>

<template>
  <div class="settings-row settings-row--options" id="settings-veyra-account">
    <div class="settings-row__copy">
      <label>{{ $t('ui.account.title') }}</label>
      <p v-if="user">
        {{ $t('ui.account.signedInAs', { email: user.email }) }}
      </p>
      <p v-else-if="isConfigured">
        {{ $t('ui.account.signInHelp') }}
      </p>
      <p v-else>
        {{ $t('ui.account.configureHelp') }}
      </p>

      <div class="disclaimer-box">
        <q-icon name="info" class="disclaimer-icon" />
        <span class="disclaimer-text">{{ $t('ui.account.disclaimer') }}</span>
      </div>

      <small v-if="statusMessage" class="settings-status-message settings-status-message--success">
        {{ statusMessage }}
      </small>
      <small v-if="errorMessage" class="settings-status-message settings-status-message--danger">
        {{ errorMessage }}
      </small>

      <!-- Sign In / Sign Up Form -->
      <div v-if="!user && isConfigured" class="auth-form-row">
        <input
          v-model="email"
          type="email"
          :placeholder="$t('ui.account.email')"
          class="settings-input auth-input"
        />
        <input
          v-model="password"
          type="password"
          :placeholder="$t('ui.account.password')"
          class="settings-input auth-input"
        />
        <div class="auth-btn-group">
          <button
            type="button"
            class="settings-button"
            :disabled="!email || !password || isSubmitting"
            @click="handleSignIn"
          >
            {{ $t('ui.account.signIn') }}
          </button>
          <button
            type="button"
            class="settings-link-button"
            :disabled="!email || !password || isSubmitting"
            @click="handleSignUp"
          >
            {{ $t('ui.account.createAccount') }}
          </button>
        </div>
      </div>

      <!-- Advanced Supabase Config Toggle -->
      <div class="config-toggle-row">
        <button
          type="button"
          class="settings-link-button"
          @click="showConfig = !showConfig"
        >
          {{ $t(showConfig ? 'ui.account.hideCloudSettings' : 'ui.account.customSupabase') }}
        </button>
      </div>

      <div v-if="showConfig" class="config-fields">
        <input
          v-model="customUrl"
          type="text"
          :placeholder="$t('ui.account.supabaseUrl')"
          class="settings-input config-input"
        />
        <input
          v-model="customAnonKey"
          type="password"
          :placeholder="$t('ui.account.supabaseKey')"
          class="settings-input config-input"
        />
        <button
          type="button"
          class="settings-button"
          @click="saveConfig"
        >
          {{ $t('ui.account.saveCloudSettings') }}
        </button>
      </div>
    </div>

    <div class="settings-actions account-actions">
      <button
        v-if="user"
        type="button"
        class="settings-link-button settings-link-button--danger"
        @click="handleSignOut"
      >
        {{ $t('ui.account.signOut') }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.disclaimer-box {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  margin-top: 8px;
  padding: 8px 10px;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 6px;
  border-left: 3px solid rgba(255, 255, 255, 0.2);
}

.disclaimer-icon {
  font-size: 14px;
  margin-top: 2px;
  color: #7b9fe8;
}

.disclaimer-text {
  font-size: 11px;
  line-height: 1.4;
  color: #a8b0ad;
}

.auth-form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.auth-input {
  min-width: 180px;
  flex: 1;
}

.auth-btn-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

.config-toggle-row {
  margin-top: 10px;
}

.config-fields {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 8px;
  max-width: 400px;
}

.config-input {
  width: 100%;
}

.settings-status-message--success {
  color: #4cd964;
}

.settings-status-message--danger {
  color: #ff5252;
}

.account-actions {
  justify-content: flex-end;
  padding-top: 0;
}
</style>
