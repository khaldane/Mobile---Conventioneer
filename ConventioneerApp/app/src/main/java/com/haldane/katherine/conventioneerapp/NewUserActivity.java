package com.haldane.katherine.conventioneerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class NewUserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        String s1 = "Create an Account";
        String s2 = "this is required to create an event list";
        int n = s1.length();
        int m = s2.length();

        Spannable span = new SpannableString(s1 + "\n" +  s2);
        //Big font till you find `\n`
        span.setSpan(null, 0, n, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //Small font from `\n` to the end
        span.setSpan(new RelativeSizeSpan(0.6f), n, (n+m+1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Button signup = (Button) findViewById(R.id.signupButton);
        signup.setText(span);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_user, menu);
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

    public void onButtonClick(View view)
    {
        switch(view.getId()) {
            case R.id.signupButton:
                Intent iSignup = new Intent(this, RegisterActivity.class);
                iSignup.putExtra("username","");
                startActivity(iSignup);
                break;
            case R.id.anonButton:
                AuthenticationDBAdapter db = new AuthenticationDBAdapter(this);
                //add user to the database
                db.open();
                long id = db.insertUser("anon", "", "", 1);
                db.close();
                Intent iAnonymous = new Intent(this, ConventionInformationActivity.class);
                iAnonymous.putExtra("username","anon");
                startActivity(iAnonymous);
                break;
        }
    }
}


