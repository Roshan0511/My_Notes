package com.roshan.mynotes.Models;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

public class NotesModel {

    String note;
    boolean completed;
    Timestamp time;
    String userId;
    String heading;

    public NotesModel() {
    }

    public NotesModel(String note,String heading, boolean completed, Timestamp time, String userId) {
        this.note = note;
        this.heading = heading;
        this.completed = completed;
        this.time = time;
        this.userId = userId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public String toString() {
        return "NotesModel{" +
                "note='" + note + '\'' +
                ", completed=" + completed +
                ", time=" + time +
                ", userId='" + userId + '\'' +
                ", heading='" + heading + '\'' +
                '}';
    }
}
