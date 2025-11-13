package com.voicenotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.voicenotes.ui.viewmodel.NoteViewModel
import com.voicenotes.data.local.entities.NoteEntity
import com.voicenotes.utils.AudioRecorder
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope

@Composable
fun AddNoteScreen(viewModel: NoteViewModel, onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Обычное") }
    var isRecording by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder() }
    var recordedFilePath by remember { mutableStateOf<String?>(null) }

    // Получаем lifecycleOwner и scope
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = lifecycleOwner.lifecycleScope

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Заголовок") }
        )

        TextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Категория") }
        )

        Button(
            onClick = {
                if (!isRecording) {
                    val outputPath = "${context.getExternalFilesDir(null)?.absolutePath}/record_${System.currentTimeMillis()}.m4a"
                    // Запускаем suspend-функцию в scope
                    scope.launch {
                        audioRecorder.start(outputPath) // ✅ Теперь можно вызвать
                    }
                    recordedFilePath = outputPath
                    isRecording = true
                } else {
                    audioRecorder.stop() // stop — обычная функция, вызывается напрямую
                    isRecording = false
                }
            }
        ) {
            Text(if (isRecording) "Остановить запись" else "Начать запись")
        }

        Button(
            onClick = {
                val filePath = recordedFilePath
                val note = NoteEntity(
                    title = title,
                    content = "Транскрибация будет позже...",
                    category = category,
                    timestamp = System.currentTimeMillis(),
                    filePath = filePath,
                    reminderTime = null
                )
                viewModel.insertNote(note)
                onBack()
            }
        ) {
            Text("Сохранить")
        }
    }
}