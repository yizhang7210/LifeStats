package com.lifestats;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public class MainActivity extends Activity
{

    ActionBar.Tab tab1, tab2, tab3;
    Fragment fragmentTab1 = new RecordActivityTab();
    Fragment fragmentTab2 = new ShowActivityTab();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        tab1 = actionBar.newTab().setText("Record Activities");
        tab2 = actionBar.newTab().setText("Show Activities");

        tab1.setTabListener(new TabListener(fragmentTab1));
        tab2.setTabListener(new TabListener(fragmentTab2));

        actionBar.addTab(tab1);
        actionBar.addTab(tab2);

    }
}
