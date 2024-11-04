package com.example.notes.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;
import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;

    public NoteViewModel(Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void update(Note note) {
        repository.update(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }
}
