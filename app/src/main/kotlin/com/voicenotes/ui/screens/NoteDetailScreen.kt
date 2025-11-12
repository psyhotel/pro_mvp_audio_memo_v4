package com.voicenotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.voicenotes.data.local.entities.NoteEntity
import com.voicenotes.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

@Composable
fun NoteDetailScreen(nav: NavController, noteId: String, vm: NoteViewModel) {
    var note by remember { mutableStateOf<NoteEntity?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(noteId) {
        note = vm.get(noteId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text(note?.title ?: "(no title)")
        Spacer(Modifier.height(8.dp))
        Text(note?.text ?: "")
        Spacer(Modifier.height(12.dp))
        Row {
            Button(onClick = { nav.navigateUp() }) { Text("Back") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                note?.let {
                    scope.launch { vm.scheduleReminder(it, 60_000L) }
                }
            }) { Text("Set Reminder (1 min)") }
        }
    }
}
