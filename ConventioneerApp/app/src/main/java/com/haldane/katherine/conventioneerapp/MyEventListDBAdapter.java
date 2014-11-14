package com.haldane.katherine.conventioneerapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyEventListDBAdapter {
    static final String KEY_ID = "my_event_list_id";
    static final String KEY_EVENT_ID = "event_id";
    static final String KEY_USERNAME = "username";
    static final String KEY_CHECK_IN = "check_in";

    static final String TAG = "myEventListDBAdapter";

    static final String DATABASE_NAME = "ConventioneerDB";
    static final String DATABASE_TABLE = "myEventList";
    static final int DATABASE_VERSION = 1;

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public MyEventListDBAdapter(Context ctx)
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
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }

    //---opens the database---
    public MyEventListDBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---insert a contact into the database---
    public long insertMyEventList(String event_id, String username, String check_in)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_EVENT_ID, event_id);
        initialValues.put(KEY_USERNAME, username);
        initialValues.put(KEY_CHECK_IN, check_in);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular contact---
    public boolean deleteMyEventList(long id)
    {
        return db.delete(DATABASE_TABLE, KEY_ID + "=" + id, null) > 0;
    }

    //---retrieves a particular contact---
    public Boolean getMyEventList(String event, String username) throws SQLException
    {
        Cursor mCursor =
                db.query(true,
                        DATABASE_TABLE,
                        new String[] {KEY_ID, KEY_EVENT_ID, KEY_USERNAME, KEY_CHECK_IN},
                        KEY_EVENT_ID + "=? AND " + KEY_USERNAME + "=?",
                        new String[] { event, username },
                        null, null, null, null, null
                );
        if (mCursor != null) {
            return mCursor.moveToFirst();
        }
        else
            return false;
    }

    public Cursor getMyEventListID(String eventId, String username) throws SQLException
    {
        Cursor mCursor =
                db.query(true,
                        DATABASE_TABLE,
                        new String[] {KEY_ID, KEY_EVENT_ID, KEY_USERNAME, KEY_CHECK_IN},
                        KEY_EVENT_ID + "=? AND " + KEY_USERNAME + "=?",
                        new String[] { eventId, username },
                        null, null, null, null, null
                );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a contact---
    public boolean updateMyEventList(int id, int event_id, String username, String check_in)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_EVENT_ID, event_id);
        args.put(KEY_USERNAME, username);
        args.put(KEY_CHECK_IN, check_in);
        return db.update(DATABASE_TABLE, args, KEY_ID + "=" + id, null) > 0;
    }
}
