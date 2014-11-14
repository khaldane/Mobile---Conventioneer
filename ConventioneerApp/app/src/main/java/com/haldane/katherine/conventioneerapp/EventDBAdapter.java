package com.haldane.katherine.conventioneerapp;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.DateFormat;

public class EventDBAdapter {
    static final String KEY_ID = "event_id";
    static final String KEY_EVENT_NAME = "event_name";
    static final String KEY_LOCATION = "location";
    static final String KEY_URL = "url";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_START_DATE = "start_date";
    static final String KEY_TIME = "time";
    static final String KEY_TYPE= "type";
    static final String KEY_IMGFLAG= "imgflag";
    static final String TAG = "ConventionDBAdapter";

    static final String DATABASE_NAME = "ConventioneerDB";
    static final String DATABASE_TABLE = "event";
    static final String DATABASE_MEL_TABLE = "myEventList";
    static final int DATABASE_VERSION = 1;

    private final String MYEVENTLIST_QUERY = "SELECT e.event_id, e.event_name, e.location, wl.check_in, e.description, e.start_date, e.time, e.type FROM " + DATABASE_TABLE + " as e INNER JOIN " + DATABASE_MEL_TABLE + " as wl ON e.event_id=wl.event_id WHERE wl.username=?";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    DateFormat dateFormat = DateFormat.getDateTimeInstance();

    public EventDBAdapter(Context ctx)
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
    public EventDBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---retrieves all the contacts---
    public Cursor getAllEvents()
    {
        Cursor mCursor =  db.query(
                DATABASE_TABLE,
                new String[] {KEY_ID, KEY_EVENT_NAME,KEY_LOCATION, KEY_URL, KEY_DESCRIPTION, KEY_START_DATE, KEY_TIME, KEY_TYPE, KEY_IMGFLAG},
                null, null, null, null, null
        );

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---retrieves all the contacts---
    public Cursor getMyEventListEvents(String username)
    {
        Cursor mCursor = db.rawQuery(MYEVENTLIST_QUERY, new String[]{username});
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---retrieves all the contacts---
    public Cursor getEventsByType(String eventType)
    {
        Cursor mCursor = db.query(
                true,
                DATABASE_TABLE,
                new String[] {KEY_ID, KEY_EVENT_NAME,KEY_LOCATION, KEY_URL, KEY_DESCRIPTION, KEY_START_DATE, KEY_TIME, KEY_TYPE, KEY_IMGFLAG},
                KEY_TYPE + "=?",
                new String[] {eventType},
                null, null, null, null, null
        );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---retrieves all the contacts---
    public Cursor getEventTypes()
    {
        Cursor mCursor = db.rawQuery("select DISTINCT "+KEY_TYPE+" from "+DATABASE_TABLE, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    //---retrieves a particular contact---
    public Cursor getEvent(String eventId) throws SQLException
    {
        Cursor mCursor = db.query(
                true,
                DATABASE_TABLE,
                new String[] {KEY_ID, KEY_EVENT_NAME,KEY_LOCATION, KEY_URL, KEY_DESCRIPTION, KEY_START_DATE, KEY_TIME, KEY_TYPE, KEY_IMGFLAG},
                KEY_ID + "=?",
                new String[] {eventId},
                null, null, null, null, null
        );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}