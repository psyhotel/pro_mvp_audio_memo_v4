package com.voicenotes.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.voicenotes.MainActivity

class ReminderWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: "Reminder"
        val noteId = inputData.getString("noteId")
        showNotification(title, noteId)
        return Result.success()
    }

    private fun showNotification(title: String, noteId: String?) {
        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reminders"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, "Reminders", NotificationManager.IMPORTANCE_HIGH)
            nm.createNotificationChannel(ch)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("noteId", noteId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        val pending = PendingIntent.getActivity(applicationContext, (noteId?:"0").hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0)

        val n = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("AudioMemo Reminder")
            .setContentText(title)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()
        nm.notify((System.currentTimeMillis()%100000).toInt(), n)
    }
}
