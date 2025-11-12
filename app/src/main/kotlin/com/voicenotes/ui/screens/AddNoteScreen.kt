package com.voicenotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.voicenotes.data.local.entities.NoteEntity
import com.voicenotes.ui.viewmodel.NoteViewModel
import java.util.*
import com.voicenotes.ui.screens.waveform.WaveformComposable
import java.io.File

@Composable
fun AddNoteScreen(nav: NavController, vm: NoteViewModel) {
    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var audioPath by remember { mutableStateOf<String?>(null) }
    var isRecording by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Text") }, modifier = Modifier.fillMaxWidth(), maxLines = 4)
        Spacer(Modifier.height(8.dp))
        WaveformComposable(modifier = Modifier.fillMaxWidth().height(100.dp))
        Spacer(Modifier.height(8.dp))
        Row {
            Button(onClick = {
                if (!isRecording) {
                    val out = File.createTempFile("audio_", ".m4a").absolutePath
                    audioPath = out
                    // Recording to be wired
                    isRecording = true
                } else {
                    isRecording = false
                }
            }) { Text(if (!isRecording) "Record" else "Stop") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                val id = UUID.randomUUID().toString()
                val note = NoteEntity(id = id, title = title, text = text, audioPath = audioPath)
                vm.save(note)
                nav.navigateUp()
            }) { Text("Save") }
        }
    }
}
