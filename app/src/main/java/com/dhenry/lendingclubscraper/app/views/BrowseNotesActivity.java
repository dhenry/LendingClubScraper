package com.dhenry.lendingclubscraper.app.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dhenry.lendingclubscraper.app.R;
import com.dhenry.lendingclubscraper.app.adapters.NoteAdapter;
import com.dhenry.lendingclubscraper.app.persistence.DatabaseHelper;
import com.dhenry.lendingclubscraper.app.persistence.models.NoteData;
import com.dhenry.lendingclubscraper.app.persistence.models.NotesPagedResult;
import com.dhenry.lendingclubscraper.app.tasks.NotesPaginatedDataRetrievalTask;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;

import java.util.ArrayList;

public class BrowseNotesActivity extends OrmLiteBaseListActivity<DatabaseHelper> implements RemoteTaskCallback<NotesPagedResult> {

    private ListView listview;
    private Button prevButton;
    private Button nextButton;

    private NoteAdapter adapter;
    private Integer pageCount ;

    /**
     * Using this increment value we can move the listview items
     */
    private int increment = 0;

    public static final int NUM_ITEMS_PAGE = 25;
    public Integer totalListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browse_notes);
        listview = (ListView)findViewById(android.R.id.list);
        nextButton = (Button)findViewById(R.id.next);
        prevButton = (Button)findViewById(R.id.prev);

        prevButton.setEnabled(false);

        adapter = new NoteAdapter(this);
        setListAdapter(adapter);

        //retrieve the first 25 notes
        loadList(0);

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                increment++;
                loadList(increment);
                togglePaginationButtons();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment--;
                loadList(increment);
                togglePaginationButtons();
            }
        });
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setEmptyView(empty);
    }

    private void togglePaginationButtons()
    {
        if(increment + 1 == pageCount)
        {
            nextButton.setEnabled(false);
        }
        else if(increment == 0)
        {
            prevButton.setEnabled(false);
        }
        else
        {
            prevButton.setEnabled(true);
            nextButton.setEnabled(true);
        }
    }

    private void loadList(int number)
    {
        //title.setText("Page "+(number+1)+" of "+pageCount);

        int start = number * NUM_ITEMS_PAGE;

        new NotesPaginatedDataRetrievalTask(BrowseNotesActivity.this).execute(start, NUM_ITEMS_PAGE);
    }

    @Override
    public void onTaskError(Exception exception) {
        Toast.makeText(BrowseNotesActivity.this, "Note Retrieval Failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskSuccess(NotesPagedResult result) {

        totalListItems = result.getTotalRecords();

        // check the number of pages
        if (pageCount == null) {
            int val = totalListItems % NUM_ITEMS_PAGE;
            val = val==0 ? 0 : 1;
            pageCount = totalListItems / NUM_ITEMS_PAGE + val;
        }

        adapter.removeAllViews();

        for (NoteData noteData : result.getNotes()) {
            adapter.add(noteData);
        }
    }
}
