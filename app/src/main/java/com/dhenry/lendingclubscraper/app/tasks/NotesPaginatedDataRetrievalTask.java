package com.dhenry.lendingclubscraper.app.tasks;

import android.util.Log;
import android.util.Pair;

import com.dhenry.lendingclubscraper.app.connectors.LendingClubRESTConnector;
import com.dhenry.lendingclubscraper.app.exceptions.LendingClubException;
import com.dhenry.lendingclubscraper.app.persistence.models.NoteData;
import com.dhenry.lendingclubscraper.app.views.RemoteTaskCallback;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class NotesPaginatedDataRetrievalTask extends RemoteTask<Integer, Void, List<NoteData>> {

    public final static String LOG_TAG = NotesPaginatedDataRetrievalTask.class.getCanonicalName();

    @Override
    protected RemoteTaskResult<List<NoteData>> doInBackground(Integer... pagintationParams) {
        try {
            List<NoteData> availableNotes = getAvailableNotes(pagintationParams[0], pagintationParams[1]);

            return new RemoteTaskResult<List<NoteData>>(availableNotes);

        } catch (LendingClubException lendingClubException) {
            Log.e(LOG_TAG, "Caught lendingClubException => " + lendingClubException.getMessage());
            return new RemoteTaskResult<List<NoteData>>(lendingClubException);
        }
    }

    public NotesPaginatedDataRetrievalTask(RemoteTaskCallback<List<NoteData>> parent) {
        super(new WeakReference<RemoteTaskCallback<List<NoteData>>>(parent));
    }

    private List<NoteData> getAvailableNotes(Integer startIndex, Integer resultsPerPage) throws LendingClubException {
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
