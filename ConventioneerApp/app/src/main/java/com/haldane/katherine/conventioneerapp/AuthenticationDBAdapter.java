package com.haldane.katherine.conventioneerapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AuthenticationDBAdapter {
    static final String KEY_ID = "authentication_id";
    static final String KEY_USERNAME = "username";
    static final String KEY_PASSWORD = "password";
    static final String KEY_EMAIL = "email";
    static final String KEY_ACTIVE = "active";
    static final String TAG = "authenticationDBAdapter";

    static final String DATABASE_NAME = "ConventioneerDB";
    static final String DATABASE_TABLE = "authentication";
    static final int DATABASE_VERSION = 1;

    static final String DATABASE_CREATE =
            "create table authentication (authentication_id integer primary key autoincrement, "
                    + "username text not null, password text not null, email text not null, active integer not null);";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public AuthenticationDBAdapter(Context ctx)
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
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
    public AuthenticationDBAdapter open() throws SQLException
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
    public long insertUser(String username, String password, String email, int active)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, username);
        initialValues.put(KEY_PASSWORD, password);
        initialValues.put(KEY_EMAIL, email);
        initialValues.put(KEY_ACTIVE, active);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular contact---
    public boolean deleteUser(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ID + "=" + rowId, null) > 0;
    }

    //---retrieves all the contacts---
    public Cursor getAllUsers()
    {
        Cursor mCursor = db.query(
                DATABASE_TABLE,
                new String[] {KEY_ID, KEY_USERNAME, KEY_PASSWORD, KEY_EMAIL, KEY_ACTIVE},
                null, null, null, null, null
        );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---retrieves a particular contact---
    public Cursor getUser(String username) throws SQLException
    {
        Cursor mCursor =
                db.query(
                        true,
                        DATABASE_TABLE,
                        new String[] {KEY_ID, KEY_USERNAME, KEY_PASSWORD, KEY_EMAIL, KEY_ACTIVE},
                        KEY_USERNAME + "=?",
                        new String[] { username },
                        null, null, null, null
                );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getUserByEmail(String email) throws SQLException
    {
        Cursor mCursor =
                db.query(true,
                        DATABASE_TABLE,
                        new String[] {KEY_ID, KEY_USERNAME, KEY_PASSWORD, KEY_EMAIL, KEY_ACTIVE},
                        KEY_EMAIL + "=?",
                        new String[] { email },
                        null, null, null, null
                );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---retrieves a particular contact---
    public Cursor getUser(String username, String password) throws SQLException
    {
        Cursor mCursor =
                db.query(
                        true,
                        DATABASE_TABLE,
                        new String[] {KEY_ID, KEY_USERNAME, KEY_PASSWORD, KEY_EMAIL, KEY_ACTIVE},
                        KEY_USERNAME + "=? AND " + KEY_PASSWORD + "=?",
                        new String[] { username, password },
                        null, null, null, null
                );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    //---updates a contact---
    public boolean updateUser(String usernameOld, String username, String password, String email, int active) throws SQLException
    {
        ContentValues args = new ContentValues();
        args.put(KEY_USERNAME, username);
        args.put(KEY_PASSWORD, password);
        args.put(KEY_EMAIL, email);
        args.put(KEY_ACTIVE, active);

        return db.update(DATABASE_TABLE, args, KEY_USERNAME + "=?", new String[] { usernameOld} ) > 0;
    }

}


