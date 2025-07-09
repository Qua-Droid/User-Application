package com.example.parentlauncher;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.content.Context;


public class AppRestrictionProvider extends ContentProvider {

    private static final String TAG = "AppRestrictionProvider";
    private static final String AUTHORITY = "com.example.parentlauncher.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/app_restrictions");

    // URI Matcher codes
    private static final int APP_RESTRICTIONS = 1;
    private static final int APP_RESTRICTIONS_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, "app_restrictions", APP_RESTRICTIONS);
        sUriMatcher.addURI(AUTHORITY, "app_restrictions/#", APP_RESTRICTIONS_ID);
    }

    private AppRestrictionDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new AppRestrictionDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case APP_RESTRICTIONS:
                cursor = db.query(AppRestrictionContract.AppRestrictionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case APP_RESTRICTIONS_ID:
                selection = AppRestrictionContract.AppRestrictionEntry._ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                cursor = db.query(AppRestrictionContract.AppRestrictionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case APP_RESTRICTIONS:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".app_restrictions";
            case APP_RESTRICTIONS_ID:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + ".app_restrictions";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case APP_RESTRICTIONS:
                id = db.insert(AppRestrictionContract.AppRestrictionEntry.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (id > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return Uri.withAppendedPath(CONTENT_URI, String.valueOf(id));
        } else {
            throw new android.database.SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case APP_RESTRICTIONS:
                rowsDeleted = db.delete(AppRestrictionContract.AppRestrictionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case APP_RESTRICTIONS_ID:
                selection = AppRestrictionContract.AppRestrictionEntry._ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                rowsDeleted = db.delete(AppRestrictionContract.AppRestrictionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case APP_RESTRICTIONS:
                rowsUpdated = db.update(AppRestrictionContract.AppRestrictionEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case APP_RESTRICTIONS_ID:
                selection = AppRestrictionContract.AppRestrictionEntry._ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                rowsUpdated = db.update(AppRestrictionContract.AppRestrictionEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    // Inner class for database helper
    private static class AppRestrictionDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "app_restrictions.db";
        private static final int DATABASE_VERSION = 2; // Incremented version for schema change

        public AppRestrictionDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            final String SQL_CREATE_APP_RESTRICTION_TABLE = "CREATE TABLE " +
                    AppRestrictionContract.AppRestrictionEntry.TABLE_NAME + " (" +
                    AppRestrictionContract.AppRestrictionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    AppRestrictionContract.AppRestrictionEntry.COLUMN_CHILD_USER_ID + " INTEGER NOT NULL," +
                    AppRestrictionContract.AppRestrictionEntry.COLUMN_CATEGORY + " TEXT," +
                    AppRestrictionContract.AppRestrictionEntry.COLUMN_PACKAGE_NAME + " TEXT," +
                    AppRestrictionContract.AppRestrictionEntry.COLUMN_IS_ALLOWED + " INTEGER NOT NULL DEFAULT 1," +
                    AppRestrictionContract.AppRestrictionEntry.COLUMN_IS_APP_SPECIFIC + " INTEGER NOT NULL DEFAULT 0);";
            db.execSQL(SQL_CREATE_APP_RESTRICTION_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < 2) {
                // Add the new column for existing databases
                db.execSQL("ALTER TABLE " + AppRestrictionContract.AppRestrictionEntry.TABLE_NAME + 
                          " ADD COLUMN " + AppRestrictionContract.AppRestrictionEntry.COLUMN_IS_APP_SPECIFIC + 
                          " INTEGER NOT NULL DEFAULT 0");
            }
        }
    }
}

