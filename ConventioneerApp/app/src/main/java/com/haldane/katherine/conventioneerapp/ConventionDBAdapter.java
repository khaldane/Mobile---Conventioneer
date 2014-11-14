package com.haldane.katherine.conventioneerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.text.DateFormat;

public class ConventionDBAdapter {
    static final String KEY_ID = "convention_id";
    static final String KEY_NAME = "name";
    static final String KEY_LOCATION = "location";
    static final String KEY_URL = "url";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_START_DATE = "start_date";
    static final String KEY_END_DATE = "end_date";
    static final String KEY_MAP_URL = "map";
    static final String TAG = "ConventionDBAdapter";

    static final String DATABASE_NAME = "ConventioneerDB";
    static final String DATABASE_TABLE = "convention";
    static final int DATABASE_VERSION = 1;

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    DateFormat dateFormat = DateFormat.getDateTimeInstance();

    public ConventionDBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS convention");
            onCreate(db);
        }
    }

    //---opens the database---
    public ConventionDBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---retrieves a particular contact---
    public Cursor getConvention(String  convention) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ID, KEY_NAME, KEY_LOCATION,
                                KEY_URL, KEY_DESCRIPTION, KEY_START_DATE, KEY_END_DATE, KEY_MAP_URL}, KEY_NAME + "=?", new String[] {convention}, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a contact---
    public boolean updateConvention(int id, String name, String location, String url, String description, Date startDate, Date endDate, String mapUrl)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_LOCATION, location);
        args.put(KEY_URL, url);
        args.put(KEY_DESCRIPTION, description);
        args.put(KEY_START_DATE, dateFormat.format(startDate));
        args.put(KEY_END_DATE, dateFormat.format(endDate));
        args.put(KEY_MAP_URL, mapUrl);
        return db.update(DATABASE_TABLE, args, KEY_ID + "=" + id, null) > 0;
    }

}


