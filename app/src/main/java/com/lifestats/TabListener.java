package com.lifestats;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.util.Log;


/**
 * Created by yzhang on 25/12/14.
 */

public class TabListener implements ActionBar.TabListener {
    Fragment fragment;

    public TabListener(Fragment fragment) {
        this.fragment = fragment;
    }

    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

        ft.replace(R.id.fragment_container, fragment);
        Log.e("tab","on selected");
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.e("tab", "reselected");
    }
}
