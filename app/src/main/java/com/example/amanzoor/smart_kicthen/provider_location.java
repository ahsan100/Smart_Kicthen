package com.example.amanzoor.smart_kicthen;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.aware.utils.DatabaseHelper;

import java.io.File;
import java.util.HashMap;

/**
 * Created by ahsanmanzoor on 28/04/16.
 */
public class provider_location extends ContentProvider {

    public static String AUTHORITY = "com.myapplication.provider";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "location.db";

    private static final int MESSAGE = 1;
    private static final int MESSAGE_ID = 2;



    @Override
    public boolean onCreate() {
        System.out.println("INSIDE create");
        AUTHORITY = getContext().getPackageName() + ".provider.location";
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0], MESSAGE);
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0]+"/#", MESSAGE_ID);

        tableMap = new HashMap<String, String>();
        tableMap.put(BasicData._ID, BasicData._ID);
        tableMap.put(BasicData.NAMING, BasicData.NAMING);
        tableMap.put(BasicData.LATITUDE1, BasicData.LATITUDE1);
        tableMap.put(BasicData.LONGITUDE1, BasicData.LONGITUDE1);
        tableMap.put(BasicData.LATITUDE2, BasicData.LATITUDE2);
        tableMap.put(BasicData.LONGITUDE2, BasicData.LONGITUDE2);
        return true;
    }


    public static final class BasicData implements BaseColumns {
        private BasicData(){};
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/location");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.myapplication.location";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.myapplication.location";
        public static final String _ID = "_ID";
        public static final String NAMING = "name";
        public static final String LATITUDE1 = "latitude1";
        public static final String LONGITUDE1 = "longitude1";
        public static final String LATITUDE2 = "latitude2";
        public static final String LONGITUDE2 = "longitude2";


    }

    public static final String[] DATABASE_TABLES = { "location" };
    public static final String[] TABLES_FIELDS = {
            BasicData._ID + " integer primary key autoincrement,"
                    + BasicData.NAMING + " text default '',"
                    + BasicData.LATITUDE1 + " text default '',"
                    + BasicData.LONGITUDE1 + " text default '',"
                    + BasicData.LATITUDE2 + " text default '',"
                    + BasicData.LONGITUDE2 + " text default ''," + "UNIQUE("
                    + BasicData._ID + ")" };

    private static UriMatcher sUriMatcher = null;
    private static HashMap<String, String> tableMap = null;
    private static DatabaseHelper databaseHelper = null;
    private static SQLiteDatabase database = null;

    private boolean initializeDB() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper( getContext(), DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS );
        }
        if( databaseHelper != null && ( database == null || ! database.isOpen() )) {
            database = databaseHelper.getWritableDatabase();
        }
        return( database != null && databaseHelper != null);
    }

    public static void resetDB(Context c ) {

        File db = new File(DATABASE_NAME);
        db.delete();
        databaseHelper = new DatabaseHelper( c, DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS);
        if( databaseHelper != null ) {
            database = databaseHelper.getWritableDatabase();
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY, "Database unavailable...");
            return null;
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case MESSAGE:
                qb.setTables(DATABASE_TABLES[0]);
                qb.setProjectionMap(tableMap);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            Cursor c = qb.query(database, projection, selection, selectionArgs, null, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        } catch (IllegalStateException e) {
            Log.e("ERRoR", e.getMessage());
            return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MESSAGE:
                return BasicData.CONTENT_TYPE;
            case MESSAGE_ID:
                return BasicData.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues new_values) {
        if (!initializeDB()) {
            Log.w(AUTHORITY, "Database unavailable...");
            return null;
        }

        ContentValues values = (new_values != null) ? new ContentValues(new_values) : new ContentValues();

        switch (sUriMatcher.match(uri)) {
            case MESSAGE:
                long _id = database.insert(DATABASE_TABLES[0], BasicData._ID, values);
                if (_id > 0) {
                    Uri dataUri = ContentUris.withAppendedId(BasicData.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY, "Database unavailable...");
            return 0;
        }
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case MESSAGE:
                count = database.delete(DATABASE_TABLES[0], selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case MESSAGE:
                count = database.update(DATABASE_TABLES[0], values, selection, selectionArgs);
                break;
            default:
                database.close();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
