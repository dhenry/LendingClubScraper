package com.dhenry.lendingclubscraper.app.utilities;

import com.dhenry.lendingclubscraper.app.persistence.models.NoteData;

public interface NoteOrderer {

    NoteData investIn(NoteData noteData, Integer amount);
}
