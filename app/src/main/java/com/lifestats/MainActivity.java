package com.lifestats;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * Main user interface.
 */
public class MainActivity extends FragmentActivity {

    /**
     * Initialize Tab structure: need a ViewPager and a PagerAdapter.
     */
    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    /**
     * onCreate: Setup Tab structure and Action Bar.
     * @param savedInstanceState saved instance state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState){

        /**
         * Routine create view.
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /**
         * Setup the ViewPager and its Adapter.
         */
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        /**
         * Change selected Action Bar title when swipe left or right.
         */
        mPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });

        /**
         * Setup Action Bar displaying the Tab titles.
         */
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        /**
         * Setup TabListener to display the right tab content.
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

        // Add 3 tabs, specifying the tab's text and TabListener
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
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     * Define the PagerAdapter.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Implement the abstract method: get the corresponding fragment.
         * @param position, i.e. the page number of the view.
         * @return The right fragment corresponding to the position.
         */
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new RecordTab();
                case 1:
                    return new ShowTab();
                default:
                    throw new RuntimeException("Tab position out of range.");
            }
        }

        /**
         * Implement the abstract method: get the total number of pages.
         * @return total number of pages.
         */
        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
