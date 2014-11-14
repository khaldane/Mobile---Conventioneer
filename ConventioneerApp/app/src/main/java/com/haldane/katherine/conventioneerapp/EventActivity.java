package com.haldane.katherine.conventioneerapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class EventActivity extends Activity {

    String website = "";
    String eventId = "";
    String username = "";
    String typeForButton = "";
    String imgflag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        //Get information from the intent
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        eventId = bundle.getString("eventid");
        // --get convention info---
        EventDBAdapter db = new EventDBAdapter(this);

        db.open();
        Cursor c = db.getEvent(eventId);
        if (c.moveToFirst()) {
            DisplayContact(c);
        }
        db.close();

        //Check for Wishlist Event Name / Username if checked and unchecked
        checkCheckbox();
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonbg));
        Button purchaseTicket = (Button) findViewById(R.id.purchaseButton);
        ImageView getImage = (ImageView) findViewById(R.id.eventImage);

        CheckBox myEventList = (CheckBox) findViewById(R.id.myEventListCheckbox) ;
        CheckBox checkIn = (CheckBox) findViewById(R.id.checkinCheckbox) ;
        TextView addToMyEventList = (TextView) findViewById(R.id.addToMyEventList);
        TextView checkin = (TextView) findViewById(R.id.checkIn);

        int resID = getResources().getIdentifier(imgflag, "drawable", getPackageName());

        getImage.setImageResource(resID);

        if(username.equals("anon"))
        {
            myEventList.setEnabled(false);
            addToMyEventList.setTextColor(Color.parseColor("#e0e0e0"));
            checkin.setTextColor(Color.parseColor("#e0e0e0"));
            checkIn.setEnabled(false);
        }

        if(typeForButton.equals("Workshop"))
        {
            //android:layout_height="218dp"
            purchaseTicket.setVisibility(View.VISIBLE);
        }
        else purchaseTicket.setVisibility(View.INVISIBLE);
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
        getMenuInflater().inflate(R.menu.event, menu);
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

    public void DisplayContact(Cursor c) {
        TextView name = (TextView) findViewById(R.id.name);
        TextView date = (TextView) findViewById(R.id.date);
        TextView time = (TextView) findViewById(R.id.time);
        TextView location = (TextView) findViewById(R.id.location);
        TextView type = (TextView) findViewById(R.id.type);
        TextView description = (TextView) findViewById(R.id.description);
        eventId = c.getString(0);
        name.setText(c.getString(1));
        location.setText(c.getString(2));
        website = c.getString(3);
        description.setText(c.getString(4));
        date.setText(c.getString(5));
        time.setText(c.getString(6));
        type.setText(toTitleCase(c.getString(7)) + ": ");
        typeForButton = toTitleCase(c.getString(7).toString());
        imgflag = c.getString(8);
    }

    public void onButtonClick(View view) {
        CheckBox myEventlistCB = (CheckBox)findViewById(R.id.myEventListCheckbox);
        CheckBox checkinCB = (CheckBox) findViewById(R.id.checkinCheckbox);
        switch(view.getId()) {
            case R.id.purchaseButton:
                String url = "http://"+website;
                Intent iUrl = new Intent(Intent.ACTION_VIEW);
                iUrl.setData(Uri.parse(url));
                startActivity(iUrl);
                break;
            case R.id.myEventListCheckbox:
                if(myEventlistCB.isChecked())
                {
                    //if checked add to database
                    updateMyEventlist("0");
                }
                else {
                    removeMyEventList();
                    checkinCB.setChecked(false);
                }
                break;
            case R.id.checkinCheckbox:
                if(checkinCB.isChecked())
                {
                    updateMyEventlist("1");
                    myEventlistCB.setChecked(true);
                }
                else {
                    removeCheckin();
                }
                break;
        }
    }

    public void updateMyEventlist(String checked)
    {
        MyEventListDBAdapter db = new MyEventListDBAdapter(this);
        //check if it already exists in the database if so update

        db.open();
        Boolean c = db.getMyEventList(eventId, username);
        db.close();

        if(c){
            db.open();
            Cursor melID = db.getMyEventListID(eventId, username);
            db.close();
            //if it exists update the database
            db.open();
            boolean id = db.updateMyEventList(Integer.parseInt(melID.getString(0)),Integer.parseInt(melID.getString(1)),melID.getString(2), checked);
            db.close();
        }
        else {
            //otherwise add it to the database
            try {
                db.open();
                long d = db.insertMyEventList(eventId, username, checked);
                db.close();
            }
            catch(SQLException ex)
            {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void removeMyEventList()
    {
        MyEventListDBAdapter db = new MyEventListDBAdapter(this);
        //Remove from wishlist
        db.open();
        Cursor melID = db.getMyEventListID(eventId, username);
        db.close();
        //if it exists update the database
        db.open();
        boolean id = db.deleteMyEventList(Integer.parseInt(melID.getString(0)));
        db.close();
    }

    public void removeCheckin() {
        MyEventListDBAdapter db = new MyEventListDBAdapter(this);
        //Remove from wishlist
        db.open();
        Cursor melID = db.getMyEventListID(eventId, username);
        db.close();
        //if it exists update the database
        db.open();
        boolean id = db.updateMyEventList(Integer.parseInt(melID.getString(0)),Integer.parseInt(melID.getString(1)),melID.getString(2), "0");
        db.close();
    }

    public void checkCheckbox(){
        CheckBox myEventListCB = (CheckBox)findViewById(R.id.myEventListCheckbox);
        CheckBox checkinCB = (CheckBox) findViewById(R.id.checkinCheckbox);
        MyEventListDBAdapter db = new MyEventListDBAdapter(this);
        db.open();
        Cursor c = db.getMyEventListID(eventId, username);
        db.close();

        if(c.moveToFirst()) {
            myEventListCB.setChecked(true);//just check wishlist
            if (c.getString(3).equals("1")) {
                checkinCB.setChecked(true);
            }
        }
    }

    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }
        return titleCase.toString();
    }

    public void toastEventDetails(Cursor c)
    {
        Toast.makeText(this,
                "id: " + c.getString(0) + "\n" +
                        "Name: " + c.getString(1) + "\n" +
                        "Location:  " + c.getString(2) + "\n" +
                        "URL:  " + c.getString(3) + "\n" +
                        "Desc: " + c.getString(4) + "\n" +
                        "Date: " + c.getString(5) + "\n" +
                        "Time:  " + c.getString(6) + "\n" +
                        "Type:  " + c.getString(7) + "\n" +
                        "Flag:  " + c.getString(8),
                Toast.LENGTH_LONG).show();
    }
}