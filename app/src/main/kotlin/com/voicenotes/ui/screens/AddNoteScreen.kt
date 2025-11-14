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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionStatus
import android.Manifest
import com.voicenotes.ui.viewmodel.NoteViewModel
import com.voicenotes.ui.theme.GradientStart
import com.voicenotes.ui.theme.GradientEnd
import com.voicenotes.ui.theme.MicrophoneRed
import com.voicenotes.data.local.entities.NoteEntity
import com.voicenotes.utils.WavAudioRecorder
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.rememberCoroutineScope
import com.voicenotes.BuildConfig
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddNoteScreen(viewModel: NoteViewModel, onBack: () -> Unit) {
    var isRecording by remember { mutableStateOf(false) }
    var seconds by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val audioRecorder = remember { WavAudioRecorder() }
    var recordedFilePath by remember { mutableStateOf<String?>(null) }
    val settings = remember { com.voicenotes.utils.SettingsManager(context) }
    var offline by remember { mutableStateOf(settings.getMode() == com.voicenotes.utils.SettingsManager.MODE_OFFLINE) }
    var lang by remember { mutableStateOf(settings.getLang()) }
    val recordPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val wm = remember { WorkManager.getInstance(context) }
    var processingName by remember { mutableStateOf<String?>(null) }
    val infosFlow = remember(processingName) { processingName?.let { wm.getWorkInfosForUniqueWorkFlow(it) } }
    val workInfos by (infosFlow?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) })

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
            .background(Brush.verticalGradient(listOf(com.voicenotes.ui.theme.GradientStart, com.voicenotes.ui.theme.GradientMid, com.voicenotes.ui.theme.GradientEnd)))
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Запись мысли",
                style = MaterialTheme.typography.titleLarge.copy(color = com.voicenotes.ui.theme.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            )
        }

                Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = { offline = false },
                        label = { Text("ONLINE") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (!offline) com.voicenotes.ui.theme.ChipSelectedBg else com.voicenotes.ui.theme.ChipDefaultBg,
                            labelColor = if (!offline) com.voicenotes.ui.theme.MicrophoneRed else com.voicenotes.ui.theme.TextSecondary
                        )
                    )
                    AssistChip(
                        onClick = { offline = true },
                        label = { Text("OFFLINE") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (offline) com.voicenotes.ui.theme.ChipSelectedBg else com.voicenotes.ui.theme.ChipDefaultBg,
                            labelColor = if (offline) com.voicenotes.ui.theme.MicrophoneRed else com.voicenotes.ui.theme.TextSecondary
                        )
                    )
                    AssistChip(
                        onClick = { lang = "ru" },
                        label = { Text("RU") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (lang == "ru") com.voicenotes.ui.theme.ChipSelectedBg else com.voicenotes.ui.theme.ChipDefaultBg,
                            labelColor = if (lang == "ru") com.voicenotes.ui.theme.MicrophoneRed else com.voicenotes.ui.theme.TextSecondary
                        )
                    )
                    AssistChip(
                        onClick = { lang = "en" },
                        label = { Text("EN") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (lang == "en") com.voicenotes.ui.theme.ChipSelectedBg else com.voicenotes.ui.theme.ChipDefaultBg,
                            labelColor = if (lang == "en") com.voicenotes.ui.theme.MicrophoneRed else com.voicenotes.ui.theme.TextSecondary
                        )
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
                        when (recordPermission.status) {
                            is PermissionStatus.Granted -> {
                                val outputPath = "${context.getExternalFilesDir(null)?.absolutePath}/record_${System.currentTimeMillis()}.wav"
                                scope.launch { audioRecorder.start(outputPath) }
                                recordedFilePath = outputPath
                                seconds = 0
                                isRecording = true
                            }
                            else -> {
                                recordPermission.launchPermissionRequest()
                            }
                        }
                    } else {
                        audioRecorder.stop()
                        isRecording = false
                        val note = NoteEntity(
                            title = "Мысль",
                            content = "Обработка аудио...",
                            category = "Заметка",
                            timestamp = System.currentTimeMillis(),
                            filePath = recordedFilePath,
                            reminderTime = null
                        )
                        val fp = recordedFilePath
                        viewModel.insertNote(note) { id ->
                            if (fp != null) {
                                com.voicenotes.worker.TranscriptionScheduler.enqueue(
                                    context,
                                    id,
                                    fp,
                                    offline,
                                    lang
                                )
                                processingName = "transcribe_${id}"
                            }
                        }
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
        if (recordPermission.status is PermissionStatus.Denied) {
            Text(
                text = "Нужно разрешение на микрофон",
                color = Color.Red.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }

        if (processingName != null) {
            Spacer(Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MicrophoneRed)
            }
            Text(
                text = "Обработка записи...",
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }

        LaunchedEffect(workInfos) {
            if (processingName != null) {
                val finished = workInfos.any { it.state == WorkInfo.State.SUCCEEDED || it.state == WorkInfo.State.FAILED || it.state == WorkInfo.State.CANCELLED }
                if (finished) {
                    processingName = null
                    onBack()
                }
            }
        }
    }
}