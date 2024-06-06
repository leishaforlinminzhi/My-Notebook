package com.example.mynotebook.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotebook.DetailActivity;
import com.example.mynotebook.GlobalValue;
import com.example.mynotebook.R;
import com.example.mynotebook.model.Note;
import com.example.mynotebook.utils.HttpGetRequest;
import com.example.mynotebook.utils.NoteAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class fragment_record extends Fragment {

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> noteList = new ArrayList<>();
    private EditText editTextSearch;
    private Spinner spinnerSearchType;
    private Button buttonSearch;
    private Button buttonRefresh;

    private Integer id = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        GlobalValue app = (GlobalValue) requireActivity().getApplication();
        id = app.getId();

        View view = inflater.inflate(R.layout.fragment_record, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        spinnerSearchType = view.findViewById(R.id.spinnerSearchType);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        buttonRefresh = view.findViewById(R.id.buttonRefresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noteAdapter = new NoteAdapter(noteList, this::onNoteClicked, this::onDeleteClicked);
        recyclerView.setAdapter(noteAdapter);

        buttonSearch.setOnClickListener(v -> onSearchClicked());
        buttonRefresh.setOnClickListener(v -> onRefreshClicked());

        // Initial fetch of all notes
        fetchNotes("/note/getAllNotes");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Only refresh if needed, initial fetch handled in onCreateView
    }

    private void onNoteClicked(Note note) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("title", note.getTitle());
        intent.putExtra("text", note.getText());
        intent.putExtra("tags", note.getTags());
        intent.putExtra("id", note.getId());
        intent.putExtra("noteID", note.getNoteID());
        intent.putExtra("images", note.getImages());
        intent.putExtra("voice", note.getVoice());
        startActivity(intent);
    }

    private void onDeleteClicked(Note note) {
        new DeleteNoteTask().execute(note);
    }

    private void fetchNotes(String url) {
        noteList.clear();
        new FetchNotesTask().execute(url);
    }

    private void onSearchClicked() {
        String keyword = editTextSearch.getText().toString();
        String searchType = spinnerSearchType.getSelectedItem().toString();

        if (keyword.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a search keyword", Toast.LENGTH_SHORT).show();
            return;
        }

        String url;
        if (searchType.equals("By Content")) {
            url = "/note/getByKey?id=" + id + "&key=" + keyword;
        } else {
            url = "/note/getByTag?id=" + id + "&tag=" + keyword;
        }

        fetchNotes(url);
    }

    private void onRefreshClicked() {
        fetchNotes("/note/getAllNotes");
    }

    private class FetchNotesTask extends AsyncTask<String, Void, Object[]> {
        @Override
        protected Object[] doInBackground(String... params) {
            return new HttpGetRequest().sendGetRequest(params[0], new String[][]{});
        }

        @Override
        protected void onPostExecute(Object[] result) {
            if ("Success".equals(result[0])) {
                try {
                    JSONArray jsonArray = new JSONArray((String) result[1]);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Note note = new Note();
                        note.setId(jsonObject.getInt("id"));
                        note.setNoteID(jsonObject.getInt("noteID"));
                        note.setTitle(jsonObject.getString("title"));
                        note.setText(jsonObject.getString("text"));
                        note.setTags(jsonObject.getString("tags"));
                        note.setImages(jsonObject.getString("images"));
                        note.setVoice(jsonObject.getString("voice"));
                        noteList.add(note);
                    }
                    noteAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Data parsing error", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Request failed: " + result[0], Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DeleteNoteTask extends AsyncTask<Note, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Note... notes) {
            Note note = notes[0];
            return new HttpGetRequest().sendGetRequest("/note/deleteByNoteId?noteID=" + note.getNoteID(), new String[][]{});
        }

        @Override
        protected void onPostExecute(Object[] result) {
            if ("Success".equals(result[0])) {
                Toast.makeText(getActivity(), "Note deleted successfully", Toast.LENGTH_SHORT).show();
                onRefreshClicked();
            } else {
                Toast.makeText(getActivity(), "Failed to delete note: " + result[0], Toast.LENGTH_SHORT).show();
            }
        }
    }
}
