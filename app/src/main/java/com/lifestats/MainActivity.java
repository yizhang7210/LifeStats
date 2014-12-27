package com.lifestats;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity
{
    ActionBar.Tab tab1, tab2;
    android.app.Fragment fragmentTab1 = new RecordActivityTab();
    android.app.Fragment fragmentTab2 = new ShowActivityTab();



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
