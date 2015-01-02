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
 * .Created by yzhang on 27/12/14.....
 */
public class MainActivity extends FragmentActivity {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState){

        Log.e("trial", "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());

        mPager = (ViewPager) findViewById(R.id.pager);

        mPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });

        mPager.setAdapter(mPagerAdapter);

        final ActionBar actionBar = getActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
                mPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        // Add 3 tabs, specifying the tab's text and TabListener

        ActionBar.Tab recordTab = actionBar.newTab();
        ActionBar.Tab showTab = actionBar.newTab();
        recordTab.setText(R.string.recordActivities);
        showTab.setText(R.string.showActivities);
        recordTab.setTabListener(tabListener);
        showTab.setTabListener(tabListener);

        actionBar.addTab(recordTab);
        actionBar.addTab(showTab);

    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

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

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
