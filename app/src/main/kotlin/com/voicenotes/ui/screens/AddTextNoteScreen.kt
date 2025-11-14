package com.voicenotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.voicenotes.ui.theme.GradientStart
import com.voicenotes.ui.theme.GradientEnd
import com.voicenotes.ui.viewmodel.NoteViewModel
import com.voicenotes.data.local.entities.NoteEntity

@Composable
fun AddTextNoteScreen(viewModel: NoteViewModel, onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Заметка") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(GradientStart, GradientEnd)))
            .padding(16.dp)
    ) {
        Text("Новая заметка", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Заголовок") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Содержание") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Категория") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val note = NoteEntity(
                    title = if (title.isBlank()) "Заметка" else title,
                    content = content,
                    category = category,
                    timestamp = System.currentTimeMillis(),
                    filePath = null,
                    reminderTime = null
                )
                viewModel.insertNote(note)
                onBack()
            }) { Text("Сохранить") }
            OutlinedButton(onClick = onBack) { Text("Отмена") }
        }
    }
}