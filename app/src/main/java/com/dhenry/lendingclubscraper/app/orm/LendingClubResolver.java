package com.dhenry.lendingclubscraper.app.orm;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.dhenry.lendingclubscraper.app.orm.model.AccountSummaryData;
import com.dhenry.lendingclubscraper.app.provider.LendingClubSchema;

/**
 * Author: Dave
 */
public class LendingClubResolver {

    private ContentResolver cr;

    private Uri accountSummaryURI = LendingClubSchema.AccountSummary.CONTENT_URI;

    public LendingClubResolver(Activity activity) {
        cr = activity.getContentResolver();
    }

    /**
     * Update or insert a {@link AccountSummaryData}
     *
     * @param accountSummaryObject object to be inserted
     * @throws RemoteException
     * @return Uri the newly created objects path
     */
    public Uri insert(final AccountSummaryData accountSummaryObject) throws RemoteException {
        ContentValues tempCV = accountSummaryObject.getCV();
        tempCV.remove(LendingClubSchema.AccountSummary.Cols.ID);

        return cr.insert(accountSummaryURI, tempCV);
    }

    /**
     * Delete an {@link AccountSummaryData} using the userEmail field
     *
     * @param accountSummaryObject object to be inserted
     * @return int number of rows deleted
     * @throws RemoteException
     */
    public int deleteByUserEmail(final AccountSummaryData accountSummaryObject) throws RemoteException {
        String[] args = { String.valueOf(accountSummaryObject.getUserEmail()) };
        return cr.delete(accountSummaryURI, LendingClubSchema.AccountSummary.Cols.USER_EMAIL + " = ? ", args);
    }

    /**
     * Get the current users accountSummaryData
     * @return the current users AccountSummaryData
     */
    public AccountSummaryData getAccountSummaryData() {
        // query the C.R.
        Cursor result = cr.query(accountSummaryURI, null, null, null, null);
        // make return object
        AccountSummaryData rValue = AccountSummaryCreator.getAccountSummaryDataFromCursor(result);

        if (result != null) {
            result.close();
        }

        // return 'return object'
        return rValue;

    }
}
