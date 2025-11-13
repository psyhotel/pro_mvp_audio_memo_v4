package com.voicenotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.voicenotes.ui.viewmodel.NoteViewModel
import com.voicenotes.ui.theme.GradientStart
import com.voicenotes.ui.theme.GradientEnd
import com.voicenotes.ui.theme.MicrophoneRed
import com.voicenotes.data.local.entities.NoteEntity
import com.voicenotes.utils.AudioRecorder
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun AddNoteScreen(viewModel: NoteViewModel, onBack: () -> Unit) {
    var isRecording by remember { mutableStateOf(false) }
    var seconds by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder() }
    var recordedFilePath by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                kotlinx.coroutines.delay(1000)
                seconds++
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(GradientStart, GradientEnd)))
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Запись мысли",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }

        Spacer(Modifier.height(48.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3A3148))
            )
            Icon(
                painter = painterResource(com.voicenotes.R.drawable.ic_mic),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(Modifier.height(24.dp))
        Text(
            text = String.format("%02d:%02d", seconds / 60, seconds % 60),
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.weight(1f))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            IconButton(
                onClick = {
                    if (!isRecording) {
                        val outputPath = "${context.getExternalFilesDir(null)?.absolutePath}/record_${System.currentTimeMillis()}.m4a"
                        scope.launch { audioRecorder.start(outputPath) }
                        recordedFilePath = outputPath
                        seconds = 0
                        isRecording = true
                    } else {
                        audioRecorder.stop()
                        isRecording = false
                        val note = NoteEntity(
                            title = "Мысль",
                            content = "Транскрибация будет позже...",
                            category = "Заметка",
                            timestamp = System.currentTimeMillis(),
                            filePath = recordedFilePath,
                            reminderTime = null
                        )
                        viewModel.insertNote(note)
                        onBack()
                    }
                },
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MicrophoneRed)
            ) {
                Icon(
                    painter = painterResource(com.voicenotes.R.drawable.ic_mic),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            text = "Нажмите на кнопку, чтобы начать запись",
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}