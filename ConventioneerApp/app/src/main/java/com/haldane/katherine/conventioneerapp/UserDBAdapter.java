package com.haldane.katherine.conventioneerapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDBAdapter {
    static final String KEY_ID = "user_id";
    static final String KEY_USERNAME = "username";
    static final String KEY_NAME = "name";
    static final String KEY_DOB = "dob";
    static final String KEY_GENDER = "gender";
    static final String KEY_LOCATION = "location";
    static final String KEY_EMAIL = "email";
    static final String KEY_ABOUT = "about";
    static final String KEY_PROFILE_IMG = "profileImg";

    static final String TAG = "UserDBAdapter";

    static final String DATABASE_NAME = "ConventioneerDB";
    static final String DATABASE_TABLE = "user";
    static final int DATABASE_VERSION = 1;

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public UserDBAdapter(Context ctx)
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
    public UserDBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close() {
        DBHelper.close();
    }

    //---insert a contact into the database---
    public long insertUser(String username, String name, String dob, String gender, String location, String about, String email, String profileImg)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, username);
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_DOB, dob);
        initialValues.put(KEY_GENDER, gender);
        initialValues.put(KEY_LOCATION, location);
        initialValues.put(KEY_ABOUT, about);
        initialValues.put(KEY_EMAIL, email);
        initialValues.put(KEY_PROFILE_IMG, profileImg);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular contact---
    public boolean deleteUser(long id)
    {
        return db.delete(DATABASE_TABLE, KEY_ID + "=" + id, null) > 0;
    }

    //---retrieves all the contacts---
    public Cursor getAllProfiles()
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_USERNAME, KEY_NAME,
                KEY_DOB, KEY_GENDER, KEY_LOCATION, KEY_ABOUT, KEY_EMAIL, KEY_PROFILE_IMG}, null, null, null, null, null, null);
    }

    //---retrieves a particular contact---
    public Cursor getProfile(String username) throws SQLException
    {
        Cursor mCursor = db.query(
                true,
                DATABASE_TABLE,
                new String[] {KEY_NAME, KEY_DOB, KEY_GENDER, KEY_LOCATION, KEY_ABOUT, KEY_EMAIL, KEY_PROFILE_IMG},
                KEY_USERNAME + "=?",
                new String[] { username },
                null, null, null, null
        );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a contact---
    public boolean updateProfile(String username, String name, String dob, String gender, String location, String about, String email, String profileImg)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_USERNAME, username);
        args.put(KEY_NAME, name);
        args.put(KEY_DOB, dob);
        args.put(KEY_GENDER, gender);
        args.put(KEY_LOCATION, location);
        args.put(KEY_ABOUT, about);
        args.put(KEY_EMAIL, email);
        args.put(KEY_PROFILE_IMG, profileImg);
        return db.update(DATABASE_TABLE, args, KEY_USERNAME + "=?", new String[] {username}) > 0;
    }

}

