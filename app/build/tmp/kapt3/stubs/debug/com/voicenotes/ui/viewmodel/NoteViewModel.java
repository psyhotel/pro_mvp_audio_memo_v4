package com.voicenotes.ui.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\nJ\u0018\u0010\u0010\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0011\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0013J\u000e\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\nJ\u0016\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0017\u001a\u00020\u0018R\u001d\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/voicenotes/ui/viewmodel/NoteViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "repo", "Lcom/voicenotes/data/repository/NoteRepository;", "application", "Landroid/app/Application;", "(Lcom/voicenotes/data/repository/NoteRepository;Landroid/app/Application;)V", "notes", "Lkotlinx/coroutines/flow/StateFlow;", "", "Lcom/voicenotes/data/local/entities/NoteEntity;", "getNotes", "()Lkotlinx/coroutines/flow/StateFlow;", "delete", "Lkotlinx/coroutines/Job;", "note", "get", "id", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "save", "scheduleReminder", "", "delayMillis", "", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class NoteViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.voicenotes.data.repository.NoteRepository repo = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.voicenotes.data.local.entities.NoteEntity>> notes = null;
    
    @javax.inject.Inject()
    public NoteViewModel(@org.jetbrains.annotations.NotNull()
    com.voicenotes.data.repository.NoteRepository repo, @org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.voicenotes.data.local.entities.NoteEntity>> getNotes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job save(@org.jetbrains.annotations.NotNull()
    com.voicenotes.data.local.entities.NoteEntity note) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job delete(@org.jetbrains.annotations.NotNull()
    com.voicenotes.data.local.entities.NoteEntity note) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object get(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.voicenotes.data.local.entities.NoteEntity> $completion) {
        return null;
    }
    
    public final void scheduleReminder(@org.jetbrains.annotations.NotNull()
    com.voicenotes.data.local.entities.NoteEntity note, long delayMillis) {
    }
}