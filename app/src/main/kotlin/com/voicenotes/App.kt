package com.voicenotes

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import com.voicenotes.worker.DownloadModelWorker
import com.voicenotes.worker.TranscriptionWorker
import com.voicenotes.BuildConfig

@HiltAndroidApp
class App : Application() {
    companion object {
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        // Инициализация быстрая, без автоматического скачивания моделей.
        // Модели скачиваются вручную из экрана настроек или при OFFLINE‑распознавании.
    }
}