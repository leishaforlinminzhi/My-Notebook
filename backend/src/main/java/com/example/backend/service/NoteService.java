package com.example.backend.service;

import com.example.backend.entity.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NoteService {

    int insert(Note note);

    int getNoteId();

    Note getByNoteId(Integer id);

    List<Note> getByUserId(Integer id);

    List<Note> getByKey(Integer id, String key);

    List<Note> getByTag(Integer id, String tag);

    List<Note> getAllNotes();

    int deleteByNoteId(Integer id);
}
