package com.haldane.katherine.conventioneerapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class AppLoadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_load);
        AuthenticationDBAdapter authenticationDB = new AuthenticationDBAdapter(this);

        try {
            String destPath = "/data/data/" + getPackageName() +
                    "/databases";
            File f = new File(destPath);
            if (!f.exists()) {
                f.mkdirs();
                f.createNewFile();

                //---copy the db from the assets folder into
                // the databases folder---
                CopyDB(getBaseContext().getAssets().open("ConventioneerDB"),
                        new FileOutputStream(destPath + "/ConventioneerDB"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // display Authentication table for debugging
        //DisplayAuthenticationTable();

        authenticationDB.open();
        Cursor c = authenticationDB.getAllUsers();
        authenticationDB.close();

        // find the active user and take them to their start screen
        if ( c.moveToFirst() && c.getCount() > 0) {
            do {
                // if this is the active user
                if (c.getString(4).equals("1")) {
                    Intent i;
                    if (c.getString(1).equals("anon")) {
                        i = new Intent(this, ConventionInformationActivity.class);
                    } else {
                        i = new Intent(this, ProfileActivity.class);
                    }
                    i.putExtra("username", c.getString(1));
                    startActivity(i);
                }
            } while (c.moveToNext());
        } else {
            // if there was no active user, ask if they want to continue anonymously, or register
            Intent i = new Intent(this, TabHostActivity.class);

            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_load, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void CopyDB(InputStream inputStream,
                       OutputStream outputStream) throws IOException {
        //---copy 1K bytes at a time---
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }

    public void DisplayAuthenticationTable()
    {
        AuthenticationDBAdapter authenticationDB = new AuthenticationDBAdapter(this);

        authenticationDB.open();
        Cursor c = authenticationDB.getAllUsers();
        if (c.moveToFirst()) {
            do {
                //KEY_ID, KEY_USERNAME, KEY_PASSWORD, KEY_EMAIL, KEY_ACTIVE
                Toast.makeText(this,
                        "id: " + c.getString(0) + "\n" +
                                "Username: " + c.getString(1) + "\n" +
                                "Password:  " + c.getString(2) + "\n" +
                                "Email:  " + c.getString(3) + "\n" +
                                "ActiveFlag: " + c.getString(4),
                        Toast.LENGTH_LONG).show();
            } while(c.moveToNext());
        }
    }
}
