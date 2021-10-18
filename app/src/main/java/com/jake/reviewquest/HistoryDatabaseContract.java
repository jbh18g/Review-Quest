package com.jake.reviewquest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import static com.jake.reviewquest.HistoryContentProvider.HISTORY_URI;

public class HistoryDatabaseContract {

    public static void addEntry(Context context, ContentValues values)
    {
        context.getContentResolver().insert(HISTORY_URI, values);
    }

    public static void emptyDatabase(Context context)
    {
        context.getContentResolver().delete(HISTORY_URI, null, null);
    }

    public static Cursor queryEntireDatabase(Context context, String orderBy)
    {
        HistoryContentProvider.HistoryDatabaseHelper db = new HistoryContentProvider.HistoryDatabaseHelper(context);
        return db.getReadableDatabase().query(HistoryContentProvider.DATABASE_NAME,
                null, null, null, null, null, orderBy);
    }

    public static Cursor queryByNumber(Context context, String phoneNumber)
    {
        HistoryContentProvider.HistoryDatabaseHelper db = new HistoryContentProvider.HistoryDatabaseHelper(context);
        return db.getReadableDatabase().query(HistoryContentProvider.DATABASE_NAME, null,
                HistoryContentProvider.PHONE_NUMBER + " = " + phoneNumber, null, null, null, null);
    }

    public static int updateEntry(Context context, String phoneNumber, ContentValues values) {
        HistoryContentProvider.HistoryDatabaseHelper db = new HistoryContentProvider.HistoryDatabaseHelper(context);
        return db.getWritableDatabase().update(HistoryContentProvider.DATABASE_NAME, values, HistoryContentProvider.PHONE_NUMBER + " = " + phoneNumber, null);

    }
}
