package com.example.backend.service;

import com.example.backend.dao.NoteDao;
import com.example.backend.dao.UserDao;
import com.example.backend.entity.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteServiceImple implements NoteService{
    @Autowired
    private NoteDao noteDao;

    @Override
    public int insert(Note note){ return noteDao.insert(note); }

    @Override
    public int getNoteId() { return noteDao.getNoteID();}

    @Override
    public Note getByNoteId(Integer noteID){ return noteDao.getByNoteId(noteID); }

    @Override
    public List<Note> getByUserId(Integer id) { return noteDao.getByUserId(id); }

    @Override
    public List<Note> getByKey(Integer id, String key) { return noteDao.getByKey(id, key); }

    @Override
    public List<Note> getByTag(Integer id, String tag) { return noteDao.getByTag(id, tag); }

    @Override
    public List<Note> getAllNotes() {
        return noteDao.getAllNotes();
    }

    @Override
    public int deleteByNoteId(Integer noteID) {
        return noteDao.deleteByNoteId(noteID);
    }

}
