package com.voicenotes.utils

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class WavAudioRecorder {
    private var audioRecord: AudioRecord? = null
    private var writeJob: Job? = null
    private var outStream: FileOutputStream? = null
    private var dataSize: Int = 0

    suspend fun start(outputPath: String) {
        stop()
        val sampleRate = 16000
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val minBuffer = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        val bufferSize = minBuffer.coerceAtLeast(2048)
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, bufferSize)
        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            audioRecord?.release()
            audioRecord = null
            return
        }
        val file = File(outputPath)
        outStream = FileOutputStream(file)
        writeWaveHeader(outStream!!, sampleRate, 1, 16)
        dataSize = 0
        audioRecord?.startRecording()
        writeJob = CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(bufferSize)
            while (isActive) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    outStream?.write(buffer, 0, read)
                    dataSize += read
                }
            }
        }
    }

    fun stop() {
        writeJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        outStream?.let {
            finalizeWaveHeader(it, dataSize)
            it.flush()
            it.close()
        }
        outStream = null
        dataSize = 0
    }

    private fun writeWaveHeader(out: FileOutputStream, sampleRate: Int, channels: Int, bitsPerSample: Int) {
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
        header.putInt(0x52494646) // RIFF
        header.putInt(0) // Placeholder for chunk size
        header.putInt(0x57415645) // WAVE
        header.putInt(0x666d7420) // fmt 
        header.putInt(16) // Subchunk1Size
        header.putShort(1) // AudioFormat PCM
        header.putShort(channels.toShort())
        header.putInt(sampleRate)
        header.putInt(byteRate)
        header.putShort((channels * bitsPerSample / 8).toShort()) // BlockAlign
        header.putShort(bitsPerSample.toShort())
        header.putInt(0x64617461) // data
        header.putInt(0) // Placeholder for data size
        out.write(header.array())
    }

    private fun finalizeWaveHeader(out: FileOutputStream, pcmDataSize: Int) {
        val totalDataLen = 36 + pcmDataSize
        val header = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
        header.putInt(totalDataLen)
        header.putInt(pcmDataSize)
        out.channel.position(4)
        out.channel.write(ByteBuffer.wrap(header.array(), 0, 4))
        out.channel.position(40)
        out.channel.write(ByteBuffer.wrap(header.array(), 4, 4))
    }
}
