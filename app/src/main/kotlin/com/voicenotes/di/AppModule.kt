package com.voicenotes.di

import com.voicenotes.data.local.AppDatabase
import com.voicenotes.data.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(): AppDatabase = AppDatabase.getDatabase()

    @Provides
    @Singleton
    fun provideNoteRepository(db: AppDatabase): NoteRepository = NoteRepository(db.noteDao())
}
