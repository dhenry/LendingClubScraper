package com.dhenry.lendingclubscraper.app.tasks;

import android.util.Log;
import android.util.Pair;

import com.dhenry.lendingclubscraper.app.connectors.LendingClubRESTConnector;
import com.dhenry.lendingclubscraper.app.exceptions.LendingClubException;
import com.dhenry.lendingclubscraper.app.persistence.models.NoteData;
import com.dhenry.lendingclubscraper.app.persistence.models.NotesPagedResult;
import com.dhenry.lendingclubscraper.app.views.RemoteTaskCallback;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class NotesPaginatedDataRetrievalTask extends RemoteTask<Integer, Void, NotesPagedResult> {

    public final static String LOG_TAG = NotesPaginatedDataRetrievalTask.class.getCanonicalName();

    @Override
    protected RemoteTaskResult<NotesPagedResult> doInBackground(Integer... pagintationParams) {
        try {
            NotesPagedResult notesPagedResult = getAvailableNotes(pagintationParams[0], pagintationParams[1]);

            return new RemoteTaskResult<NotesPagedResult>(notesPagedResult);

        } catch (LendingClubException lendingClubException) {
            Log.e(LOG_TAG, "Caught lendingClubException => " + lendingClubException.getMessage());
            return new RemoteTaskResult<NotesPagedResult>(lendingClubException);
        }
    }

    public NotesPaginatedDataRetrievalTask(RemoteTaskCallback<NotesPagedResult> parent) {
        super(new WeakReference<RemoteTaskCallback<NotesPagedResult>>(parent));
    }

    private NotesPagedResult getAvailableNotes(Integer startIndex, Integer resultsPerPage) throws LendingClubException {
        try {
            LendingClubRESTConnector restConnector = new LendingClubRESTConnector();
            return restConnector.viewPaginatedAvailableNotes(startIndex, resultsPerPage);
        } catch (IOException e) {
            throw new LendingClubException(e);
        } catch (JSONException e) {
            throw new LendingClubException(e);
        }
    }
}
