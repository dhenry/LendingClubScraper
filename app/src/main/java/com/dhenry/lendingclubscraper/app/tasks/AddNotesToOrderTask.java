package com.dhenry.lendingclubscraper.app.tasks;

import android.util.Log;

import com.dhenry.lendingclubscraper.app.connectors.LendingClubRESTConnector;
import com.dhenry.lendingclubscraper.app.exceptions.LendingClubException;
import com.dhenry.lendingclubscraper.app.persistence.models.CheckInOrderResult;
import com.dhenry.lendingclubscraper.app.persistence.models.NoteData;
import com.dhenry.lendingclubscraper.app.persistence.models.UserData;
import com.dhenry.lendingclubscraper.app.views.RemoteTaskCallback;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;

public class AddNotesToOrderTask extends RemoteTask<Collection<NoteData>, Void, CheckInOrderResult> {

public final static String LOG_TAG = NotesPaginatedDataRetrievalTask.class.getCanonicalName();

    private final UserData currentUser;

    public AddNotesToOrderTask(RemoteTaskCallback<CheckInOrderResult> parent, UserData currentUser) {
        super(new WeakReference<RemoteTaskCallback<CheckInOrderResult>>(parent));
        this.currentUser = currentUser;
    }

    @Override
    protected RemoteTaskResult<CheckInOrderResult> doInBackground(Collection<NoteData>... notesParams) {
        try {
            CheckInOrderResult notesPagedResult = addNotesToOrder(notesParams[0]);

            return new RemoteTaskResult<CheckInOrderResult>(notesPagedResult);

        } catch (LendingClubException lendingClubException) {
            Log.e(LOG_TAG, "Caught lendingClubException => " + lendingClubException.getMessage());
            return new RemoteTaskResult<CheckInOrderResult>(lendingClubException);
        }
    }

    private CheckInOrderResult addNotesToOrder(Collection<NoteData> notes) throws LendingClubException {
        try {
            LendingClubRESTConnector restConnector = new LendingClubRESTConnector();

            for (NoteData note : notes) {
                restConnector.addNoteToOrder(note.getLoanGUID(), note.getAmountToInvest(),
                        currentUser.getUserEmail(), currentUser.getPassword());
            }

            return restConnector.checkInOrder(currentUser.getUserEmail(), currentUser.getPassword());

        } catch (IOException e) {
            throw new LendingClubException(e);
        } catch (JSONException e) {
            throw new LendingClubException(e);
        }
    }
}



