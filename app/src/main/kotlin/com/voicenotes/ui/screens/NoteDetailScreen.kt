package com.voicenotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.voicenotes.ui.viewmodel.NoteViewModel

@Composable
fun NoteDetailScreen(noteId: Long, viewModel: NoteViewModel, onBack: () -> Unit) {
    // Пример отображения деталей заметки
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Детали заметки $noteId",
            style = MaterialTheme.typography.headlineMedium
        )
        // Здесь можно добавить логику для получения и отображения заметки по ID
    }
}