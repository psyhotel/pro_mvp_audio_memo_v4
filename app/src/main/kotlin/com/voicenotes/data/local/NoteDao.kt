package com.voicenotes.data.local

import androidx.room.*
import com.voicenotes.data.local.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    suspend fun getAllNotes(): List<NoteEntity>

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotesFlow(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT DISTINCT category FROM notes")
    suspend fun getAllCategories(): List<String>

    @Query("SELECT DISTINCT category FROM notes")
    fun getAllCategoriesFlow(): Flow<List<String>>

    @Query("SELECT * FROM notes WHERE category = :category")
    suspend fun getNotesByCategory(category: String): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE category = :category")
    fun getNotesByCategoryFlow(category: String): Flow<List<NoteEntity>>

    @Query("UPDATE notes SET category = :newCategory WHERE category = :oldCategory")
    suspend fun renameCategory(oldCategory: String, newCategory: String)

    @Query("UPDATE notes SET content = :content WHERE id = :id")
    suspend fun updateNoteContent(id: Long, content: String)
}
