package com.voicenotes.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.voicenotes.BuildConfig

object TranscriptionScheduler {
    fun enqueue(context: Context, noteId: Long, filePath: String, offline: Boolean, lang: String) {
        if (!offline) {
            val settings = com.voicenotes.utils.SettingsManager(context)
            TranscriptionWorker.enqueue(
                context,
                noteId,
                filePath,
                settings.getUrl(BuildConfig.TRANSCRIBE_URL),
                TranscriptionWorker.MODE_ONLINE,
                lang
            )
            return
        }
        val base = context.getExternalFilesDir(null)?.absolutePath ?: return
        val modelDir = java.io.File(base, "vosk/${lang}")
        if (modelDir.exists()) {
            TranscriptionWorker.enqueue(
                context,
                noteId,
                filePath,
                "",
                TranscriptionWorker.MODE_OFFLINE,
                lang
            )
            return
        }
        val url = if (lang == TranscriptionWorker.LANG_RU) BuildConfig.VOSK_RU_URL else BuildConfig.VOSK_EN_URL
        val download = OneTimeWorkRequestBuilder<DownloadModelWorker>()
            .setInputData(
                androidx.work.Data.Builder()
                    .putString(DownloadModelWorker.KEY_LANG, lang)
                    .putString(DownloadModelWorker.KEY_URL, url)
                    .build()
            ).build()
        val transcribe = OneTimeWorkRequestBuilder<TranscriptionWorker>()
            .setInputData(
                androidx.work.Data.Builder()
                    .putLong(TranscriptionWorker.KEY_NOTE_ID, noteId)
                    .putString(TranscriptionWorker.KEY_FILE_PATH, filePath)
                    .putString(TranscriptionWorker.KEY_TRANSCRIBE_URL, "")
                    .putString(TranscriptionWorker.KEY_MODE, TranscriptionWorker.MODE_OFFLINE)
                    .putString(TranscriptionWorker.KEY_LANG, lang)
                    .build()
            ).build()
        WorkManager.getInstance(context)
            .beginUniqueWork("transcribe_${noteId}", ExistingWorkPolicy.REPLACE, download)
            .then(transcribe)
            .enqueue()
    }
}

