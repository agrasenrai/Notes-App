package com.example.notesview;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.notes.model.Note;
import com.example.notes.viewmodel.NoteViewModel;

public class MainActivity extends AppCompatActivity {
    private NoteViewModel noteViewModel;
    private NoteAdapter adapter;
    private BottomSheetDialog noteDialog;
    private TextInputEditText titleEditText, descriptionEditText;
    private TextInputLayout titleInputLayout, descriptionInputLayout;
    private Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupRecyclerView();
        setupNoteDialog();
        setupViewModel();

        ExtendedFloatingActionButton addButton = findViewById(R.id.button_add);
        addButton.setOnClickListener(v -> showNoteDialog(null));
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Add animation
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.list_item_spacing);
            }
        });

        adapter = new NoteAdapter(note -> showNoteDialog(note));
        recyclerView.setAdapter(adapter);

        // Add swipe to delete
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Note note = adapter.getNoteAt(viewHolder.getAdapterPosition());
                noteViewModel.delete(note);

                Snackbar.make(recyclerView, "Note deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", v -> noteViewModel.insert(note))
                        .setActionTextColor(getResources().getColor(R.color.colorAccent))
                        .show();
            }
        };
        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);
    }

    private void setupViewModel() {
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, notes -> {
            adapter.setNotes(notes);
        });
    }

    private void setupNoteDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_note, null);
        titleEditText = dialogView.findViewById(R.id.edit_text_title);
        descriptionEditText = dialogView.findViewById(R.id.edit_text_description);
        titleInputLayout = dialogView.findViewById(R.id.title_input_layout);
        descriptionInputLayout = dialogView.findViewById(R.id.description_input_layout);

        noteDialog = new BottomSheetDialog(this);
        noteDialog.setContentView(dialogView);
        noteDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        // Add save button to dialog
        dialogView.findViewById(R.id.button_save).setOnClickListener(v -> saveNote());
        dialogView.findViewById(R.id.button_cancel).setOnClickListener(v -> noteDialog.dismiss());
    }

    private void showNoteDialog(Note note) {
        currentNote = note;
        if (note != null) {
            titleEditText.setText(note.getTitle());
            descriptionEditText.setText(note.getDescription());
            noteDialog.setTitle("Edit Note");
        } else {
            titleEditText.setText("");
            descriptionEditText.setText("");
            noteDialog.setTitle("Add Note");
        }

        // Clear any previous errors
        titleInputLayout.setError(null);
        descriptionInputLayout.setError(null);

        noteDialog.show();
    }

    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            titleInputLayout.setError("Title cannot be empty");
            return;
        }

        if (description.isEmpty()) {
            descriptionInputLayout.setError("Description cannot be empty");
            return;
        }

        if (currentNote == null) {
            // Create new note
            Note newNote = new Note(title, description);
            noteViewModel.insert(newNote);
            Snackbar.make(findViewById(R.id.recycler_view), "Note added", Snackbar.LENGTH_SHORT).show();
        } else {
            // Update existing note
            currentNote.setTitle(title);
            currentNote.setDescription(description);
            noteViewModel.update(currentNote);
            Snackbar.make(findViewById(R.id.recycler_view), "Note updated", Snackbar.LENGTH_SHORT).show();
        }

        noteDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (noteDialog != null && noteDialog.isShowing()) {
            noteDialog.dismiss();
        }
    }
}