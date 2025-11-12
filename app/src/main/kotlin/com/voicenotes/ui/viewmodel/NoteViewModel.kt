package com.voicenotes.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.voicenotes.data.local.entities.NoteEntity
import com.voicenotes.data.repository.NoteRepository
import com.voicenotes.worker.ReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.concurrent.TimeUnit

@HiltViewModel
class NoteViewModel @Inject constructor(private val repo: NoteRepository, application: Application): AndroidViewModel(application) {

    val notes = repo.allNotes().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun save(note: NoteEntity) = viewModelScope.launch { repo.save(note) }
    fun delete(note: NoteEntity) = viewModelScope.launch { repo.delete(note) }
    suspend fun get(id: String) = repo.get(id)

    fun scheduleReminder(note: NoteEntity, delayMillis: Long) {
        val data = workDataOf("title" to (note.title ?: "Audio Note"), "noteId" to note.id)
        val req = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()
        WorkManager.getInstance(getApplication()).enqueue(req)
    }
}
