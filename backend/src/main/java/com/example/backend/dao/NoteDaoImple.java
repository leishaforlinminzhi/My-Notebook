package com.example.backend.dao;

import com.example.backend.entity.Note;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Repository
public class NoteDaoImple implements NoteDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int getNoteID() {
        String sql = "SELECT MAX(noteID) FROM Notes";
        Integer maxNoteID = jdbcTemplate.queryForObject(sql, Integer.class);
        if (maxNoteID == null) {
            return 0; // 如果表中没有记录，返回0或者其他默认值
        } else {
            return maxNoteID;
        }
    }

    @Override
    public int insert(Note note) {
        String sql = "insert into Notes(id,noteID,title,text,images,tags,voice) values(?,?,?,?,?,?,?)";
        return this.jdbcTemplate.update(
                sql,
                note.getId(),
                note.getNoteID(),
                note.getTitle(),
                note.getText(),
                note.getImages(),
                note.getTags(),
                note.getVoice()
        );
    }

    @Override
    public Note getByNoteId(Integer noteID) {
        String sql = "select * from Notes where noteID = ?";
        return this.jdbcTemplate.queryForObject(sql, new RowMapper<Note>() {
            @Override
            public Note mapRow(ResultSet resultSet, int i) throws SQLException {
                Note note = new Note();
                note.setId(resultSet.getInt("id"));
                note.setNoteID(resultSet.getInt("noteID"));
                note.setTitle(resultSet.getString("title"));
                note.setText(resultSet.getString("text"));
                note.setImages(resultSet.getString("images"));
                note.setTags(resultSet.getString("tags"));
                note.setVoice(resultSet.getString("voice"));
                return note;
            }
        }, noteID);
    }

    @Override
    public List<Note> getByUserId(Integer id) {
        String sql = "SELECT * FROM Notes WHERE id = ?";
        return this.jdbcTemplate.query(sql, new RowMapper<Note>() {
            @Override
            public Note mapRow(ResultSet resultSet, int i) throws SQLException {
                Note note = new Note();
                note.setId(resultSet.getInt("id"));
                note.setNoteID(resultSet.getInt("noteID"));
                note.setTitle(resultSet.getString("title"));
                note.setText(resultSet.getString("text"));
                note.setImages(resultSet.getString("images"));
                note.setTags(resultSet.getString("tags"));
                note.setVoice(resultSet.getString("voice"));
                return note;
            }
        }, id);
    }


    @Override
    public List<Note> getByKey(Integer id, String key) {
        String sql = "SELECT * FROM Notes WHERE id = ? AND (title LIKE ? OR text LIKE ?)";
        String likePattern = "%" + key + "%";
        return jdbcTemplate.query(sql, new Object[]{id, likePattern, likePattern}, new RowMapper<Note>() {
            @Override
            public Note mapRow(ResultSet resultSet, int i) throws SQLException {
                Note note = new Note();
                note.setId(resultSet.getInt("id"));
                note.setNoteID(resultSet.getInt("noteID"));
                note.setTitle(resultSet.getString("title"));
                note.setText(resultSet.getString("text"));
                note.setImages(resultSet.getString("images"));
                note.setTags(resultSet.getString("tags"));
                note.setVoice(resultSet.getString("voice"));
                return note;
            }
        });
    }

    @Override
    public List<Note> getByTag(Integer id, String tag) {
        String sql = "SELECT * FROM Notes WHERE id = ? AND tags LIKE ?";
        String likePattern = "%" + tag + "%";
        return jdbcTemplate.query(sql, new Object[]{id, likePattern}, new RowMapper<Note>() {
            @Override
            public Note mapRow(ResultSet resultSet, int i) throws SQLException {
                Note note = new Note();
                note.setId(resultSet.getInt("id"));
                note.setNoteID(resultSet.getInt("noteID"));
                note.setTitle(resultSet.getString("title"));
                note.setText(resultSet.getString("text"));
                note.setImages(resultSet.getString("images"));
                note.setTags(resultSet.getString("tags"));
                note.setVoice(resultSet.getString("voice"));
                return note;
            }
        });
    }



    @Override
    public List<Note> getAllNotes() {
        String sql = "SELECT * FROM Notes";
        return jdbcTemplate.query(sql, new RowMapper<Note>() {
            @Override
            public Note mapRow(ResultSet resultSet, int i) throws SQLException {
                Note note = new Note();
                note.setId(resultSet.getInt("id"));
                note.setNoteID(resultSet.getInt("noteID"));
                note.setTitle(resultSet.getString("title"));
                note.setText(resultSet.getString("text"));
                note.setImages(resultSet.getString("images"));
                note.setTags(resultSet.getString("tags"));
                note.setVoice(resultSet.getString("voice"));
                return note;
            }
        });
    }

    @Override
    public int deleteByNoteId(Integer noteID) {
        String sql = "DELETE FROM Notes WHERE noteID = ?";
        return this.jdbcTemplate.update(sql, noteID);
    }

}
