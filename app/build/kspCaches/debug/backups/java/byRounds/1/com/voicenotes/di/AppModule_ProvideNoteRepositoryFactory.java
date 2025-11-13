package com.voicenotes.di;

import com.voicenotes.data.local.AppDatabase;
import com.voicenotes.data.repository.NoteRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AppModule_ProvideNoteRepositoryFactory implements Factory<NoteRepository> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideNoteRepositoryFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public NoteRepository get() {
    return provideNoteRepository(dbProvider.get());
  }

  public static AppModule_ProvideNoteRepositoryFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideNoteRepositoryFactory(dbProvider);
  }

  public static NoteRepository provideNoteRepository(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideNoteRepository(db));
  }
}
