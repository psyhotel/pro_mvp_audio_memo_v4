package com.voicenotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.voicenotes.data.local.entities.NoteEntity
import com.voicenotes.ui.theme.MicrophoneRed
import com.voicenotes.ui.theme.GradientStart
import com.voicenotes.ui.theme.GradientEnd
import com.voicenotes.ui.viewmodel.NoteViewModel

@Composable
fun MainScreen(
    notes: List<NoteEntity>,
    categories: List<String>,
    onAddAudioClick: () -> Unit,
    onAddTextClick: () -> Unit,
    onNoteClick: (Long) -> Unit,
    onDelete: (NoteEntity) -> Unit,
    onRenameCategory: (String, String) -> Unit,
    onSettingsClick: () -> Unit
) {
    var selected by remember { mutableStateOf("Все") }
    val categoriesAll = remember(categories) { listOf("Все") + categories }
    val filtered = remember(notes, selected) {
        if (selected == "Все") notes else notes.filter { it.category == selected }
    }
    var editingCategory by remember { mutableStateOf<String?>(null) }
    var newCategoryName by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(com.voicenotes.ui.theme.GradientStart, com.voicenotes.ui.theme.GradientMid, com.voicenotes.ui.theme.GradientEnd))
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Мысли",
                style = MaterialTheme.typography.headlineLarge.copy(color = com.voicenotes.ui.theme.TextPrimary, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold)
            )
            TextButton(onClick = onSettingsClick) { Text("Настройки") }
        }

        Text(
            text = "${filtered.size} записей",
            style = MaterialTheme.typography.bodyMedium.copy(color = com.voicenotes.ui.theme.TextSecondary)
        )

        Row(modifier = Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            categoriesAll.forEach { cat ->
                AssistChip(
                    onClick = { selected = cat },
                    label = { Text(cat) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected == cat) com.voicenotes.ui.theme.ChipSelectedBg else com.voicenotes.ui.theme.ChipDefaultBg,
                        labelColor = if (selected == cat) com.voicenotes.ui.theme.MicrophoneRed else com.voicenotes.ui.theme.TextSecondary
                    ),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .pointerInput(cat) {
                            detectTapGestures(onLongPress = {
                                if (cat != "Все") {
                                    editingCategory = cat
                                    newCategoryName = cat
                                }
                            })
                        }
                )
            }
            if (selected != "Все") {
                TextButton(onClick = { editingCategory = selected; newCategoryName = selected }) {
                    Text("Переименовать")
                }
            }
        }

        if (editingCategory != null) {
            AlertDialog(
                onDismissRequest = { editingCategory = null },
                title = { Text("Переименовать категорию") },
                text = {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Новое имя") }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val old = editingCategory ?: return@TextButton
                        if (newCategoryName.isNotBlank() && newCategoryName != old) {
                            onRenameCategory(old, newCategoryName)
                        }
                        editingCategory = null
                    }) { Text("Сохранить") }
                },
                dismissButton = {
                    TextButton(onClick = { editingCategory = null }) { Text("Отмена") }
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp)
        ) {
            items(filtered, key = { it.id }, contentType = { it.category }) { note ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        when (value) {
                            SwipeToDismissBoxValue.StartToEnd -> {
                                onNoteClick(note.id)
                                false
                            }
                            SwipeToDismissBoxValue.EndToStart -> {
                                onDelete(note)
                                true
                            }
                            else -> false
                        }
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = true,
                    enableDismissFromEndToStart = true,
                    backgroundContent = {
                        val bgColor = when (dismissState.currentValue) {
                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                            SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(bgColor)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            val text = when (dismissState.currentValue) {
                                SwipeToDismissBoxValue.EndToStart -> "Удалить"
                                SwipeToDismissBoxValue.StartToEnd -> "Редактировать"
                                else -> "Смахните для действий"
                            }
                            Text(text = text, style = MaterialTheme.typography.bodyMedium)
                        }
                    },
                    content = {
                        Card(
                            onClick = { onNoteClick(note.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .height(120.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = com.voicenotes.ui.theme.CardTransparent
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.voicenotes.ui.theme.BorderTransparent)
                        ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(
                                    when (note.category) {
                                        "Бизнес-идея" -> com.voicenotes.ui.theme.CategoryBusiness
                                        "Задача" -> com.voicenotes.ui.theme.CategoryTask
                                        "Заметка" -> com.voicenotes.ui.theme.CategoryNote
                                        "Размышление" -> com.voicenotes.ui.theme.CategoryThought
                                        "Цель" -> com.voicenotes.ui.theme.CategoryGoal
                                        else -> com.voicenotes.ui.theme.CategoryOther
                                    }
                                )
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = note.category,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = com.voicenotes.ui.theme.TextSecondary
                                    )
                                )
                                Text(
                                    text = "3 дня назад",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = com.voicenotes.ui.theme.TextSecondary
                                    )
                                )
                            }

                            Text(
                                text = note.content.take(100) + if (note.content.length > 100) "..." else "",
                                style = MaterialTheme.typography.bodyMedium.copy(color = com.voicenotes.ui.theme.TextPrimary),
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row {
                                    Icon(
                                        painter = painterResource(com.voicenotes.R.drawable.ic_mic),
                                        contentDescription = "Длительность",
                                        tint = com.voicenotes.ui.theme.TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "0:18",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = com.voicenotes.ui.theme.TextSecondary
                                        )
                                    )
                                }

                                if (note.category == "Задача") {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("Важно") },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = Color(0xFF4A4A6A),
                                            labelColor = Color.White
                                        ),
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                        }
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExtendedFloatingActionButton(
                onClick = onAddAudioClick,
                containerColor = MicrophoneRed
            ) { Text("Аудио") }
            ExtendedFloatingActionButton(
                onClick = onAddTextClick,
                containerColor = Color(0xFF4A4A6A)
            ) { Text("Текст") }
        }
    }
}