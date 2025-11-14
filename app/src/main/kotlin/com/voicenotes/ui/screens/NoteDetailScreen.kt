package com.voicenotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.voicenotes.ui.viewmodel.NoteViewModel
import androidx.compose.ui.graphics.Brush
import com.voicenotes.ui.theme.GradientStart
import com.voicenotes.ui.theme.GradientEnd
import com.voicenotes.data.local.entities.NoteEntity
import kotlinx.coroutines.launch

@Composable
fun NoteDetailScreen(noteId: Long, viewModel: NoteViewModel, onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var loadedNote by remember { mutableStateOf<NoteEntity?>(null) }

    LaunchedEffect(noteId) {
        val note = viewModel.getNoteById(noteId)
        if (note != null) {
            loadedNote = note
            title = note.title
            content = note.content
            category = note.category
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(com.voicenotes.ui.theme.GradientStart, com.voicenotes.ui.theme.GradientMid, com.voicenotes.ui.theme.GradientEnd)))
            .padding(16.dp)
    ) {
        Text(text = "Детали", style = MaterialTheme.typography.headlineMedium, color = com.voicenotes.ui.theme.TextPrimary)

        Spacer(Modifier.height(12.dp))

        if (loadedNote == null) {
            Text("Загрузка...", color = MaterialTheme.colorScheme.onPrimary)
        } else {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Заголовок") }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(12.dp))

            Section(title = "Транскрипт") {
                Text(text = content, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Категория") }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(12.dp))
            Section(title = "Резюме") {
                Text(text = if (content.isBlank()) "" else content.take(120), style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(12.dp))
            Section(title = "Ключевые моменты") {
                val bullets = content.split(". ").take(2)
                bullets.forEach { Text("• ${it}", style = MaterialTheme.typography.bodyMedium) }
            }

            Spacer(Modifier.height(12.dp))
            Section(title = "Action Items") {
                Text(text = "Добавить напоминание", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(24.dp))

            val scope = rememberCoroutineScope()
            Button(onClick = {
                val original = loadedNote ?: return@Button
                scope.launch {
                    viewModel.updateNote(
                        original.copy(
                            title = title,
                            content = content,
                            category = category
                        )
                    )
                    onBack()
                }
            }) {
                Text("Сохранить")
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(text = title, style = MaterialTheme.typography.titleMedium, color = com.voicenotes.ui.theme.TextPrimary)
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        colors = CardDefaults.cardColors(containerColor = com.voicenotes.ui.theme.CardTransparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, com.voicenotes.ui.theme.BorderTransparent)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            content()
        }
    }
}