package com.dhenry.lendingclubscraper.app.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Author: Dave
 */
public class AccountSummaryProvider extends ContentProvider {


    private SQLiteDatabase database;
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "lendingclubscraper";


    private static final String CREATE_ACCOUNT_SUMMARY_TABLE = " CREATE TABLE "
            + LendingClubSchema.AccountSummary.PATH + " (" + LendingClubSchema.AccountSummary.Cols.ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LendingClubSchema.AccountSummary.Cols.USER_EMAIL + " STRING NOT NULL, "
            + LendingClubSchema.AccountSummary.Cols.ACCOUNT_VALUE + " REAL NOT NULL, "
            + LendingClubSchema.AccountSummary.Cols.ADJUSTED_ACCOUNT_VALUE + " REAL NOT NULL, "
            + LendingClubSchema.AccountSummary.Cols.AVAILABLE_CASH + " REAL NOT NULL, "
            + LendingClubSchema.AccountSummary.Cols.IN_FUNDING_NOTES + " REAL NOT NULL, "
            + LendingClubSchema.AccountSummary.Cols.INTEREST_RECEIVED + " REAL NOT NULL, "
            + LendingClubSchema.AccountSummary.Cols.NET_ANNUALIZED_RETURN + " REAL NOT NULL, "
            + LendingClubSchema.AccountSummary.Cols.ADJUSTED_NET_ANNUALIZED_RETURN + " REAL NOT NULL, "
            + LendingClubSchema.AccountSummary.Cols.OUTSTANDING_PRINCIPLE + " REAL NOT NULL, "
            + LendingClubSchema.AccountSummary.Cols.PAST_DUE_NOTES_ADJUSTMENT + " REAL NOT NULL, "
            + LendingClubSchema.AccountSummary.Cols.TOTAL_PAYMENTS + " REAL NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_ACCOUNT_SUMMARY_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + LendingClubSchema.AccountSummary.PATH);
            onCreate(db);
        }

        // uncomment to clean house
        /*@Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            db.execSQL("DROP TABLE IF EXISTS " + LendingClubSchema.AccountSummary.PATH);
            onCreate(db);
        }*/
    }

    @Override
    public int delete(Uri arg0, String whereClause, String[] whereArgs) {
        int rowsDeleted = database.delete(LendingClubSchema.AccountSummary.PATH, whereClause, whereArgs);
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(LendingClubSchema.AccountSummary.CONTENT_URI, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri arg0) {
        // unimplemented
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = database.insert(LendingClubSchema.AccountSummary.PATH, "",
                LendingClubSchema.AccountSummary.initializeWithDefault(values));

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(
                    LendingClubSchema.AccountSummary.CONTENT_URI, rowID);
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(_uri, null);
            }
            return _uri;
        }
        throw new SQLException("Failed to add record into" + uri);
    }

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        database = dbHelper.getWritableDatabase();
        return (database != null);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(LendingClubSchema.AccountSummary.PATH);

        Cursor c = qb.query(database, projection, selection, selectionArgs, null, null, sortOrder);

        if (c != null && getContext() != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    @Override
    public int update(Uri arg0, ContentValues contentValues, String whereClause, String[] whereArgs) {
        return database.update(LendingClubSchema.AccountSummary.PATH, contentValues, whereClause, whereArgs);
    }
}
