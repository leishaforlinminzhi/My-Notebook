package com.example.backend.service;

import com.example.backend.entity.Note;

import java.util.List;

public interface NoteService {

    int insert(Note note);

    List<Note> getById(Integer id);
}
