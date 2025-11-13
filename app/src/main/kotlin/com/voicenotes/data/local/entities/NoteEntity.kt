package com.voicenotes.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.compose.runtime.Immutable

@Entity(tableName = "notes")
@Immutable
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String,
    val timestamp: Long,
    val filePath: String?,
    val reminderTime: Long?,
    val reportType: String? = null
)