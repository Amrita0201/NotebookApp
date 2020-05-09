package com.tarento.notebook.models;

import java.util.List;

public class NoteResponse {
    Note note;
    List<Tag> tags;

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
