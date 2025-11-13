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
import com.voicenotes.data.local.entities.NoteEntity
import com.voicenotes.ui.theme.MicrophoneRed
import com.voicenotes.ui.theme.GradientStart
import com.voicenotes.ui.theme.GradientEnd
import com.voicenotes.ui.viewmodel.NoteViewModel

@Composable
fun MainScreen(
    notes: List<NoteEntity>,
    categories: List<String>,
    onAddClick: () -> Unit,
    onNoteClick: (Long) -> Unit,
    onDelete: (NoteEntity) -> Unit,
    onRenameCategory: (String, String) -> Unit
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
                Brush.verticalGradient(listOf(GradientStart, GradientEnd))
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Мысли",
            style = MaterialTheme.typography.headlineLarge.copy(color = Color.White),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "${filtered.size} записей",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.7f))
        )

        // Фильтры
        Row(modifier = Modifier.padding(top = 12.dp)) {
            categoriesAll.forEach { cat ->
                AssistChip(
                    onClick = { selected = cat },
                    label = { Text(cat) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected == cat) Color(0xFF4A4A6A) else Color(0xFF2F2F4F),
                        labelColor = Color.White
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
                                containerColor = Color(0xFF2A2A4A)
                            )
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
                                        "Бизнес-идея" -> Color.Yellow
                                        "Задача" -> Color.Green
                                        "Заметка" -> Color.Blue
                                        else -> Color.Gray
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
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                )
                                Text(
                                    text = "3 дня назад",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                )
                            }

                            Text(
                                text = note.content.take(100) + if (note.content.length > 100) "..." else "",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
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
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "0:18",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White.copy(alpha = 0.7f)
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

        FloatingActionButton(
            onClick = onAddClick,
            containerColor = MicrophoneRed,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(com.voicenotes.R.drawable.ic_add),
                contentDescription = "Добавить",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}