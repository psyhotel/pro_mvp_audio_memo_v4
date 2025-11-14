package com.voicenotes.worker

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream
import androidx.work.workDataOf
import androidx.work.ForegroundInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class DownloadModelWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val lang = inputData.getString(KEY_LANG) ?: return Result.failure()
        val url = inputData.getString(KEY_URL) ?: return Result.failure()
        val base = applicationContext.getExternalFilesDir(null)?.absolutePath ?: return Result.failure()
        val destDir = File(base, "vosk/$lang")
        if (destDir.exists()) return Result.success()
        val tmpZip = File(base, "vosk_${lang}.zip")
        ensureChannel()
        setForeground(createForegroundInfo(lang, 0, false, "Загрузка модели"))
        val ok = download(url, tmpZip)
        if (!ok) return Result.retry()
        val okUnzip = unzip(tmpZip, destDir)
        tmpZip.delete()
        if (okUnzip) {
            notifyComplete(lang)
            return Result.success()
        }
        return Result.retry()
    }

    private fun download(src: String, target: File): Boolean {
        return try {
            val u = URL(src)
            val c = (u.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 30000
                readTimeout = 30000
            }
            val code = c.responseCode
            val stream = if (code in 200..299) c.inputStream else c.errorStream
            val total = c.contentLengthLong.takeIf { it > 0 } ?: -1L
            val start = System.currentTimeMillis()
            FileOutputStream(target).use { out ->
                val buf = ByteArray(8192)
                var downloaded = 0L
                while (true) {
                    val r = stream.read(buf)
                    if (r <= 0) break
                    out.write(buf, 0, r)
                    downloaded += r
                    if (total > 0) {
                        val elapsed = System.currentTimeMillis() - start
                        val speed = if (elapsed > 0) downloaded * 1000L / elapsed else 0L
                        val remaining = if (speed > 0) (total - downloaded) * 1000L / speed else -1L
                        setProgressAsync(workDataOf(
                            "stage" to "download",
                            "progress" to ((downloaded * 100) / total).toInt(),
                            "downloaded" to downloaded,
                            "total" to total,
                            "eta_ms" to remaining.toInt()
                        ))
                        setForegroundAsync(createForegroundInfo(inputData.getString(KEY_LANG) ?: "", ((downloaded * 100) / total).toInt(), false, "Загрузка модели"))
                    }
                }
            }
            c.disconnect()
            code in 200..299
        } catch (e: Exception) {
            Log.e("DownloadModelWorker", "download error", e)
            false
        }
    }

    private fun unzip(zipFile: File, destDir: File): Boolean {
        return try {
            destDir.mkdirs()
            ZipInputStream(zipFile.inputStream()).use { zis ->
                var entries = 0
                while (true) {
                    val entry = zis.nextEntry ?: break
                    val outFile = File(destDir, entry.name)
                    if (entry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()
                        FileOutputStream(outFile).use { out ->
                            val buf = ByteArray(8192)
                            while (true) {
                                val r = zis.read(buf)
                                if (r <= 0) break
                                out.write(buf, 0, r)
                            }
                        }
                    }
                    zis.closeEntry()
                    entries++
                    if (entries % 50 == 0) {
                        setProgressAsync(workDataOf("stage" to "unzip", "progress" to entries))
                        setForegroundAsync(createForegroundInfo(inputData.getString(KEY_LANG) ?: "", entries, true, "Распаковка модели"))
                    }
                }
            }
            setProgressAsync(workDataOf("stage" to "unzip", "progress" to -1))
            true
        } catch (e: Exception) {
            Log.e("DownloadModelWorker", "unzip error", e)
            false
        }
    }

    private fun ensureChannel() {
        val id = CHANNEL_ID
        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val ch = NotificationChannel(id, "Загрузки моделей", NotificationManager.IMPORTANCE_LOW)
            nm.createNotificationChannel(ch)
        }
    }

    private fun createForegroundInfo(lang: String, progress: Int, indeterminate: Boolean, title: String): ForegroundInfo {
        val text = if (indeterminate) "${title} (${lang})" else "${title} (${lang}) ${progress}%"
        val n = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(text)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setProgress(if (indeterminate) 0 else 100, if (indeterminate) 0 else progress, indeterminate)
            .build()
        return ForegroundInfo(NOTIFICATION_ID, n)
    }

    private fun notifyComplete(lang: String) {
        val n = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Модель (${lang}) установлена")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID + 1, n)
    }

    companion object {
        const val KEY_LANG = "lang"
        const val KEY_URL = "url"
        const val CHANNEL_ID = "model_downloads"
        const val NOTIFICATION_ID = 2001

        fun enqueue(context: Context, lang: String, url: String) = OneTimeWorkRequestBuilder<DownloadModelWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setInputData(Data.Builder().putString(KEY_LANG, lang).putString(KEY_URL, url).build())
            .addTag(if (lang == com.voicenotes.worker.TranscriptionWorker.LANG_RU) "download_model_ru" else "download_model_en")
            .build()
            .also { WorkManager.getInstance(context).enqueue(it) }
        }
}

