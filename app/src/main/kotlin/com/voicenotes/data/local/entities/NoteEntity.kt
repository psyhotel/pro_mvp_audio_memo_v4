package com.voicenotes.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    var title: String? = null,
    var text: String? = null,
    var audioPath: String? = null,
    var created: Long = System.currentTimeMillis(),
    var updated: Long = System.currentTimeMillis(),
    var reminderMillis: Long? = null,
    var category: String? = null
)
