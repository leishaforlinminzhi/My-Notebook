package com.example.mynotebook.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotebook.R;
import com.example.mynotebook.model.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;
    private OnNoteClickListener onNoteClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public NoteAdapter(List<Note> noteList, OnNoteClickListener onNoteClickListener, OnDeleteClickListener onDeleteClickListener) {
        this.noteList = noteList;
        this.onNoteClickListener = onNoteClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.textViewTitle.setText(note.getTitle());
        holder.textViewTags.setText(note.getTags());

        holder.itemView.setOnClickListener(v -> onNoteClickListener.onNoteClicked(note));
        holder.buttonDelete.setOnClickListener(v -> onDeleteClickListener.onDeleteClicked(note));
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewTags;
        Button buttonDelete;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewTags = itemView.findViewById(R.id.textViewTags);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }

    public interface OnNoteClickListener {
        void onNoteClicked(Note note);
    }

    public interface OnDeleteClickListener {
        void onDeleteClicked(Note note);
    }
}
