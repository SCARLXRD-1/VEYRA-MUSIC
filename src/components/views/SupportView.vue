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
import { computed, onBeforeUnmount, ref, watch } from 'vue';

export default {
  name: 'SupportView',
  props: { app: { type: Object, required: true } },
  setup(props) {
    const composing = ref(false);
    const reportType = ref('bug');
    const title = ref('');
    const body = ref('');
    const includeDiagnostics = ref(false);
    const diagnosticsCollecting = ref(false);
    const diagnosticsSnapshot = ref(null);
    const screenshot = ref(null);
    const screenshotPreview = ref('');
    const replyBody = ref('');
    const replyScreenshot = ref(null);
    const replyPreview = ref('');
    const fileInput = ref(null);
    const replyFileInput = ref(null);

    let diagnosticsRequest = null;
    const diagnosticsPreview = computed(() => {
      if (diagnosticsSnapshot.value) {
        return JSON.stringify(diagnosticsSnapshot.value, null, 2);
      }
      return diagnosticsCollecting.value
        ? 'Collecting diagnostics…'
        : 'Diagnostics could not be prepared. Refresh to try again.';
    });
    const reportClosed = computed(() => !['open', 'waiting_on_user'].includes(
      props.app.supportActiveReport.value?.status
    ));
    const activeAttachments = computed(() => Object.fromEntries(
      props.app.supportAttachments.value.map((attachment) => [attachment.messageId, attachment])
    ));

    function setImage(target, previewTarget, file) {
      if (!file) return;
      if (!['image/png', 'image/jpeg', 'image/webp'].includes(file.type) || file.size > 5 * 1024 * 1024) {
        props.app.supportMessage.value = 'Screenshots must be PNG, JPEG, or WebP and 5 MiB or smaller.';
        return;
      }
      if (previewTarget.value) URL.revokeObjectURL(previewTarget.value);
      target.value = file;
      previewTarget.value = URL.createObjectURL(file);
    }

    function pickReportImage(event) {
      const input = event.currentTarget;
      setImage(screenshot, screenshotPreview, input?.files?.[0]);
      if (input) input.value = '';
    }

    function pickReplyImage(event) {
      const input = event.currentTarget;
      setImage(replyScreenshot, replyPreview, input?.files?.[0]);
      if (input) input.value = '';
    }

    function imageFromTransfer(event, target, previewTarget) {
      const file = [...(event.clipboardData?.files || event.dataTransfer?.files || [])]
        .find((item) => item.type.startsWith('image/'));
      if (file) {
        event.preventDefault();
        setImage(target, previewTarget, file);
      }
    }

    async function capture(target, previewTarget) {
      try {
        setImage(target, previewTarget, await props.app.captureSupportScreenshot());
      } catch (error) {
        props.app.supportMessage.value = error.message;
      }
    }

    function clearImage(target, previewTarget) {
      if (previewTarget.value) URL.revokeObjectURL(previewTarget.value);
      target.value = null;
      previewTarget.value = '';
    }

    function openFileInput(input) {
      input?.click();
    }

    async function refreshDiagnosticsPreview() {
      if (diagnosticsRequest) return diagnosticsRequest;

      diagnosticsCollecting.value = true;
      diagnosticsRequest = (async () => {
        try {
          await props.app.collectDiagnostics();
          const report = props.app.diagnostics.value.report;
          const snapshot = report
            ? JSON.parse(JSON.stringify(report))
            : null;
          diagnosticsSnapshot.value = includeDiagnostics.value ? snapshot : null;
          if (includeDiagnostics.value && !diagnosticsSnapshot.value) {
            props.app.supportMessage.value = 'Could not prepare diagnostics for review.';
          }
        } catch (error) {
          diagnosticsSnapshot.value = null;
          props.app.supportMessage.value = error.message || 'Could not prepare diagnostics for review.';
        } finally {
          diagnosticsCollecting.value = false;
          diagnosticsRequest = null;
        }
      })();

      return diagnosticsRequest;
    }

    async function submitReport() {
      try {
        if (includeDiagnostics.value && !diagnosticsSnapshot.value) {
          await refreshDiagnosticsPreview();
        }
        if (includeDiagnostics.value && !diagnosticsSnapshot.value) return;

        const sent = await props.app.submitSupportReport({
          type: reportType.value,
          title: title.value,
          body: body.value,
          diagnostics: includeDiagnostics.value ? diagnosticsSnapshot.value : null,
          screenshot: screenshot.value
        });
        if (!sent) return;
        clearImage(screenshot, screenshotPreview);
        title.value = '';
        body.value = '';
        includeDiagnostics.value = false;
        composing.value = false;
      } catch (error) {
        props.app.supportMessage.value = error.message || 'Could not send the report.';
      }
    }

    watch(includeDiagnostics, (enabled) => {
      if (enabled) {
        void refreshDiagnosticsPreview();
      } else {
        diagnosticsSnapshot.value = null;
      }
    });

    async function submitReply() {
      if (!replyBody.value.trim()) return;
      const sent = await props.app.replyToSupportReport(replyBody.value, replyScreenshot.value);
      if (!sent) return;
      replyBody.value = '';
      clearImage(replyScreenshot, replyPreview);
    }

    async function deleteReport() {
      if (!window.confirm('Delete this report, its messages, and screenshots permanently?')) return;
      await props.app.deleteSupportReport();
    }

    function newReport() {
      props.app.closeSupportReport();
      composing.value = true;
      void props.app.loadSupportGithubAuth();
    }

    function showInbox() {
      composing.value = false;
      props.app.closeSupportReport();
    }

    onBeforeUnmount(() => {
      clearImage(screenshot, screenshotPreview);
      clearImage(replyScreenshot, replyPreview);
    });

    return {
      ...props.app,
      activeAttachments,
      body,
      capture,
      clearImage,
      composing,
      deleteReport,
      diagnosticsPreview,
      diagnosticsCollecting,
      fileInput,
      imageFromTransfer,
      includeDiagnostics,
      newReport,
      openFileInput,
      pickReplyImage,
      pickReportImage,
      replyBody,
      replyFileInput,
      replyPreview,
      replyScreenshot,
      reportClosed,
      reportType,
      refreshDiagnosticsPreview,
      screenshot,
      screenshotPreview,
      showInbox,
      submitReply,
      submitReport,
      title
    };
  }
};
</script>

<template>
  <main class="support-view">
    <aside class="support-inbox" :aria-label="$t('supportUi.reports')">
      <div class="support-inbox__toolbar">
        <strong>{{ $t('supportUi.reports') }}</strong>
        <div class="support-inbox__actions">
          <a class="support-secondary-button" :href="supportIssueTrackerUrl" target="_blank" rel="noreferrer">{{ $t('supportUi.currentIssues') }}</a>
          <button type="button" class="support-icon-button" :title="$t('supportUi.refreshReports')" @click="loadSupportReports()">
            <q-icon name="refresh" />
          </button>
        </div>
      </div>
      <button type="button" class="support-new-button" @click="newReport">
        <q-icon name="add" />
        {{ $t('supportUi.newReport') }}
      </button>
      <div class="support-report-list">
        <button
          v-for="report in supportReports"
          :key="report.id"
          type="button"
          class="support-report-row"
          :class="{ 'support-report-row--active': supportActiveReport?.id === report.id, 'support-report-row--unread': report.unread }"
          @click="composing = false; openSupportReport(report)"
        >
          <span class="support-report-row__title">{{ report.title }}</span>
          <span class="support-report-row__meta">
            <span>{{ supportStatusLabel(report.status) }}</span>
            <time>{{ new Date(report.updatedAt).toLocaleDateString() }}</time>
          </span>
        </button>
        <p v-if="!supportReports.length && !supportLoading" class="support-empty-copy">{{ $t('supportUi.noReports') }}</p>
      </div>
    </aside>

    <section v-if="composing" class="support-workspace" aria-labelledby="support-new-title">
      <header class="support-workspace__header">
        <div>
          <h2 id="support-new-title">{{ $t('supportUi.newReport') }}</h2>
          <p>{{ $t('supportUi.reportDescription') }}</p>
        </div>
        <button type="button" class="support-text-button" @click="showInbox">{{ $t('common.cancel') }}</button>
      </header>

      <form class="support-form" @submit.prevent="submitReport" @paste="imageFromTransfer($event, screenshot, screenshotPreview)" @drop.prevent="imageFromTransfer($event, screenshot, screenshotPreview)" @dragover.prevent>
        <label class="support-form__type">
          <span>{{ $t('supportUi.type') }}</span>
          <select v-model="reportType">
            <option value="bug">{{ $t('supportUi.bug') }}</option>
            <option value="feature">{{ $t('supportUi.featureRequest') }}</option>
            <option value="artist_page">{{ $t('supportUi.artistPageRequest') }}</option>
            <option value="feedback">{{ $t('supportUi.feedback') }}</option>
          </select>
        </label>
        <label>
          <span>{{ $t('supportUi.title') }}</span>
          <input v-model="title" required maxlength="140" :placeholder="$t('supportUi.shortSummary')" />
        </label>
        <label>
          <span>{{ $t('supportUi.whatHappened') }}</span>
          <textarea v-model="body" required maxlength="12000" rows="8" :placeholder="$t('supportUi.describeExpected')"></textarea>
        </label>

        <div class="support-attachment-row">
          <input ref="fileInput" hidden type="file" accept="image/png,image/jpeg,image/webp" @change="pickReportImage" />
          <button type="button" class="support-secondary-button" @click="openFileInput(fileInput)"><q-icon name="attach_file" />{{ $t('supportUi.chooseImage') }}</button>
          <button type="button" class="support-secondary-button" @click="capture(screenshot, screenshotPreview)"><q-icon name="screenshot_monitor" />{{ $t('supportUi.captureVeyra') }}</button>
          <span>{{ $t('supportUi.imageHelp') }}</span>
        </div>
        <div v-if="screenshotPreview" class="support-image-preview">
          <img :src="screenshotPreview" :alt="$t('supportUi.screenshotPreview')" />
          <button type="button" @click="clearImage(screenshot, screenshotPreview)">{{ $t('supportUi.remove') }}</button>
        </div>

        <label class="support-check-row">
          <input v-model="includeDiagnostics" type="checkbox" />
          <span>{{ $t('supportUi.includeDiagnostics') }}</span>
        </label>
        <details v-if="includeDiagnostics" open class="support-diagnostics-preview">
          <summary>{{ $t('supportUi.reviewDiagnostics') }}</summary>
          <div class="support-diagnostics-preview__notice">
            <span>
              {{ diagnosticsCollecting
                ? $t('supportUi.preparingAttachment')
                : $t('supportUi.diagnosticsNotice') }}
            </span>
            <button
              type="button"
              class="support-text-button support-diagnostics-preview__refresh"
              :disabled="diagnosticsCollecting || supportSubmitting"
              @click="refreshDiagnosticsPreview"
            >{{ $t('supportUi.refresh') }}</button>
          </div>
          <pre aria-live="polite">{{ diagnosticsPreview }}</pre>
        </details>

        <section class="support-github-auth" aria-labelledby="support-github-title">
          <q-icon name="code" />
          <div>
            <strong id="support-github-title">{{ $t('supportUi.githubAttribution') }}</strong>
            <p v-if="supportGithubAuth.status === 'connected'">
              {{ $t('supportUi.issuePostedAs') }} <b>@{{ supportGithubAuth.login }}</b>.
            </p>
            <p v-else-if="supportGithubAuth.status === 'pending'">
              {{ $t('supportUi.enterGithubCode', { code: supportGithubAuth.userCode }) }}
            </p>
            <p v-else-if="supportGithubAuth.status === 'error'">
              {{ supportGithubAuth.message }}
            </p>
            <p v-else>{{ $t('supportUi.connectGithubHelp') }}</p>
          </div>
          <button
            v-if="supportGithubAuth.status === 'connected'"
            type="button"
            class="support-text-button"
            @click="disconnectSupportGithub"
          >{{ $t('supportUi.disconnect') }}</button>
          <button
            v-else-if="!['pending', 'loading', 'unavailable'].includes(supportGithubAuth.status)"
            type="button"
            class="support-secondary-button"
            @click="connectSupportGithub"
          >{{ $t('supportUi.connectGithub') }}</button>
          <q-spinner v-else-if="['pending', 'loading'].includes(supportGithubAuth.status)" size="20px" />
        </section>

        <div class="support-form__footer">
          <p>{{ $t('supportUi.privacyNotice') }}</p>
          <button type="submit" class="support-primary-button" :disabled="supportSubmitting || diagnosticsCollecting">
            <q-spinner v-if="supportSubmitting" size="16px" />
            <q-icon v-else name="send" />
            {{ $t('supportUi.sendReport') }}
          </button>
        </div>
      </form>
    </section>

    <section v-else-if="supportActiveReport" class="support-workspace support-conversation" aria-labelledby="support-thread-title">
      <header class="support-workspace__header">
        <div>
          <button type="button" class="support-back-button" @click="showInbox"><q-icon name="arrow_back" />{{ $t('supportUi.reportsBack') }}</button>
          <h2 id="support-thread-title">{{ supportActiveReport.title }}</h2>
          <p>
            {{ supportStatusLabel(supportActiveReport.status) }} · {{ supportActiveReport.id.slice(0, 8) }}
            <template v-if="supportActiveReport.externalIssueUrl">
              · <a :href="supportActiveReport.externalIssueUrl" target="_blank" rel="noreferrer">{{ $t('supportUi.publicIssue') }}</a>
            </template>
          </p>
        </div>
        <button type="button" class="support-delete-button" :disabled="supportSubmitting" @click="deleteReport">{{ $t('supportUi.delete') }}</button>
      </header>

      <div class="support-message-list" aria-live="polite">
        <article v-for="message in supportMessages" :key="message.id" class="support-message" :class="`support-message--${message.sender}`">
          <header>
            <strong>{{ message.sender === 'staff' ? $t('supportUi.welcomeTitle') : message.sender === 'system' ? $t('common.appName') : $t('supportUi.you') }}</strong>
            <time>{{ new Date(message.createdAt).toLocaleString() }}</time>
          </header>
          <p>{{ message.body }}</p>
          <img
            v-if="activeAttachments[message.id] && supportAttachmentUrls[activeAttachments[message.id].id]"
            :src="supportAttachmentUrls[activeAttachments[message.id].id]"
            :alt="activeAttachments[message.id].filename"
            class="support-message__image"
          />
        </article>
      </div>

      <form v-if="!reportClosed" class="support-reply" @submit.prevent="submitReply" @paste="imageFromTransfer($event, replyScreenshot, replyPreview)" @drop.prevent="imageFromTransfer($event, replyScreenshot, replyPreview)" @dragover.prevent>
        <textarea v-model="replyBody" required maxlength="12000" rows="3" :placeholder="$t('supportUi.replyPlaceholder')"></textarea>
        <div v-if="replyPreview" class="support-reply__preview">
          <img :src="replyPreview" :alt="$t('supportUi.replyScreenshotPreview')" />
          <button type="button" @click="clearImage(replyScreenshot, replyPreview)">{{ $t('supportUi.remove') }}</button>
        </div>
        <div class="support-reply__actions">
          <input ref="replyFileInput" hidden type="file" accept="image/png,image/jpeg,image/webp" @change="pickReplyImage" />
          <button type="button" class="support-icon-button" :title="$t('supportUi.attachImage')" @click="openFileInput(replyFileInput)"><q-icon name="attach_file" /></button>
          <button type="button" class="support-icon-button" :title="$t('supportUi.captureVeyra')" @click="capture(replyScreenshot, replyPreview)"><q-icon name="screenshot_monitor" /></button>
          <span></span>
          <button type="submit" class="support-primary-button" :disabled="supportSubmitting || !replyBody.trim()">{{ $t('supportUi.sendReply') }}</button>
        </div>
      </form>
      <p v-else class="support-closed-note">{{ $t('supportUi.closedReport') }}</p>
    </section>

    <section v-else class="support-workspace support-welcome">
      <q-icon name="support_agent" />
      <h2>{{ $t('supportUi.welcomeTitle') }}</h2>
      <p>{{ $t('supportUi.welcomeDescription') }}</p>
      <a class="support-secondary-button" :href="supportIssueTrackerUrl" target="_blank" rel="noreferrer">{{ $t('supportUi.currentIssues') }}</a>
      <button type="button" class="support-primary-button" @click="newReport">{{ $t('supportUi.createReport') }}</button>
    </section>

    <div v-if="supportMessage" class="support-toast" role="status">{{ supportMessage }}</div>
  </main>
</template>
