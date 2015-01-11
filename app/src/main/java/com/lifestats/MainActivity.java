/**
 * LifeStats
 * Copyright (C) 2014  Yi Zhang
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License,
 * with the "Linking Exception", which can be found at the license.txt
 * file in this program.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * with the "Linking Exception" along with this program; if not,
 * write to the author Yi Zhang <yi.zhang7210@gmail.com>.
 *
 * Acknowledgement:
 * This app used the GraphView library from: http://www.jjoe64.com/p/graphview-library.html,
 * and examples from the official Android documentation: http://developer.android.com
 * and http://stackoverflow.com.
 */

package com.lifestats;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * MainActivity: Main user interface.
 */
public class MainActivity extends FragmentActivity {

    /**
     * Initialize Tab structure: Need a ViewPager and a PagerAdapter.
     */
    private static final int NUM_PAGES = 2;
    private ViewPager mPager;

    /**
     * onCreate: Setup Tab structure and Action Bar.
     *
     * @param savedInstanceState saved instance state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        /**
         * Routine create view.
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /**
         * Setup the ViewPager and its Adapter.
         */
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(0);

        /**
         * Change selected ActionBar title when swipe left or right.
         */
        mPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onPageSelected(int position) {
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });

        /**
         * Setup ActionBar displaying the Tab titles.
         */
        final ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        /**
         * Setup TabListener to display the correct tab content.
         */
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }
        };

        /**
         * Add the actual Tabs to the page.
         * 1. Record activity tab.
         * 2. Show activity tab.
         */
        ActionBar.Tab recordTab = actionBar.newTab();
        recordTab.setText(R.string.recordActivities);
        recordTab.setTabListener(tabListener);
        actionBar.addTab(recordTab);

        ActionBar.Tab showTab = actionBar.newTab();
        showTab.setText(R.string.showActivities);
        showTab.setTabListener(tabListener);
        actionBar.addTab(showTab);

    }

    /**
     * onBackPressed: Moved back instead of quit.
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     * Define the PagerAdapter.
     */
    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

        /**
         * Default constructor.
         *
         * @param fm The FragmentManager.
         */
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Implement the abstract method: Get the corresponding Fragment.
         *
         * @param position, i.e. the page number of the view.
         * @return The Fragment corresponding to the position.
         */
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new RecordTab();
                case 1:
                    return new ShowTab();
                default:
                    throw new RuntimeException(getString(R.string.tabOutOfRange));
            }
        }

        /**
         * Implement the abstract method: Get the total number of pages.
         *
         * @return total number of pages.
         */
        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        /**
         * Override to make sure new pages are created if queried.
         *
         * @param obj The item in question.
         * @return POSITION_NONE because want new item to be created.
         */
        @Override
        public int getItemPosition(Object obj) {
            return POSITION_NONE;
        }

    }
}
