package com.example.backend.controller;

import com.example.backend.entity.Note;
import com.example.backend.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;


@RestController
@RequestMapping("/note")
public class NoteController {
    @Autowired
    private NoteService noteService;

    @RequestMapping("/save")
    @ResponseBody
    public Note save(Integer id, Integer noteID, String title, String text, String images, String tags, String voice) {
        Note note = new Note();

        note.setId(id);
        note.setNoteID(noteID);
        note.setTitle(title);
        note.setText(text);
        note.setImages(images);
        note.setTags(tags);
        note.setVoice(voice);

        int result = noteService.insert(note);
        System.out.println(result);

        return note;
    }


    @RequestMapping("/getByUserId")
    @ResponseBody
    public List<Note> getByUserId(Integer id) {
        return this.noteService.getByUserId(id);
    }

    @RequestMapping("/getByNoteId")
    @ResponseBody
    public Note getByNoteId(Integer noteID) {
        return this.noteService.getByNoteId(noteID);
    }

    @RequestMapping("/getByKey")
    @ResponseBody
    public List<Note> getByKey(Integer id, String key) {
        return this.noteService.getByKey(id, key);
    }

    @RequestMapping("/getByTag")
    @ResponseBody
    public List<Note> getByTag(Integer id, String tag) { return noteService.getByTag(id, tag); }

    @RequestMapping("/getNoteID")
    @ResponseBody
    public int getNoteID() {
        System.out.println(noteService.getNoteId());
        return noteService.getNoteId();
    }

    // 好像用不上
    @RequestMapping("/getAllNotes")
    @ResponseBody
    public List<Note> getAllNotes() {
        return noteService.getAllNotes();
    }

    @RequestMapping("/deleteByNoteId")
    @ResponseBody
    public String deleteByNoteId(Integer noteID) {
        int rowsAffected = this.noteService.deleteByNoteId(noteID);
        if (rowsAffected > 0) {
            return "Success";
        } else {
            return "Failed";
        }
    }

}

