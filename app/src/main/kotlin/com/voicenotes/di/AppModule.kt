package com.voicenotes.di

import android.content.Context
import com.voicenotes.data.local.AppDatabase
import com.voicenotes.data.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext app: Context) = AppDatabase.get(app)

    @Provides
    @Singleton
    fun provideNoteRepository(db: AppDatabase) = NoteRepository(db.noteDao())
}
