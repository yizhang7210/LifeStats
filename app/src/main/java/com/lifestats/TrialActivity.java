package com.lifestats;




import android.app.Fragment;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * .Created by yzhang on 27/12/14.....
 */
public class TrialActivity extends Fragment {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 4;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    /*
    @Override
    public void onCreate(Bundle savedInstanceState){

        Log.e("trial", "on create");
        super.onCreate(savedInstanceState);
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //super.onCreateView(inflater,container,savedInstanceState);
        Log.e("trial", "on create view!!");
        return inflater.inflate(R.layout.recordtab, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.e("trial", "on activity created");
        this.setRetainInstance(true);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());

        mPager = (ViewPager) getView().findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        Log.e("trial", "exiting on activity created");
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
            Log.e("trial","getItem"+position);
            return new RecordTabs();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
