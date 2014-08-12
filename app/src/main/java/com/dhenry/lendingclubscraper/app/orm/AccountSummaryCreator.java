package com.dhenry.lendingclubscraper.app.orm;

import android.content.ContentValues;
import android.database.Cursor;

import com.dhenry.lendingclubscraper.app.orm.model.AccountSummaryData;
import com.dhenry.lendingclubscraper.app.provider.LendingClubSchema;

import java.util.ArrayList;

/**
 * Author: Dave
 */
public class AccountSummaryCreator {

    /**
    * Create a ContentValues from a provided AccountSummaryData.
    *
    * @param data AccountSummaryData to be converted.
    * @return ContentValues that is created from the AccountSummaryData object
    */
    public static ContentValues getCVfromAccountSummary(final AccountSummaryData data) {
        ContentValues rValue = new ContentValues();
        rValue.put(LendingClubSchema.AccountSummary.Cols.ID, data.getId());
        rValue.put(LendingClubSchema.AccountSummary.Cols.USER_EMAIL, data.getUserEmail());
        rValue.put(LendingClubSchema.AccountSummary.Cols.NET_ANNUALIZED_RETURN, data.getNetAnnualizedReturn());
        rValue.put(LendingClubSchema.AccountSummary.Cols.ADJUSTED_NET_ANNUALIZED_RETURN, data.getAdjustedNetAnnualizedReturn());
        rValue.put(LendingClubSchema.AccountSummary.Cols.ADJUSTED_ACCOUNT_VALUE, data.getAdjustedAccountValues());
        rValue.put(LendingClubSchema.AccountSummary.Cols.INTEREST_RECEIVED, data.getInterestReceived());
        rValue.put(LendingClubSchema.AccountSummary.Cols.AVAILABLE_CASH, data.getAvailableCash());
        rValue.put(LendingClubSchema.AccountSummary.Cols.IN_FUNDING_NOTES, data.getInFundingNotes());
        rValue.put(LendingClubSchema.AccountSummary.Cols.OUTSTANDING_PRINCIPLE, data.getOutstandingPrinciple());
        rValue.put(LendingClubSchema.AccountSummary.Cols.ACCOUNT_VALUE, data.getAccountValue());
        rValue.put(LendingClubSchema.AccountSummary.Cols.PAST_DUE_NOTES_ADJUSTMENT, data.getPastDueNotesAdjustment());
        rValue.put(LendingClubSchema.AccountSummary.Cols.TOTAL_PAYMENTS, data.getTotalPayments());
        return rValue;
    }

    /**
     * Get all of the AccountSummaryData from the passed in cursor.
     *
     * @param cursor
     *            passed in cursor to get AccountSummaryData(s) of.
     * @return ArrayList<AccountSummaryData> The set of AccountSummaryData
     */
    public static ArrayList<AccountSummaryData> getAccountSummaryDataArrayListFromCursor(
            Cursor cursor) {
        ArrayList<AccountSummaryData> rValue = new ArrayList<AccountSummaryData>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    rValue.add(getAccountSummaryDataFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        }
        return rValue;
    }

    /**
     * Get the first AccountSummaryData from the passed in cursor.
     *
     * @param cursor passed in cursor
     * @return AccountSummaryData object or null if the Cursor is invalid
     */
    public static AccountSummaryData getAccountSummaryDataFromCursor(Cursor cursor) {

        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        long rowID = cursor.getLong(cursor
                .getColumnIndex(LendingClubSchema.AccountSummary.Cols.ID));
        String userEmail = cursor.getString(cursor
                .getColumnIndex(LendingClubSchema.AccountSummary.Cols.USER_EMAIL));
        double netAnnualizedReturns = cursor.getDouble(cursor
                .getColumnIndex(LendingClubSchema.AccountSummary.Cols.NET_ANNUALIZED_RETURN));
        double adjustedNetAnnualizedReturns = cursor.getDouble(cursor
                .getColumnIndex(LendingClubSchema.AccountSummary.Cols.ADJUSTED_NET_ANNUALIZED_RETURN));
        double adjustedAccountValues = cursor.getDouble(cursor
                .getColumnIndex(LendingClubSchema.AccountSummary.Cols.ADJUSTED_ACCOUNT_VALUE));
        double interestReceived = cursor.getDouble(cursor
                .getColumnIndex(LendingClubSchema.AccountSummary.Cols.INTEREST_RECEIVED));
        double availableCash = cursor.getDouble(cursor
                .getColumnIndex(LendingClubSchema.AccountSummary.Cols.AVAILABLE_CASH));
        double inFundingNotes = cursor.getDouble(cursor
                .getColumnIndex(LendingClubSchema.AccountSummary.Cols.IN_FUNDING_NOTES));
        double outstandingPrinciple = cursor.getDouble(cursor
                .getColumnIndex(LendingClubSchema.AccountSummary.Cols.OUTSTANDING_PRINCIPLE));
        double accountValue = cursor.getDouble(cursor
                .getColumnIndex(LendingClubSchema.AccountSummary.Cols.ACCOUNT_VALUE));
        double pastDueNotesAdjustment = cursor.getDouble(cursor
                .getColumnIndex(LendingClubSchema.AccountSummary.Cols.PAST_DUE_NOTES_ADJUSTMENT));
        double totalPayments = cursor.getDouble(cursor
                .getColumnIndex(LendingClubSchema.AccountSummary.Cols.TOTAL_PAYMENTS));

        // construct the returned object
        return new AccountSummaryData(rowID, userEmail, netAnnualizedReturns, adjustedNetAnnualizedReturns,
                adjustedAccountValues, interestReceived, availableCash, inFundingNotes,
                outstandingPrinciple, accountValue, pastDueNotesAdjustment, totalPayments);
    }
}
