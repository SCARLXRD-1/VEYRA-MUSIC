/**
 * Veyra Music — Reactive i18n Architecture
 * AKASHI DEV — ESM, Vue 3 Composition API, Zero Bloat
 */

import { ref, computed, inject } from 'vue';
import esMX from './locales/es-MX.js';
import en from './locales/en.js';

export const LOCALE_STORAGE_KEY = 'veyra:locale';
export const DEFAULT_LOCALE = 'es-MX';

const messages = {
  'es-MX': esMX,
  en
};

export const AVAILABLE_LOCALES = [
  { code: 'es-MX', label: 'Español' },
  { code: 'en', label: 'English' }
];

function getStoredLocale() {
  try {
    const stored = window.localStorage?.getItem(LOCALE_STORAGE_KEY);
    if (stored && messages[stored]) return stored;
  } catch {
    // localStorage may be unavailable in some sandboxes
  }
  return DEFAULT_LOCALE;
}

// Reactive global locale state
export const currentLocale = ref(getStoredLocale());

export function setLocale(localeCode) {
  if (messages[localeCode]) {
    currentLocale.value = localeCode;
    try {
      window.localStorage?.setItem(LOCALE_STORAGE_KEY, localeCode);
    } catch {
      // ignore storage errors
    }
  }
}

/**
 * Resolves a nested translation path with fallback and parameter interpolation.
 * e.g. t('welcome.account.signedInAs', { name: 'Alex' })
 */
export function t(path, params = {}) {
  const active = currentLocale.value;
  const activeDict = messages[active] || messages[DEFAULT_LOCALE];
  const fallbackDict = messages[DEFAULT_LOCALE] || messages.en;

  const resolve = (dict, keyPath) => {
    return keyPath.split('.').reduce((obj, key) => (obj && obj[key] !== undefined ? obj[key] : null), dict);
  };

  let value = resolve(activeDict, path);
  if (value === null || value === undefined) {
    value = resolve(fallbackDict, path);
  }
  if (value === null || value === undefined) {
    // Return last segment of path as fallback rather than empty
    const segments = path.split('.');
    return segments[segments.length - 1];
  }

  if (typeof value === 'string' && params && typeof params === 'object') {
    return value.replace(/\{(\w+)\}/g, (_, k) => (params[k] !== undefined ? params[k] : `{${k}}`));
  }

  return value;
}

/**
 * Composition API Composable for i18n
 */
export function useI18n() {
  return {
    locale: currentLocale,
    t,
    setLocale,
    availableLocales: AVAILABLE_LOCALES
  };
}

/**
 * Vue Plugin for global registration
 */
export const i18nPlugin = {
  install(app) {
    app.config.globalProperties.$t = (path, params) => t(path, params);
    app.config.globalProperties.$setLocale = (loc) => setLocale(loc);
    app.config.globalProperties.$locale = currentLocale;
    app.provide('i18n', { locale: currentLocale, t, setLocale });
  }
};

export default i18nPlugin;
