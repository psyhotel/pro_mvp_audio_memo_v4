package com.voicenotes.data.local

import androidx.room.*
import com.voicenotes.data.local.entities.NoteEntity

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    suspend fun getAllNotes(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT DISTINCT category FROM notes")
    suspend fun getAllCategories(): List<String>

    @Query("SELECT * FROM notes WHERE category = :category")
    suspend fun getNotesByCategory(category: String): List<NoteEntity>
}