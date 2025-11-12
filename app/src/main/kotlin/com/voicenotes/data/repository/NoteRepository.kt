package com.voicenotes.data.repository

import com.voicenotes.data.local.NoteDao
import com.voicenotes.data.local.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {
    fun allNotes(): Flow<List<NoteEntity>> = dao.getAll()
    suspend fun get(id: String): NoteEntity? = dao.findById(id)
    suspend fun save(note: NoteEntity) = dao.insert(note)
    suspend fun delete(note: NoteEntity) = dao.delete(note)
}
