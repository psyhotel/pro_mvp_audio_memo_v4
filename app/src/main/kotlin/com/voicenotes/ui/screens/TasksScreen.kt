package com.voicenotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.compose.runtime.collectAsState

@Composable
fun TasksScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val wm = remember { WorkManager.getInstance(context) }
    val ru by wm.getWorkInfosByTagFlow("download_model_ru").collectAsState(initial = emptyList())
    val en by wm.getWorkInfosByTagFlow("download_model_en").collectAsState(initial = emptyList())
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Задачи", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onBack) { Text("Назад") }
        }
        Spacer(Modifier.height(12.dp))
        TaskList(title = "Скачивание RU", infos = ru)
        Spacer(Modifier.height(12.dp))
        TaskList(title = "Скачивание EN", infos = en)
    }
}

@Composable
private fun TaskList(title: String, infos: List<WorkInfo>) {
    Text(title, style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
    Spacer(Modifier.height(8.dp))
    infos.forEach { info ->
        val stage = info.progress.getString("stage") ?: ""
        val pct = info.progress.getInt("progress", 0)
        val state = info.state.name
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Column(Modifier.padding(12.dp)) {
                Text("${state}")
                if (stage.isNotEmpty()) Text("${stage} ${pct}%")
            }
        }
    }
}