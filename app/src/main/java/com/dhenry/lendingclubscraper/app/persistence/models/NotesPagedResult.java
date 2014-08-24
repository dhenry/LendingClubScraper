package com.dhenry.lendingclubscraper.app.persistence.models;

import java.util.List;

public class NotesPagedResult {
    private List<NoteData> notes;
    private int totalRecords;

    public NotesPagedResult(final List<NoteData> notes, final int totalRecords) {
        this.notes = notes;
        this.totalRecords = totalRecords;
    }

    public List<NoteData> getNotes() {
        return notes;
    }

    public int getTotalRecords() {
        return totalRecords;
    }
}
