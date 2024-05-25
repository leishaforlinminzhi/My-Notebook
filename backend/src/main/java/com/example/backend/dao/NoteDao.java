package com.example.backend.dao;

import com.example.backend.entity.Note;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteDao {
    /**查当前noteid**/
    int getNoteID();
    /**增**/
    int insert(Note note);
    /**查note**/
    Note getByNoteId(Integer noteId);
    /**查user的所有note**/
    List<Note> getByUserId(Integer id);
    /**关键词搜索**/
    List<Note> getByKey(Integer id, String key);
    /**tag检索**/
    List<Note> getByTag(Integer id, String tag);
}
