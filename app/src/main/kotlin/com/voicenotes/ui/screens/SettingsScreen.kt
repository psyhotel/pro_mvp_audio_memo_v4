package com.voicenotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.work.WorkManager
import androidx.work.WorkInfo
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import android.Manifest
import android.os.Build
import com.voicenotes.BuildConfig
import com.voicenotes.worker.DownloadModelWorker
import com.voicenotes.worker.TranscriptionWorker
import com.voicenotes.utils.SettingsManager

@Composable
fun SettingsScreen(onBack: () -> Unit, onTasks: () -> Unit) {
    val context = LocalContext.current
    val settings = remember { SettingsManager(context) }
    var url by remember { mutableStateOf(settings.getUrl(BuildConfig.TRANSCRIBE_URL)) }
    var mode by remember { mutableStateOf(settings.getMode()) }
    var lang by remember { mutableStateOf(settings.getLang()) }
    val base = context.getExternalFilesDir(null)?.absolutePath
    val ruInstalled = remember { mutableStateOf(settings.isModelInstalled(base, TranscriptionWorker.LANG_RU)) }
    val enInstalled = remember { mutableStateOf(settings.isModelInstalled(base, TranscriptionWorker.LANG_EN)) }
    val wm = remember { WorkManager.getInstance(context) }
    val ruProgress by wm.getWorkInfosByTagFlow("download_model_ru").collectAsState(initial = emptyList())
    val enProgress by wm.getWorkInfosByTagFlow("download_model_en").collectAsState(initial = emptyList())
    val notificationsAllowed = remember {
        Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Настройки", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("URL транскрибации") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = { mode = SettingsManager.MODE_ONLINE }, label = { Text("ONLINE") }, colors = AssistChipDefaults.assistChipColors(containerColor = if (mode == SettingsManager.MODE_ONLINE) Color(0xFF4A4A6A) else Color(0xFF2F2F4F), labelColor = Color.White))
            AssistChip(onClick = { mode = SettingsManager.MODE_OFFLINE }, label = { Text("OFFLINE") }, colors = AssistChipDefaults.assistChipColors(containerColor = if (mode == SettingsManager.MODE_OFFLINE) Color(0xFF4A4A6A) else Color(0xFF2F2F4F), labelColor = Color.White))
            AssistChip(onClick = { lang = SettingsManager.LANG_RU }, label = { Text("RU") }, colors = AssistChipDefaults.assistChipColors(containerColor = if (lang == SettingsManager.LANG_RU) Color(0xFF4A4A6A) else Color(0xFF2F2F4F), labelColor = Color.White))
            AssistChip(onClick = { lang = SettingsManager.LANG_EN }, label = { Text("EN") }, colors = AssistChipDefaults.assistChipColors(containerColor = if (lang == SettingsManager.LANG_EN) Color(0xFF4A4A6A) else Color(0xFF2F2F4F), labelColor = Color.White))
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("RU модель: ${if (ruInstalled.value) "установлена" else "нет"}")
            val ruInfo = ruProgress.firstOrNull()
            val ruStage = ruInfo?.progress?.getString("stage")
            val ruPct = ruInfo?.progress?.getInt("progress", 0)
            if (ruInfo != null && (ruInfo.state == WorkInfo.State.RUNNING || ruInfo.state == WorkInfo.State.ENQUEUED)) {
                val eta = ruInfo.progress.getInt("eta_ms", -1)
                val etaSec = if (eta > 0) eta / 1000 else -1
                Text("${ruStage ?: ""} ${ruPct ?: 0}%" + if (etaSec >= 0) " · осталось ${etaSec}s" else "")
            }
            Button(onClick = {
                DownloadModelWorker.enqueue(context, TranscriptionWorker.LANG_RU, BuildConfig.VOSK_RU_URL)
            }, enabled = !ruInstalled.value && (ruInfo == null || ruInfo.state != WorkInfo.State.RUNNING)) { Text("Скачать RU") }
        }

        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("EN модель: ${if (enInstalled.value) "установлена" else "нет"}")
            val enInfo = enProgress.firstOrNull()
            val enStage = enInfo?.progress?.getString("stage")
            val enPct = enInfo?.progress?.getInt("progress", 0)
            if (enInfo != null && (enInfo.state == WorkInfo.State.RUNNING || enInfo.state == WorkInfo.State.ENQUEUED)) {
                val eta = enInfo.progress.getInt("eta_ms", -1)
                val etaSec = if (eta > 0) eta / 1000 else -1
                Text("${enStage ?: ""} ${enPct ?: 0}%" + if (etaSec >= 0) " · осталось ${etaSec}s" else "")
            }
            Button(onClick = {
                DownloadModelWorker.enqueue(context, TranscriptionWorker.LANG_EN, BuildConfig.VOSK_EN_URL)
            }, enabled = !enInstalled.value && (enInfo == null || enInfo.state != WorkInfo.State.RUNNING)) { Text("Скачать EN") }
        }

        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                settings.setUrl(url)
                settings.setMode(mode)
                settings.setLang(lang)
                onBack()
            }) { Text("Сохранить") }
            OutlinedButton(onClick = onBack) { Text("Отмена") }
            OutlinedButton(onClick = onTasks) { Text("Задачи") }
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Уведомления: ${if (notificationsAllowed) "разрешены" else "нет"}")
            if (!notificationsAllowed && Build.VERSION.SDK_INT >= 33) {
                Button(onClick = {
                    if (context is android.app.Activity) {
                        context.requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
                    }
                }) { Text("Разрешить") }
            }
        }
    }
}