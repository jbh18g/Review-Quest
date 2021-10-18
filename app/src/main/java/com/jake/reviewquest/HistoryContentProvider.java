package com.jake.reviewquest;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class HistoryContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.jake.reviewquest.historyprovider";
    public static final String DATABASE_NAME = "history_database";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String CONTACT_NAME = "contact_name";
    public static final String LAST_REQUEST_SENT_DATE = "date_sent";
    public static final String GOOGLE_CHECKED = "google_checked";
    public static final String YELP_CHECKED = "yelp_checked";
    public static final String FACEBOOK_CHECKED = "facebook_checked";
    private static final String SQL_TABLE = "CREATE TABLE " +
            DATABASE_NAME + " ( " +
            PHONE_NUMBER + " TEXT, " +
            CONTACT_NAME + " TEXT, " +
            LAST_REQUEST_SENT_DATE + " LONG, " +
            GOOGLE_CHECKED + " TEXT, " +
            YELP_CHECKED + " TEXT, " +
            FACEBOOK_CHECKED + " TEXT)";
    public static final Uri HISTORY_URI = Uri.parse("content://" + AUTHORITY + "/" + DATABASE_NAME);

    private HistoryDatabaseHelper databaseHelper;

    protected static final class HistoryDatabaseHelper extends SQLiteOpenHelper
    {

        HistoryDatabaseHelper(Context context)
        {
            super(context, "HISTORY_DATABASE", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            //no upgrades
        }
    }


    public HistoryContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        return databaseHelper.getWritableDatabase().delete(DATABASE_NAME, selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        String mimeType;
        if(ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()))
        {
            ContentResolver cr = getContext().getContentResolver();
            mimeType = cr.getType(uri);
        }
        else
        {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //String tableName = values.get()
        long id = databaseHelper.getWritableDatabase()
                .insert(DATABASE_NAME, null, values);
        return Uri.parse(HISTORY_URI + "/" + id);
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new HistoryDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return databaseHelper.getWritableDatabase()
                .query(String.valueOf(uri), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return databaseHelper.getWritableDatabase()
                .update(String.valueOf(uri), values, selection, selectionArgs);
    }
}