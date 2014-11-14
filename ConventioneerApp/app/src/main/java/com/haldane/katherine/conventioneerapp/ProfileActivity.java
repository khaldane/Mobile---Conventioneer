package com.haldane.katherine.conventioneerapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ProfileActivity extends Activity {
    ListView lv;
    List<String> myEventListIdArray = new ArrayList<String>(); // List of eventIDs
    String username;
    private String selectedImagePath;
    String profileImgUrl = "";
    private static final int SELECT_PICTURE = 1;
    final String saveIcon = "\uF0C7";
    final String editIcon = "\uF040";
    final String cancelIcon = "\uf1f8";

    final Context c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonbg));

        Typeface fontFamily = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        TextView sampleText = (TextView) findViewById(R.id.editProfile);
        sampleText.setTypeface(fontFamily);
        sampleText.setText(editIcon);

        sampleText = (TextView) findViewById(R.id.editCancel);
        sampleText.setTypeface(fontFamily);
        sampleText.setText(cancelIcon);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");

        loadProfileDetails();

        refreshMyList();

        LoadPreferences();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (((TextView)findViewById(R.id.editProfile)).getText().equals(saveIcon)) {
            Toast.makeText(this,"Profile changes not saved, save or cancel first",Toast.LENGTH_LONG).show();
        } else {
            SavePreferences();
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        SavePreferences();
        super.onPause();
    }

    @Override
    public void onResume() {
        //Get information from the intent
        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty())
            username = bundle.getString("username");
        refreshMyList();
        super.onResume();
    }

    private void loadProfileDetails() {
        UserDBAdapter dbUser = new UserDBAdapter(this);
        try {
            dbUser.open();
            Cursor c = dbUser.getProfile(username);
            dbUser.close();

            EditText et = (EditText) findViewById(R.id.editTextName);
            et.setText(c.getString(0));
            setEditableOff(et);
            et = (EditText) findViewById(R.id.editTextDOB);
            et.setText(c.getString(1));
            setEditableOff(et);
            et = (EditText) findViewById(R.id.editTextGender);
            et.setText(c.getString(2));
            setEditableOff(et);
            et = (EditText) findViewById(R.id.editTextLocation);
            et.setText(c.getString(3));
            setEditableOff(et);
            et = (EditText) findViewById(R.id.editTextInfo);
            et.setText(c.getString(4));
            setEditableOff(et);
            et = (EditText) findViewById(R.id.editTextEmail);
            et.setText(c.getString(5));
            setEditableOff(et);

            if (!c.getString(6).equals("empty")) {
                File imgFile = new File(c.getString(6));
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    //Add image to the image view
                    ImageView myImage = (ImageView) findViewById(R.id.profileImage);
                    myImage.setImageBitmap(myBitmap);

                } else {
                    ImageView myImage = (ImageView) findViewById(R.id.profileImage);
                    myImage.setImageResource(R.drawable.ic_launcher);
                }
            } else {
                ImageView myImage = (ImageView) findViewById(R.id.profileImage);
                myImage.setImageResource(R.drawable.ic_launcher);
            }
        }
        catch(SQLException ex)
        {
            Toast.makeText(getApplicationContext(), "getProfile sqlexception:" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void refreshMyList() {

        EventDBAdapter dbEvent = new EventDBAdapter(this);
        List<Spannable> values = new ArrayList<Spannable>();

        Cursor c = null;
        try {
            dbEvent.open();
            c = dbEvent.getMyEventListEvents(username);
            dbEvent.close();
        }
        catch(SQLException ex)
        {
            Toast.makeText(getApplicationContext(), "getProfile sqlexception:" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        lv = (ListView)findViewById(R.id.eventListView);
        if (c != null && c.getCount()>0) {
            do {

                String title = c.getString(1);
                String moreInfo = "(" + c.getString(7) + ")\n" + c.getString(5);
                if ( c.getString(6).length() < 8 ) {
                    moreInfo += " at " + c.getString(6);
                } else {
                    moreInfo += " from " + c.getString(6);
                }
                int n = title.length();
                int m = moreInfo.length();

                Spannable span = new SpannableString(title + "\n" +  moreInfo);
                //Big font till you find `\n`
                span.setSpan(null, 0, n, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //Small font from `\n` to the end
                span.setSpan(new RelativeSizeSpan(0.8f), n, (n+m+1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                myEventListIdArray.add( c.getString(0) );
                values.add( span );

            } while (c.moveToNext());
        }

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        //if(!values.isEmpty()) {
        ArrayAdapter<Spannable> adapter = new ArrayAdapter<Spannable>(this, R.layout.mytextview, values);
        // Assign adapter to ListView
        lv.setAdapter(adapter);


        // ListView Item Click Listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (((TextView)findViewById(R.id.editProfile)).getText().equals(saveIcon)) {
                    Toast.makeText(view.getContext(),"Save or cancel profile update before continuing.",Toast.LENGTH_LONG).show();
                } else {
                    // ListView Clicked item value
                    Spannable itemValue = (Spannable) lv.getItemAtPosition(position);

                    Intent iEvent = new Intent(view.getContext(), EventActivity.class);
                    iEvent.putExtra("username", username);
                    iEvent.putExtra("eventid", myEventListIdArray.get(position));
                    iEvent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(iEvent);
                }
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (((TextView)findViewById(R.id.editProfile)).getText().equals(saveIcon)) {
            Toast.makeText(this,"Profile changes not saved, save or cancel first",Toast.LENGTH_LONG).show();
        } else {
            switch (item.getItemId()) {
                case R.id.action_profileBottomBar:
                    return true;
                case R.id.action_eventBottomBar:
                    Intent iEventList = new Intent(this, EventListActivity.class);
                    iEventList.putExtra("username", username);
                    iEventList.putExtra("eventType", "all");
                    iEventList.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(iEventList);
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

    public void onClick(View view)
    {
        Button editButton = (Button)findViewById(R.id.editProfile);
        Button cancelButton = (Button)findViewById(R.id.editCancel);
        if(editButton.getText().equals(editIcon) && view.toString().contains("Button")) {

            EditText et = (EditText)findViewById(R.id.editTextName);
            setEditableOn(et);
            et = (EditText)findViewById(R.id.editTextDOB);
            setEditableOn(et);
            et = (EditText)findViewById(R.id.editTextGender);
            setEditableOn(et);
            et = (EditText)findViewById(R.id.editTextLocation);
            setEditableOn(et);
            et = (EditText)findViewById(R.id.editTextInfo);
            setEditableOn(et);
            et = (EditText)findViewById(R.id.editTextEmail);
            setEditableOn(et);
            cancelButton.setVisibility(View.VISIBLE);
            editButton.setText(saveIcon);
        }
        else if(editButton.getText().equals(saveIcon)){
            if(view.toString().contains("ImageView") && editButton.getText().equals(saveIcon))
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
            else {
                EditText et = (EditText) findViewById(R.id.editTextName);
                String name = et.getText().toString();
                setEditableOff(et);
                et = (EditText) findViewById(R.id.editTextDOB);
                String dob = et.getText().toString();
                setEditableOff(et);
                et = (EditText) findViewById(R.id.editTextGender);
                String gender = et.getText().toString();
                setEditableOff(et);
                et = (EditText) findViewById(R.id.editTextLocation);
                String location = et.getText().toString();
                setEditableOff(et);
                et = (EditText) findViewById(R.id.editTextInfo);
                String about = et.getText().toString();
                setEditableOff(et);
                et = (EditText) findViewById(R.id.editTextEmail);
                String email = et.getText().toString();
                setEditableOff(et);

                //Save to database
                try {
                    UserDBAdapter db = new UserDBAdapter(this);
                    //Get the userID
                    db.open();
                    boolean id = db.updateProfile(username, name, dob, gender, location, about, email, profileImgUrl);
                    db.close();

                } catch (SQLException ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                cancelButton.setVisibility(View.INVISIBLE);
                editButton.setText(editIcon);
            }
        }
    }

    public void onClickCancel(View view) {

        Button editButton = (Button)findViewById(R.id.editProfile);
        Button cancelButton = (Button)findViewById(R.id.editCancel);

        loadProfileDetails();

        cancelButton.setVisibility(View.INVISIBLE);
        editButton.setText(editIcon);
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            //save image and change imageview
            File imgFile = new  File(cursor.getString(column_index));
            profileImgUrl = cursor.getString(column_index);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                //Add image to the image view
                ImageView myImage = (ImageView) findViewById(R.id.profileImage);
                myImage.setImageBitmap(myBitmap);
            }
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    public void setEditableOn(EditText et){
        et.setLongClickable(true);
        et.setClickable(true);
        et.setCursorVisible(true);
        et.setFocusable(true);
        et.setClickable(true);
        et.setEnabled(true);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        et.setBackgroundColor(Color.parseColor("#0CA5B0"));

        findViewById(R.id.labelSection).setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.18f));
        findViewById(R.id.editSection).setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.52f));
    }
    public void setEditableOff(EditText et){
        et.setLongClickable(false);
        et.setClickable(false);
        et.setCursorVisible(false);
        et.setTextColor(Color.parseColor("#000000"));
        et.setFocusable(false);
        et.setClickable(false);
        et.setEnabled(false);
        et.setFocusable(false);
        et.setFocusableInTouchMode(false);
        et.setBackgroundColor(Color.parseColor("#FEFEEB"));
        et.requestFocus();

        findViewById(R.id.labelSection).setLayoutParams(new LinearLayout.LayoutParams(0, 0, 0f));
        findViewById(R.id.editSection).setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.7f));
    }

    private void SavePreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        TextView tv = (TextView)findViewById(R.id.editProfile);

        editor.putBoolean("state", tv.getText().equals(saveIcon));

        EditText et = (EditText)findViewById(R.id.editTextName);
        editor.putString("name", et.getText().toString());

        et = (EditText)findViewById(R.id.editTextDOB);
        editor.putString("dob", et.getText().toString());

        et = (EditText)findViewById(R.id.editTextGender);
        editor.putString("gender", et.getText().toString());

        et = (EditText)findViewById(R.id.editTextLocation);
        editor.putString("location", et.getText().toString());

        et = (EditText)findViewById(R.id.editTextInfo);
        editor.putString("info", et.getText().toString());

        et = (EditText)findViewById(R.id.editTextEmail);
        editor.putString("email", et.getText().toString());

        editor.putString("imgurl", profileImgUrl);

        editor.commit();   // I missed to save the data to preference here,.
    }

    private void LoadPreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        Boolean state = sharedPreferences.getBoolean("state", false);
        TextView tv = (TextView)findViewById(R.id.editProfile);

        if (state) {
            EditText et = (EditText)findViewById(R.id.editTextName);
            et.setText(sharedPreferences.getString("name", ""));
            setEditableOn(et);
            et = (EditText)findViewById(R.id.editTextDOB);
            et.setText(sharedPreferences.getString("dob", ""));
            setEditableOn(et);
            et = (EditText)findViewById(R.id.editTextGender);
            et.setText(sharedPreferences.getString("gender", ""));
            setEditableOn(et);
            et = (EditText)findViewById(R.id.editTextLocation);
            et.setText(sharedPreferences.getString("location", ""));
            setEditableOn(et);
            et = (EditText)findViewById(R.id.editTextInfo);
            et.setText(sharedPreferences.getString("info", ""));
            setEditableOn(et);
            et = (EditText)findViewById(R.id.editTextEmail);
            et.setText(sharedPreferences.getString("email", ""));
            setEditableOn(et);

            File imgFile = new  File(sharedPreferences.getString("imgurl", ""));
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                //Add image to the image view
                ImageView myImage = (ImageView) findViewById(R.id.profileImage);
                myImage.setImageBitmap(myBitmap);
            }

            tv.setText(saveIcon);

            Button cancelButton = (Button)findViewById(R.id.editCancel);
            cancelButton.setVisibility(View.VISIBLE);
        }
    }
}



