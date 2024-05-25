package com.example.backend.entity;

public class Note {
    private int id;

    private int noteID;

    private String title;

    private String text;

    private String tags;

    private String images;

    private String voice;

    public int getId(){ return this.id; }

    public void setId(int id){ this.id = id; }

    public int getNoteID(){ return this.noteID; }

    public void setNoteID(int noteID){ this.noteID = noteID; }

    public String getTitle(){ return this.title; }

    public void setTitle(String title){ this.title = title; }

    public String getText(){ return this.text; }

    public void setText(String text){ this.text = text; }

    public String getTags(){ return this.tags; }

    public void setTags(String tags){ this.tags = tags; }

    public String getImages(){ return this.images; }

    public void setImages(String images){ this.images = images; }

    public String getVoice(){ return this.voice; }

    public void setVoice(String voice){ this.voice = voice; }


    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", noteID='" + noteID + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", tags='" + tags + '\'' +
                ", images='" + images + '\'' +
                ", voice='" + voice + '\'' +
                '}';
    }
}
