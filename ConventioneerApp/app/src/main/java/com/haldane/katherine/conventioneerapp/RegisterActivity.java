package com.haldane.katherine.conventioneerapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends Activity {

    private Pattern pattern;
    private Matcher matcher;
    String username = "";

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public RegisterActivity() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Get information from the intent
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonbg));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent iAbout = new Intent(this, AboutActivity.class);
        iAbout.putExtra("username", "anon");
        iAbout.putExtra("false", "false");
        iAbout.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(iAbout);
        return super.onOptionsItemSelected(item);
    }

    //Create an onclick register button
    public void onButtonClick(View view) {
        EditText etName = (EditText) findViewById(R.id.name);
        EditText etPassword = (EditText) findViewById(R.id.password);
        EditText etEmail = (EditText) findViewById(R.id.email);
        EditText etUsername = (EditText) findViewById(R.id.username);

        //Check that none of the fields are empty
        if(etName.getText().toString().trim().length() != 0 && etUsername.getText().toString().trim().length() != 0 && etEmail.getText().toString().trim().length() != 0 && etPassword.getText().toString().trim().length() != 0)
        {
            //Check that the username isn't already taken
            if(checkUsername(etUsername.getText().toString()))
            {
                //Check that the email doesn't already exist
                if(checkEmail(etEmail.getText().toString())) {
                    //Check that is a valid email
                    matcher = pattern.matcher(etEmail.getText().toString());

                    if(matcher.matches()) {
                        //Call insert onto the database
                        insertAuthentication(etUsername.getText().toString(), etPassword.getText().toString(), etEmail.getText().toString());
                        insertUser(etUsername.getText().toString(),etEmail.getText().toString(), etName.getText().toString());

                        //Redirect to some page
                        Intent intent = new Intent(this, ProfileActivity.class);
                        intent.putExtra("username", etUsername.getText().toString());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Email is NOT valid!!!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //Email already exists
                    Toast.makeText(getApplicationContext(), "Email Already Exists!!!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Username Already Taken!!!", Toast.LENGTH_LONG).show();
            }
        } else {
            //return error that a field is empty
            Toast.makeText(getApplicationContext(), "Empty Fields!!!", Toast.LENGTH_LONG).show();
        }
    }

    //Get contact by username
    public boolean checkUsername(String username) {
        try {
            AuthenticationDBAdapter authenticationDB = new AuthenticationDBAdapter(this);
            authenticationDB.open();
            Cursor c = authenticationDB.getUser(username);
            authenticationDB.close();
        }
        catch(SQLException ex) {
            Toast.makeText(getApplicationContext(), "checkUser is BROKE:" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    //Get contact by email
    public boolean checkEmail(String email) {
        try {
            AuthenticationDBAdapter authenticationDB = new AuthenticationDBAdapter(this);
            authenticationDB.open();
            authenticationDB.getUserByEmail(email);
            authenticationDB.close();
        }
        catch (SQLException ex) {
            Toast.makeText(getApplicationContext(), "Check Email is BROKEN!!!", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    //Insert into database
    public void insertAuthentication(String newUsername, String password, String email) {
        try {
            AuthenticationDBAdapter authenticationDB = new AuthenticationDBAdapter(this);
            authenticationDB.open();
            if (username.equals("anon")) {
                if (!authenticationDB.updateUser(username, newUsername, password, email, 1)){
                    Toast.makeText(getApplicationContext(), "anon to registered user failed", Toast.LENGTH_LONG).show();
                }
            } else {
                long id = authenticationDB.insertUser(newUsername, password, email, 1);
            }
            authenticationDB.close();
        }
        catch(SQLException ex){
            Toast.makeText(getApplicationContext(), "New User Authentication failed!!", Toast.LENGTH_LONG).show();
        }
    }
    public void insertUser(String username, String email, String name) {
        try {
            UserDBAdapter db = new UserDBAdapter(this);
            db.open();
            long id = db.insertUser(username, name, "", "", "", "", email, "empty");
            db.close();
        }
        catch(SQLException ex) {
            Toast.makeText(getApplicationContext(), "New User update failed!!", Toast.LENGTH_LONG).show();
        }
    }

    public void toastUserDetails(Cursor c) {
        Toast.makeText(this,
                "id: " + c.getString(0) + "\n" +
                        "Username: " + c.getString(1) + "\n" +
                        "2:  " + c.getString(2) + "\n" +
                        "3:  " + c.getString(3) + "\n" +
                        "4:  " + c.getString(4) + "\n" +
                        "5:  " + c.getString(5) + "\n",
                Toast.LENGTH_LONG).show();
    }
}


