package com.example.notes.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.notes.model.Note;
import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM notes")
    LiveData<List<Note>> getAllNotes();
}