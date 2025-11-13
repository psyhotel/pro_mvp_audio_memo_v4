package com.voicenotes.di;

import com.voicenotes.data.local.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AppModule_ProvideDatabaseFactory implements Factory<AppDatabase> {
  @Override
  public AppDatabase get() {
    return provideDatabase();
  }

  public static AppModule_ProvideDatabaseFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AppDatabase provideDatabase() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDatabase());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideDatabaseFactory INSTANCE = new AppModule_ProvideDatabaseFactory();
  }
}
