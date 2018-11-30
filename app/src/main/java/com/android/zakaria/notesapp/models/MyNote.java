package com.android.zakaria.notesapp.models;

public class MyNote {

    private String title, note, date, imagePath;
    private int id;

    public MyNote(int id, String title, String note, String date, String imagePath) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.date = date;
        this.imagePath = imagePath;
    }

    public MyNote(String title, String note, String date, String imagePath) {
        this.title = title;
        this.note = note;
        this.date = date;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
