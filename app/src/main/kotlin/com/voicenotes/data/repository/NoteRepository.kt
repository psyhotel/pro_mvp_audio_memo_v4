package com.voicenotes.data.repository

import com.voicenotes.data.local.NoteDao
import com.voicenotes.data.local.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    suspend fun getAllNotes() = noteDao.getAllNotes()
    fun getAllNotesFlow(): Flow<List<NoteEntity>> = noteDao.getAllNotesFlow()
    suspend fun getNoteById(id: Long) = noteDao.getNoteById(id)
    suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
    suspend fun getAllCategories() = noteDao.getAllCategories()
    fun getAllCategoriesFlow(): Flow<List<String>> = noteDao.getAllCategoriesFlow()
    suspend fun getNotesByCategory(category: String) = noteDao.getNotesByCategory(category)
    fun getNotesByCategoryFlow(category: String): Flow<List<NoteEntity>> = noteDao.getNotesByCategoryFlow(category)
    suspend fun renameCategory(oldCategory: String, newCategory: String) = noteDao.renameCategory(oldCategory, newCategory)
    suspend fun updateNoteContent(id: Long, content: String) = noteDao.updateNoteContent(id, content)
}
