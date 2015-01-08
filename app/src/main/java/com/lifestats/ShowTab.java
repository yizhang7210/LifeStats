package com.lifestats;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Provides the fragment for the "Show Activities" Tab.
 */
public class ShowTab extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    /**
     * onCreateView: inflate the UI and register the buttons.
     * @param inflater layout inflater of parent activity.
     * @param container container of the view.
     * @param savedInstanceState saved instance state.
     * @return Return the inflated view corresponding to the Record Activities Tab.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         * Inflate the view.
         */
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.show_tab, container, false);

        /**
         * Get all "Show Activity" type buttons and register OnClickListener.
         */
        ArrayList<View> allButtons;
        allButtons = rootView.findViewById(R.id.showButtons).getTouchables();

        for(View buttonView : allButtons){
            Button button = (Button) buttonView;
            button.setOnClickListener(this);
        }

        return rootView;
    }

    /**
     * Implement OnClickListener for the buttons.
     * @param v The view been clicked.
     */
    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
