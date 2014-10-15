package com.dhenry.lendingclubscraper.app.lendingClub;

import com.dhenry.lendingclubscraper.app.persistence.models.AccountDetailsData;
import com.dhenry.lendingclubscraper.app.persistence.models.AccountSummaryData;
import com.dhenry.lendingclubscraper.app.persistence.models.CheckInOrderResult;
import com.dhenry.lendingclubscraper.app.persistence.models.NARCalculationData;
import com.dhenry.lendingclubscraper.app.persistence.models.NoteData;
import com.dhenry.lendingclubscraper.app.persistence.models.NotesPagedResult;
import com.dhenry.lendingclubscraper.app.persistence.models.UserData;

import java.util.Collection;

public interface LendingClubAPI {

    void login(final UserData userData, final ResponseHandler<Boolean> handler);

    void getAccountSummary(final UserData userData, final ResponseHandler<AccountSummaryData> handler);

    void getNetAnnualizedReturnData(final UserData userData, final ResponseHandler<NARCalculationData> handler);

    void getAccountDetails(final UserData user, final ResponseHandler<AccountDetailsData> handler);

    void getAvailableNotes(int startIndex, int numItemsPage, final ResponseHandler<NotesPagedResult> handler);

    void addNotesToOrder(final UserData currentUser, final Collection<NoteData> notes, final ResponseHandler<CheckInOrderResult> handler);
}
