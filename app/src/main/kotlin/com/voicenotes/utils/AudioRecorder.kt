package com.voicenotes.utils

import android.media.MediaPlayer
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AudioRecorder {
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private val _amplitude = MutableStateFlow(0)
    val amplitude: StateFlow<Int> = _amplitude

    private var recordingJob: Job? = null // Для отмены корутины записи

    // Запуск записи на фоновых потоках
    suspend fun start(outputPath: String) {
        stop() // Останавливаем предыдущую запись
        // Инициализация и старт MediaRecorder на IO
        withContext(Dispatchers.IO) {
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputPath)
                prepare()
                start()
            }
        }
        // Отдельная корутина для чтения амплитуды без блокировки UI
        recordingJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                try {
                    _amplitude.value = recorder?.maxAmplitude ?: 0
                } catch (_: Exception) { _amplitude.value = 0 }
                delay(50)
            }
        }
    }

    fun stop() {
        recordingJob?.cancel() // Отменяем корутину записи
        try {
            recorder?.apply { stop(); release() }
        } catch (e: Exception) {}
        recorder = null
    }

    fun play(path: String) {
        stopPlay()
        player = MediaPlayer().apply {
            setDataSource(path)
            setOnPreparedListener { it.start() }
            prepareAsync()
        }
    }

    fun stopPlay() {
        player?.release()
        player = null
    }
}