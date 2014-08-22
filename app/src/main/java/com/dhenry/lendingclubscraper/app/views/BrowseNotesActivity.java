package com.dhenry.lendingclubscraper.app.views;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.dhenry.lendingclubscraper.app.R;
import com.dhenry.lendingclubscraper.app.adapters.NoteAdapter;
import com.dhenry.lendingclubscraper.app.persistence.DatabaseHelper;
import com.dhenry.lendingclubscraper.app.persistence.models.NoteData;
import com.dhenry.lendingclubscraper.app.tasks.NotesPaginatedDataRetrievalTask;
import com.dhenry.lendingclubscraper.app.utilities.NumberFormats;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;

import java.text.NumberFormat;
import java.util.List;

public class BrowseNotesActivity extends OrmLiteBaseListActivity<DatabaseHelper> implements RemoteTaskCallback<List<NoteData>> {

    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browse_notes);

        adapter = new NoteAdapter(this);
        setListAdapter(adapter);

        //retrieve the first 25 notes
        new NotesPaginatedDataRetrievalTask(BrowseNotesActivity.this).execute(0, 25);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setEmptyView(empty);
    }


    @Override
    public void onTaskError(Exception exception) {
        Toast.makeText(BrowseNotesActivity.this, "Note Retrieval Failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskSuccess(List<NoteData> result) {

        for (NoteData noteData : result) {
            adapter.add(noteData);
        }
    }
}
