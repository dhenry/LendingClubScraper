package com.dhenry.lendingclubscraper.app.provider;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Author: Dave
 */
public class LendingClubSchema {

    /**
     * Project Related Constants
     */
    public static final String ORGANIZATIONAL_NAME = "com.dhenry";
    public static final String PROJECT_NAME = "lendingclubscraper";

    /**
     * ContentProvider Related Constants
     */
    public static final String AUTHORITY = ORGANIZATIONAL_NAME + "."
            + PROJECT_NAME + ".lendingclubprovider";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    // Representation of the AccountSummaryData
    public static class AccountSummary {
        // define a URI paths to access entity
        // BASE_URI/accountSummary - for list of accountSummary(s)
        // BASE_URI/accountSummary/* - retrieve specific accountSummary by id
        public static final String PATH = "accountSummary";

        public static final String PATH_FOR_ID = "accountSummary/*";

        // URI for all content stored as an accountSummary entity
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH).build();

        // the names and order of ALL columns, including internal use ones
        public static final String[] ALL_COLUMN_NAMES = { Cols.ID,
                Cols.NET_ANNUALIZED_RETURN, Cols.ADJUSTED_NET_ANNUALIZED_RETURN, Cols.ADJUSTED_ACCOUNT_VALUE,
                Cols.INTEREST_RECEIVED, Cols.AVAILABLE_CASH, Cols.IN_FUNDING_NOTES,
                Cols.OUTSTANDING_PRINCIPLE, Cols.ACCOUNT_VALUE, Cols.PAST_DUE_NOTES_ADJUSTMENT, Cols.TOTAL_PAYMENTS };

        public static ContentValues initializeWithDefault(final ContentValues assignedValues) {

            final ContentValues setValues = (assignedValues == null) ? new ContentValues()
                    : assignedValues;
            if (!setValues.containsKey(Cols.USER_EMAIL)) {
                setValues.put(Cols.USER_EMAIL, 0);
            }
            if (!setValues.containsKey(Cols.NET_ANNUALIZED_RETURN)) {
                setValues.put(Cols.NET_ANNUALIZED_RETURN, 0);
            }
            if (!setValues.containsKey(Cols.ADJUSTED_NET_ANNUALIZED_RETURN)) {
                setValues.put(Cols.ADJUSTED_NET_ANNUALIZED_RETURN, 0);
            }
            if (!setValues.containsKey(Cols.ADJUSTED_ACCOUNT_VALUE)) {
                setValues.put(Cols.ADJUSTED_ACCOUNT_VALUE, 0);
            }
            if (!setValues.containsKey(Cols.INTEREST_RECEIVED)) {
                setValues.put(Cols.INTEREST_RECEIVED, 0);
            }
            if (!setValues.containsKey(Cols.AVAILABLE_CASH)) {
                setValues.put(Cols.AVAILABLE_CASH, 0);
            }
            if (!setValues.containsKey(Cols.IN_FUNDING_NOTES)) {
                setValues.put(Cols.IN_FUNDING_NOTES, 0);
            }
            if (!setValues.containsKey(Cols.OUTSTANDING_PRINCIPLE)) {
                setValues.put(Cols.OUTSTANDING_PRINCIPLE, 0);
            }
            if (!setValues.containsKey(Cols.ACCOUNT_VALUE)) {
                setValues.put(Cols.ACCOUNT_VALUE, 0);
            }
            if (!setValues.containsKey(Cols.PAST_DUE_NOTES_ADJUSTMENT)) {
                setValues.put(Cols.PAST_DUE_NOTES_ADJUSTMENT, 0);
            }
            if (!setValues.containsKey(Cols.TOTAL_PAYMENTS)) {
                setValues.put(Cols.TOTAL_PAYMENTS, 0);
            }
            return setValues;
        }

        // a static class to store columns in entity
        public static class Cols {
            public static final String ID = BaseColumns._ID; // convention
            // The name and column index of each column in your database
            public static final String USER_EMAIL = "USER_EMAIL";
            public static final String NET_ANNUALIZED_RETURN = "NET_ANNUALIZED_RETURN";
            public static final String ADJUSTED_NET_ANNUALIZED_RETURN = "ADJUSTED_NET_ANNUALIZED_RETURN";
            public static final String ADJUSTED_ACCOUNT_VALUE = "ADJUSTED_ACCOUNT_VALUE";
            public static final String INTEREST_RECEIVED = "INTEREST_RECEIVED";
            public static final String AVAILABLE_CASH = "AVAILABLE_CASH";
            public static final String IN_FUNDING_NOTES = "IN_FUNDING_NOTES";
            public static final String OUTSTANDING_PRINCIPLE = "OUTSTANDING_PRINCIPLE";
            public static final String ACCOUNT_VALUE = "ACCOUNT_VALUE";
            public static final String PAST_DUE_NOTES_ADJUSTMENT = "PAST_DUE_NOTES_ADJUSTMENT";
            public static final String TOTAL_PAYMENTS = "TOTAL_PAYMENTS";
        }
    }

}
