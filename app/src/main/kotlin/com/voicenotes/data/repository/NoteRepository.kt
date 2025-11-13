package com.voicenotes.data.repository

import com.voicenotes.data.local.NoteDao
import com.voicenotes.data.local.entities.NoteEntity

class NoteRepository(private val noteDao: NoteDao) {
    suspend fun getAllNotes() = noteDao.getAllNotes()
    suspend fun getNoteById(id: Long) = noteDao.getNoteById(id)
    suspend fun insertNote(note: NoteEntity) = noteDao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
    suspend fun getAllCategories() = noteDao.getAllCategories()
    suspend fun getNotesByCategory(category: String) = noteDao.getNotesByCategory(category)
}