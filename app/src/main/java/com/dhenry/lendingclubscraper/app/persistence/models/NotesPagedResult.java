package com.dhenry.lendingclubscraper.app.persistence.models;

import java.util.List;

public class NotesPagedResult {
    private List<NoteData> notes;
    private int totalRecords;
    private int startIndex;
    private int endIndex;

    public NotesPagedResult(final List<NoteData> notes, final int totalRecords, int startIndex, int endIndex) {
        this.notes = notes;
        this.totalRecords = totalRecords;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public List<NoteData> getNotes() {
        return notes;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}
