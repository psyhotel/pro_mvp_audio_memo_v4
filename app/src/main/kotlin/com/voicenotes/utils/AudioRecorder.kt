package com.voicenotes.utils

import android.media.MediaPlayer
import android.media.MediaRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class AudioRecorder {
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private val _amplitude = MutableStateFlow(0)
    val amplitude: StateFlow<Int> = _amplitude

    suspend fun start(outputPath: String) {
        withContext(Dispatchers.Main) {
            stop()
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputPath)
                prepare()
                start()
            }
            for (i in 0 until 1000) {
                try {
                    val a = recorder?.maxAmplitude ?: 0
                    _amplitude.value = a
                } catch (e: Exception) { _amplitude.value = 0 }
                delay(50)
            }
        }
    }

    fun stop() {
        try {
            recorder?.apply { stop(); release() }
        } catch (e: Exception) {}
        recorder = null
    }

    fun play(path: String) {
        stopPlay()
        player = MediaPlayer().apply {
            setDataSource(path)
            prepare()
            start()
        }
    }

    fun stopPlay() {
        player?.release()
        player = null
    }
}
