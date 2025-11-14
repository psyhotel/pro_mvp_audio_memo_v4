package com.voicenotes.worker

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.BackoffPolicy
import android.content.Context
import android.util.Log
import com.voicenotes.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class TranscriptionWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val t0 = System.currentTimeMillis()
        val noteId = inputData.getLong(KEY_NOTE_ID, -1L)
        val filePath = inputData.getString(KEY_FILE_PATH)
        val url = inputData.getString(KEY_TRANSCRIBE_URL) ?: ""
        val mode = inputData.getString(KEY_MODE) ?: MODE_ONLINE
        val lang = inputData.getString(KEY_LANG) ?: LANG_RU

        if (noteId <= 0 || filePath.isNullOrEmpty()) {
            return Result.failure()
        }

        val db = AppDatabase.getDatabase()
        val dao = db.noteDao()

        if (mode == MODE_ONLINE && url.isBlank()) {
            dao.updateNoteContent(noteId, "Транскрибация не настроена")
            return Result.success()
        }

        val file = File(filePath)
        if (!file.exists()) {
            dao.updateNoteContent(noteId, "Аудиофайл не найден")
            return Result.success()
        }

        val resultText = withContext(Dispatchers.IO) {
            if (mode == MODE_OFFLINE) transcribeOfflineVosk(file, lang) else uploadAndTranscribe(url, file, noteId)
        }
        val dt = System.currentTimeMillis() - t0
        Log.d("TranscriptionWorker", "mode=$mode lang=$lang duration_ms=$dt noteId=$noteId")
        if (resultText != null && resultText.isNotBlank()) {
            dao.updateNoteContent(noteId, resultText)
            return Result.success()
        } else {
            dao.updateNoteContent(noteId, "Не удалось распознать речь")
            return Result.retry()
        }
    }

    private fun uploadAndTranscribe(endpoint: String, file: File, noteId: Long): String? {
        val boundary = UUID.randomUUID().toString()
        val url = URL(endpoint)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            doOutput = true
            doInput = true
            useCaches = false
            connectTimeout = 30000
            readTimeout = 30000
            setChunkedStreamingMode(8192)
        }

        DataOutputStream(conn.outputStream).use { output ->
            output.writeBytes("--$boundary\r\n")
            output.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"${file.name}\"\r\n")
            val mime = if (file.extension.lowercase() == "wav") "audio/wav" else "audio/m4a"
            output.writeBytes("Content-Type: $mime\r\n\r\n")
            file.inputStream().use { input ->
                input.copyTo(output)
            }
            output.writeBytes("\r\n--$boundary\r\n")
            output.writeBytes("Content-Disposition: form-data; name=\"note_id\"\r\n\r\n")
            output.writeBytes(noteId.toString())
            output.writeBytes("\r\n--$boundary\r\n")
            output.writeBytes("Content-Disposition: form-data; name=\"device\"\r\n\r\n")
            output.writeBytes(android.os.Build.MODEL ?: "unknown")
            output.writeBytes("\r\n--$boundary--\r\n")
            output.flush()
        }

        val code = conn.responseCode
        Log.d("TranscriptionWorker", "upload code=$code noteId=$noteId")
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val response = BufferedReader(InputStreamReader(stream)).use { it.readText() }
        return try {
            JSONObject(response).optString("text", null)
        } catch (e: Exception) {
            Log.e("TranscriptionWorker", "parse error", e)
            null
        } finally {
            conn.disconnect()
        }
    }

    companion object {
        const val KEY_NOTE_ID = "note_id"
        const val KEY_FILE_PATH = "file_path"
        const val KEY_TRANSCRIBE_URL = "transcribe_url"
        const val KEY_MODE = "mode"
        const val KEY_LANG = "lang"
        const val MODE_ONLINE = "ONLINE"
        const val MODE_OFFLINE = "OFFLINE"
        const val LANG_RU = "ru"
        const val LANG_EN = "en"

        fun enqueue(context: Context, noteId: Long, filePath: String, transcribeUrl: String, mode: String, lang: String): UUID {
            val data = Data.Builder()
                .putLong(KEY_NOTE_ID, noteId)
                .putString(KEY_FILE_PATH, filePath)
                .putString(KEY_TRANSCRIBE_URL, transcribeUrl)
                .putString(KEY_MODE, mode)
                .putString(KEY_LANG, lang)
                .build()

            val constraints = if (mode == MODE_ONLINE) {
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            } else {
                Constraints.Builder().build()
            }

            val request = OneTimeWorkRequestBuilder<TranscriptionWorker>()
                .setConstraints(constraints)
                .setInputData(data)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10_000, java.util.concurrent.TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueue(request)
            return request.id
        }
    }

    private fun transcribeOfflineVosk(file: File, lang: String): String? {
        val base = applicationContext.getExternalFilesDir(null)?.absolutePath ?: return null
        val modelDir = File(base, "vosk/${lang}")
        if (!modelDir.exists()) {
            Log.e("TranscriptionWorker", "model missing: ${modelDir.absolutePath}")
            return null
        }
        val sampleRate = 16000f
        val model = Model(modelDir.absolutePath)
        val recognizer = Recognizer(model, sampleRate)
        file.inputStream().use { input ->
            val header = ByteArray(44)
            if (input.read(header) != 44) return null
            val buffer = ByteArray(4096)
            while (true) {
                val read = input.read(buffer)
                if (read <= 0) break
                recognizer.acceptWaveForm(buffer, read)
            }
        }
        val result = recognizer.finalResult
        return try {
            JSONObject(result).optString("text", null)
        } catch (e: Exception) {
            null
        }
    }
}
