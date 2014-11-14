package com.haldane.katherine.conventioneerapp;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;


public class TabHostActivity extends TabActivity implements TabHost.OnTabChangeListener {
    TabHost mTabHost;
    int selectedTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_host);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonbg));
        mTabHost=getTabHost();

        TabHost.TabSpec p = mTabHost.newTabSpec("");
        // setting Title and Icon for the Tab
        p.setIndicator("");
        Intent homeIntent = new Intent(this, NewUserActivity.class);
        p.setContent(homeIntent);

        TabHost.TabSpec t = mTabHost.newTabSpec("Facebook");
        // setting Title and Icon for the Tab
        t.setIndicator("Facebook");
        Intent tatvaIntent = new Intent(this, NewUserActivity.class);
        t.setContent(tatvaIntent);

        TabHost.TabSpec m = mTabHost.newTabSpec("Twitter");
        m.setIndicator("Twitter");
        Intent iAnonymous = new Intent(this, NewUserActivity.class);
        m.setContent(iAnonymous);

        TabHost.TabSpec u = mTabHost.newTabSpec("Instagram");
        u.setIndicator("Instagram");
        Intent updatesIntent = new Intent(this, NewUserActivity.class);
        u.setContent(updatesIntent);

        mTabHost.addTab(p);
        mTabHost.addTab(t);
        mTabHost.addTab(m);
        mTabHost.addTab(u);

        mTabHost.setOnTabChangedListener(this);
        TabHost  tabHost = (TabHost)findViewById(android.R.id.tabhost);
        tabHost.getTabWidget().getChildAt(0).setVisibility(View.GONE);

        for (int tabIndex = 0 ; tabIndex < mTabHost.getTabWidget().getTabCount() ; tabIndex ++) {
            View tab = mTabHost.getTabWidget().getChildTabViewAt(tabIndex);
            TextView d = (TextView)tab.findViewById(android.R.id.title);
            d.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        selectedTab = mTabHost.getCurrentTab();
        if(tabId.equals("Facebook"))
        {
            Intent iUrl = new Intent(Intent.ACTION_VIEW);
            iUrl.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            iUrl.setData(Uri.parse("http://www.Facebook.com"));
            startActivity(iUrl);
        }
        else if(tabId.equals("Twitter"))
        {
            Intent iUrl = new Intent(Intent.ACTION_VIEW);
            iUrl.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            iUrl.setData(Uri.parse("http://www.Twitter.com"));
            startActivity(iUrl);
        }
        else if(tabId.equals("Instagram"))
        {
            Intent iUrl = new Intent(Intent.ACTION_VIEW);
            iUrl.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            iUrl.setData(Uri.parse("http://www.Instagram.com"));
            startActivity(iUrl);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tab_host, menu);
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
}


