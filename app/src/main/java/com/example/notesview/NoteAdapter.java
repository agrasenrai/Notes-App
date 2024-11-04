package com.example.notesview;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.model.Note;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    private List<Note> notes = new ArrayList<>();
    private OnItemClickListener listener;
    private int[] cardColors;

    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public NoteAdapter(OnItemClickListener listener) {
        this.listener = listener;
        // Initialize card colors array
        cardColors = new int[]{
                R.color.card_background_1,
                R.color.card_background_2,
                R.color.card_background_3,
                R.color.card_background_4,
                R.color.card_background_5
        };
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = notes.get(position);
        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewDescription.setText(currentNote.getDescription());

        // Set card background color
        MaterialCardView cardView = (MaterialCardView) holder.itemView;
        int colorResId = cardColors[position % cardColors.length];
        cardView.setCardBackgroundColor(holder.itemView.getContext().getColor(colorResId));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public Note getNoteAt(int position) {
        return notes.get(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;

        public NoteHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(notes.get(position));
                }
            });
        }
    }
}
