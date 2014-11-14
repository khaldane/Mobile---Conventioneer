package com.haldane.katherine.conventioneerapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class AboutActivity extends Activity {

    String username = "";
    String showButtons= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonbg));

        //Get information from the intent
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        showButtons = bundle.getString("false");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_profileBottomBar:
                Intent iProfile = new Intent(this, ProfileActivity.class);
                iProfile.putExtra("username",username);
                iProfile.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(iProfile);
                return true;
            case R.id.action_eventBottomBar:
                Intent iEventList = new Intent(this, EventListActivity.class);
                iEventList.putExtra("username", username);
                iEventList.putExtra("eventType","all");
                iEventList.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(iEventList);
                return true;
            case R.id.action_conventionBottomBar:
                Intent iConInfo = new Intent(this, ConventionInformationActivity.class);
                iConInfo.putExtra("username", username);
                iConInfo.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(iConInfo);
                return true;
            case R.id.action_about:
                Intent iAbout = new Intent(this, AboutActivity.class);
                iAbout.putExtra("username", username);
                iAbout.putExtra("eventType","all");
                iAbout.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(iAbout);
                return true;
            case R.id.action_registerBottomBar:
                Intent iRegister = new Intent(this, RegisterActivity.class);
                iRegister.putExtra("username", username);
                iRegister.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(iRegister);
                return true;
        }
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(username.equals("anon")) {
            if(showButtons.equals("false")){
                MenuItem findCon = menu.findItem(R.id.action_conventionBottomBar);
                findCon.setVisible(false);
                MenuItem findEvent = menu.findItem(R.id.action_eventBottomBar);
                findEvent.setVisible(false);
            }
            MenuItem findProfile = menu.findItem(R.id.action_profileBottomBar);
            findProfile.setVisible(false);
            return true;
        }
        else {
            MenuItem findRegister = menu.findItem(R.id.action_registerBottomBar);
            findRegister.setVisible(false);
            return true;
        }
    }
}
