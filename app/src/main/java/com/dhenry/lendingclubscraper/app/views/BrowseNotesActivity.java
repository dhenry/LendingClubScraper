package com.dhenry.lendingclubscraper.app.views;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dhenry.lendingclubscraper.app.R;
import com.dhenry.lendingclubscraper.app.constants.LendingClubConstants;
import com.dhenry.lendingclubscraper.app.lendingClub.LendingClubAPI;
import com.dhenry.lendingclubscraper.app.lendingClub.ResponseHandler;
import com.dhenry.lendingclubscraper.app.lendingClub.impl.LendingClubAPIClient;
import com.dhenry.lendingclubscraper.app.persistence.models.CheckInOrderResult;
import com.dhenry.lendingclubscraper.app.persistence.models.NoteData;
import com.dhenry.lendingclubscraper.app.persistence.models.NotesPagedResult;
import com.dhenry.lendingclubscraper.app.persistence.models.UserData;
import com.dhenry.lendingclubscraper.app.utilities.NoteOrderer;
import com.dhenry.lendingclubscraper.app.views.adapters.NoteAdapter;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class BrowseNotesActivity extends ListActivity implements NoteOrderer {

    @InjectView(R.id.prev) Button prevButton;
    @InjectView(R.id.next) Button nextButton;
    @InjectView(R.id.review_order_button) Button reviewOrderButton;

    private NoteAdapter adapter;
    private Integer pageCount;

    private SparseArray<NotesPagedResult> cachedResults = new SparseArray<NotesPagedResult>();
    private Map<String, NoteData> notesToInvestIn = new HashMap<String, NoteData>();

    private int increment = 0;

    public static final int NUM_ITEMS_PAGE = 25;
    public Integer totalListItems;

    private CheckInOrderHandler checkInOrderHandler = new CheckInOrderHandler();

    private LendingClubAPI lendingClubAPI;

    @OnClick(R.id.next) void getNextPage() {
        increment++;
        loadList(increment);
        togglePaginationButtons();
    }

    @OnClick(R.id.prev) void getPrevPage() {
        increment--;
        loadList(increment);
        togglePaginationButtons();
    }

    @OnClick(R.id.review_order_button) void checkInOrder() {
        Toast.makeText(BrowseNotesActivity.this, "Investing in " + notesToInvestIn.size() + " notes.", Toast.LENGTH_LONG).show();

        lendingClubAPI.addNotesToOrder(getIntent().<UserData>getParcelableExtra(LendingClubConstants.CURRENT_USER), notesToInvestIn.values(), checkInOrderHandler);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_notes);
        ButterKnife.inject(this);

        lendingClubAPI = new LendingClubAPIClient(this);

        prevButton.setEnabled(false);

        adapter = new NoteAdapter(this, this);
        setListAdapter(adapter);

        //retrieve the first 25 notes
        loadList(0);
    }

    private class CheckInOrderHandler implements ResponseHandler<CheckInOrderResult> {

        @Override
        public void onTaskError(Exception exception) {
            Toast.makeText(BrowseNotesActivity.this, "Check in order failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTaskSuccess(CheckInOrderResult result) {
            Toast.makeText(BrowseNotesActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();

            // TODO: go to order review screen
        }

        @Override
        public CheckInOrderResult getResult() {
            return null; // TODO
        }
    }

    private class RetrieveNotesHandler implements ResponseHandler<NotesPagedResult> {

        @Override
        public void onTaskError(Exception exception) {
            Toast.makeText(BrowseNotesActivity.this, "Note Retrieval Failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTaskSuccess(NotesPagedResult result) {

            cachedResults.put(result.getStartIndex(), result);

            totalListItems = result.getTotalRecords();

            setPageCount();

            displayResultsPage(result);
        }

        @Override
        public NotesPagedResult getResult() {
            return null; // TODO
        }
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
        int start = number * NUM_ITEMS_PAGE;

        // retrieve the result from the local cache or remotely
        if (cachedResults.get(start) == null) {

            lendingClubAPI.getAvailableNotes(start, NUM_ITEMS_PAGE, new RetrieveNotesHandler());
        } else {
            displayResultsPage(cachedResults.get(start));
        }
    }


    private void setPageCount() {
        if (pageCount == null) {
            int val = totalListItems % NUM_ITEMS_PAGE;
            val = val==0 ? 0 : 1;
            pageCount = totalListItems / NUM_ITEMS_PAGE + val;
        }
    }

    private void displayResultsPage(NotesPagedResult result) {
        adapter.clear();

        for (NoteData noteData : result.getNotes()) {
            adapter.add(noteData);
        }
    }

    // TODO: move to NoteData class
    public NoteData investIn(NoteData noteData, Integer amount) {
        NoteData note = notesToInvestIn.get(noteData.getLoanGUID());

        if (note == null) {
            note = noteData;
        }

        if (amount + note.getAmountToInvest() < 0) {
            Toast.makeText(BrowseNotesActivity.this, "Can't invest less than $0", Toast.LENGTH_LONG).show();
        } else {
            note.setIsInCurrentOrder(true);

            Integer amountToInvest = amount;
            if (note.getAmountToInvest() != null) {
                amountToInvest += note.getAmountToInvest();
            }

            note.setAmountToInvest(amountToInvest);
        }

        if (note.getAmountToInvest().equals(0)) {
            notesToInvestIn.remove(note.getLoanGUID());
            note.setIsInCurrentOrder(false);
            return note;
        }

        notesToInvestIn.put(note.getLoanGUID(), note);

        return note;
    }
}
