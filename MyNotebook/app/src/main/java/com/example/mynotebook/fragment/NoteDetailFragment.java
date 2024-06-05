package com.example.mynotebook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mynotebook.R;
import com.example.mynotebook.model.Note;

public class NoteDetailFragment extends Fragment {

    private static final String ARG_ID = "arg_id";
    private static final String ARG_NOTE_ID = "arg_note_id";
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_TEXT = "arg_text";
    private static final String ARG_TAGS = "arg_tags";
    private static final String ARG_IMAGES = "arg_images";
    private static final String ARG_VOICE = "arg_voice";
    private static final String ARG_NOTE = "note";
    private Note note;

    public static NoteDetailFragment newInstance(Note note) {
        NoteDetailFragment fragment = new NoteDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, note.getId());
        args.putInt(ARG_NOTE_ID, note.getNoteID());
        args.putString(ARG_TITLE, note.getTitle());
        args.putString(ARG_TEXT, note.getText());
        args.putString(ARG_TAGS, note.getTags());
        args.putString(ARG_IMAGES, note.getImages());
        args.putString(ARG_VOICE, note.getVoice());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int id = getArguments().getInt(ARG_ID);
            int noteID = getArguments().getInt(ARG_NOTE_ID);
            String title = getArguments().getString(ARG_TITLE);
            String text = getArguments().getString(ARG_TEXT);
            String tags = getArguments().getString(ARG_TAGS);
            String images = getArguments().getString(ARG_IMAGES);
            String voice = getArguments().getString(ARG_VOICE);
            note = new Note();
            note.setId(id);
            note.setNoteID(noteID);
            note.setTitle(title);
            note.setText(text);
            note.setTags(tags);
            note.setImages(images);
            note.setVoice(voice);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_detail, container, false);
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewText = view.findViewById(R.id.textViewText);
        TextView textViewTags = view.findViewById(R.id.textViewTags);
        Button buttonBack = view.findViewById(R.id.buttonBack);

        textViewTitle.setText(note.getTitle());
        textViewText.setText(note.getText());
        textViewTags.setText(note.getTags());

        buttonBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Add code to display images and voice as needed

        return view;
    }
}

