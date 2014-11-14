package com.haldane.katherine.conventioneerapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class ConventionInformationActivity extends Activity {
    String website = "";
    String username = "";
    String map = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convention_information);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonbg));

        //Get information from the intent
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");

        // --get convention info---
        ConventionDBAdapter db = new ConventionDBAdapter(this);
        db.open();
        Cursor c = db.getConvention("Anime North");
        if (c.moveToFirst()) {
            do {
                DisplayContact(c);
            } while (c.moveToNext());
        }
        db.close();
    }

    @Override
    public void onResume() {
        //Get information from the intent
        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty())
            username = bundle.getString("username");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.convention_information, menu);
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
                return true;
            case R.id.action_about:
                Intent iAbout = new Intent(this, AboutActivity.class);
                iAbout.putExtra("username", username);
                iAbout.putExtra("false", "true");
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

    public void DisplayContact(Cursor c)
    {
        TextView name = (TextView) findViewById(R.id.conName);
        TextView startDate = (TextView)findViewById(R.id.startDate);
        TextView location = (TextView) findViewById(R.id.location);
        TextView description = (TextView) findViewById(R.id.description);
        name.setText(c.getString(1));
        location.setText(c.getString(2));
        website = c.getString(3);
        description.setText(c.getString(4));
        startDate.setText(c.getString(5));
        map = c.getString(7);

    }

    public void onButtonClick(View view){
        String url = "http://" + website;
        Intent i = new Intent(this, EventListActivity.class);
        Intent iUrl = new Intent(Intent.ACTION_VIEW);
        iUrl.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.putExtra("username",username);
        switch(view.getId()) {
            case R.id.guestButton:
                i.putExtra("eventType","guest");
                startActivity(i);
                break;
            case R.id.panelButton:
                i.putExtra("eventType","panel");
                startActivity(i);
                break;
            case R.id.workshopButton:
                i.putExtra("eventType","workshop");
                startActivity(i);
                break;
            case R.id.mapButton:
                iUrl.setData(Uri.parse(map));
                startActivity(iUrl);
                break;
            case R.id.purchaseButton:
                iUrl.setData(Uri.parse(url));
                startActivity(iUrl);
                break;
        }
    }
}



