package com.haldane.katherine.conventioneerapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class EventListActivity extends Activity {

    String username = "";
    String eventType = "";
    ListView lv;
    List<String> eventListIdArray = new ArrayList<String>(); // List of eventIDs

    private Spinner spinnerTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        Typeface fontFamily = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        TextView arrow = (TextView) findViewById(R.id.arrow);
        arrow.setTypeface(fontFamily);
        arrow.setText("\uf063");

        //Get information from the intent
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        eventType = bundle.getString("eventType");
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonbg));

        Button sampleText = (Button) findViewById(R.id.buttonSearch);
        sampleText.setTypeface(fontFamily);
        sampleText.setText("\uf002");

        // --get events info---
        updateSpinner();

        // populate event list with
        updateEventList(eventType);
    }

    @Override
    public void onResume() {
        //Get information from the intent
        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty())
            username = bundle.getString("username");
        super.onResume();
    }

    private void updateSpinner() {
        EventDBAdapter dbEvent = new EventDBAdapter(this);
        int pos = 0;
        spinnerTypes = (Spinner)findViewById(R.id.spinnerEventType);

        List<String> list = new ArrayList<String>();
        list.add("All");
        list.add("My List");
        try {
            dbEvent.open();
            Cursor c = dbEvent.getEventTypes();
            if (c != null && c.getCount() > 0) {
                int posCounter = 1;
                do {
                    list.add(toTitleCase(c.getString(0)));
                    ++posCounter;
                    if (c.getString(0).equalsIgnoreCase(eventType)) {
                        pos = posCounter;
                    }
                } while (c.moveToNext());
            }

            dbEvent.close();
        } catch (SQLException ex) {
            Toast.makeText(getApplicationContext(), "getProfile sqlexception:" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, list);

        dataAdapter.setDropDownViewResource(R.layout.spinner);

        spinnerTypes.setAdapter(dataAdapter);
        spinnerTypes.setSelection(pos);

        // Spinner item selection Listener
        addListenerOnSpinnerItemSelection();
    }

    private void updateEventList(String eventType) {
        int counter = 0;
        String searchString = "";
        List<Spannable> values = new ArrayList<Spannable>();
        EventDBAdapter dbEvent = new EventDBAdapter(this);
        eventListIdArray.clear();
        try {
            dbEvent.open();
            Cursor c;
            if (eventType.equals("all")) {
                c = dbEvent.getAllEvents();
            } else if (eventType.equals("my list")) {
                c = dbEvent.getMyEventListEvents(username);
            } else {
                c = dbEvent.getEventsByType(eventType);
            }
            lv = (ListView) findViewById(R.id.eventListView);
            if (c != null && c.getCount() > 0) {
                searchString = ((EditText) findViewById(R.id.searchField)).getText().toString();
                Pattern pattern = Pattern.compile(searchString, Pattern.CASE_INSENSITIVE);

                do {
                    Matcher matcher = pattern.matcher(c.getString(1));
                    Matcher matcherInfo = pattern.matcher(c.getString(4));
                    if (matcher.find() || matcherInfo.find()) {
                        String title = c.getString(1);
                        String moreInfo = "(" + c.getString(7) + ")\n" + c.getString(2) + "\n" + c.getString(5);
                        if (c.getString(6).length() < 8) {
                            moreInfo += " at " + c.getString(6);
                        } else {
                            moreInfo += " from " + c.getString(6);
                        }

                        int n = title.length();
                        int m = moreInfo.length();
                        Spannable span = new SpannableString(title + "\n" + moreInfo);
                        //Big font till you find `\n`
                        //Small font from `\n` to the end
                        span.setSpan(new RelativeSizeSpan(0.8f), n, (n + m + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        eventListIdArray.add(c.getString(0));
                        values.add(span);
                        ++counter;
                    }
                } while (c.moveToNext());
            }
            dbEvent.close();
            TextView results = (TextView) findViewById(R.id.displayResults);
            if (searchString.length() > 0) {
                results.setText(counter + " results found for '" + searchString + "' in " + eventType);
            } else {
                results.setText(counter + " results found in '" + eventType + "'");
            }
        } catch (SQLException ex) {
            Toast.makeText(getApplicationContext(), "Get Events List sqlexception:" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        ArrayAdapter<Spannable> adapter = new ArrayAdapter<Spannable>(this, R.layout.mytextview, values);
        // Assign adapter to ListView
        lv.setAdapter(adapter);


        // ListView Item Click Listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                Intent iEvent = new Intent(view.getContext(), EventActivity.class);
                iEvent.putExtra("username", username);
                iEvent.putExtra("eventid", eventListIdArray.get(position));
                iEvent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(iEvent);
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profileBottomBar:
                Intent iProfile = new Intent(this, ProfileActivity.class);
                iProfile.putExtra("username", username);
                iProfile.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(iProfile);
                return true;
            case R.id.action_eventBottomBar:
                return true;
            case R.id.action_conventionBottomBar:
                Intent iConInfo = new Intent(this, ConventionInformationActivity.class);
                iConInfo.putExtra("username", username);
                iConInfo.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
        if (username.equals("anon")) {
            MenuItem findProfile = menu.findItem(R.id.action_profileBottomBar);
            findProfile.setVisible(false);
            return true;
        } else {
            MenuItem findRegister = menu.findItem(R.id.action_registerBottomBar);
            findRegister.setVisible(false);
            return true;
        }
    }

    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSearch:
                updateEventList(eventType);
                break;
        }
    }

    // Add spinner data
    public void addListenerOnSpinnerItemSelection() {

        spinnerTypes.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            eventType = parent.getItemAtPosition(pos).toString().toLowerCase();
            updateEventList(eventType);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
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

}


