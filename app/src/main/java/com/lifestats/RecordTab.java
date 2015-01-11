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

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * RecordTab: Provides the Fragment for the "Record Activities" Tab.
 */
public class RecordTab extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    /**
     * Need database helper.
     */
    public static MyDBHelper dbHelper;

    /**
     * onAttach: Initialize the database helper.
     *
     * @param activity The activity this Fragment attaches to.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        RecordTab.dbHelper = new MyDBHelper(activity);
    }

    /**
     * onCreateView: Inflate the UI and register the buttons.
     *
     * @param inflater           layout inflater of parent activity.
     * @param container          container of the view.
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
                R.layout.record_tab, container, false);

        /**
         * Get all "Record Activity" type buttons and register OnClickListener
         * and OnLongClickListener.
         */
        ArrayList<View> allButtons;
        allButtons = rootView.findViewById(R.id.recordButtons).getTouchables();
        for (View buttonView : allButtons) {
            Button button = (Button) buttonView;
            button.setOnClickListener(this);
            button.setOnLongClickListener(this);
            ButtonHelper.existButtons.add(button.getText().toString().replace(" ", "_"));
        }

        /**
         * Get the add activity button.
         */
        Button addButton = (Button) rootView.findViewById(R.id.addActButton);
        addButton.setOnClickListener(this);
        return rootView;
    }

    /**
     * onStart: Recreate the user added buttons from database.
     */
    @Override
    public void onStart() {
        super.onStart();

        /**
         * Get all existing table names and add them as Buttons. Set listeners.
         */
        SQLiteDatabase db = RecordTab.dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery(getString(R.string.sqlQuerySavedTables), null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String dbTableName = c.getString(c.getColumnIndex(getString(R.string.name)));

                if (!dbTableName.equals(getString(R.string.androidMetadata)) &&
                        !ButtonHelper.existButtons.contains(dbTableName)) {
                    Button savedBtn = ButtonHelper.addButton(getActivity(),
                            dbTableName, false, ButtonHelper.RECORD_TAB);
                    savedBtn.setOnClickListener(this);
                    savedBtn.setOnLongClickListener(this);
                }
                c.moveToNext();
            }
        }
    }

    /**
     * onClick: Implement OnClickListener for the buttons.
     * Add the appropriate buttons to the Tabs and register listeners if it's the "Add" button.
     * Otherwise record the time for that user activity.
     *
     * @param v The view been clicked.
     */
    @Override
    public void onClick(View v) {
        int buttonId = v.getId();

        /**
         * The "Add" button: Add the new button to both Tabs and register both listeners.
         */
        if (buttonId == R.id.addActButton) {

            /**
             * Get button name from EditText.
             */
            Activity act = getActivity();
            EditText textBox = (EditText) act.findViewById(R.id.addActivityBox);
            String text = textBox.getText().toString();

            if(text.equals(""))
                return;
            
            /**
             * Add the button to the list of existing buttons.
             */
            ButtonHelper.existButtons.add(text.replace(" ", "_"));

            /**
             * Use ButtonHelper to add the button, to RecordTab and then ShowTab.
             * Need to fetch the running ShowTab instance to add listener.
             * The ShowTab Fragment is found by FragmentManager using its default tag.
             */
            Button newBtnR = ButtonHelper.addButton(act, text, true, ButtonHelper.RECORD_TAB);
            newBtnR.setOnClickListener(this);
            newBtnR.setOnLongClickListener(this);

            Button newBtnS = ButtonHelper.addButton(act, text, true, ButtonHelper.SHOW_TAB);
            ShowTab s = (ShowTab) getFragmentManager().findFragmentByTag(
                    getString(R.string.showTabTag, R.id.pager));

            newBtnS.setOnClickListener(s);

            /**
             * Hide the soft keyboard.
             */
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            /**
             * Clear the edit box.
             */
            EditText inputBox = (EditText) act.findViewById(R.id.addActivityBox);
            inputBox.setText("");
            inputBox.clearFocus();
        } else {
            /**
             * Other record buttons: Get the name of the activity, record it to database.
             */
            Button btn = (Button) v;
            String actName = btn.getText().toString();

            /**
             * Format the current date and time to two formats.
             * One for showing in the PopupWindow, one for writing to database.
             */
            String dateTimeFormat = getString(R.string.dateTimeFormat);
            DateFormat dateFormatToRecord = new SimpleDateFormat(dateTimeFormat);

            String timeFormat = getString(R.string.timeFormat);
            DateFormat dateFormatToShow = new SimpleDateFormat(timeFormat);

            /**
             * Get the time. Write to database, and produce acknowledgement PopupWindow.
             */
            Date now = new Date();
            this.recordActivity(actName, dateFormatToRecord.format(now));
            this.showRecordPopup(btn, dateFormatToShow.format(now));
        }
    }

    /**
     * onLongClick: To clear history of particular activity.
     *
     * @param v The button been clicked on.
     * @return True because need to consume the click (i.e. it's not a short click).
     */
    @Override
    public boolean onLongClick(View v) {
        this.showClearHistPopup((Button) v);
        return true;
    }

    /**
     * The helpers for producing PopupWindow and write to database.
     */

    /**
     * For onClick: Create a Popup Window acknowledging recording the activity.
     *
     * @param btn The button been clicked.
     */
    public void showRecordPopup(Button btn, String currentTime) {

        /**
         * Get current activity and inflate the Popup Window layout.
         */
        Activity act = getActivity();
        View popupView = act.getLayoutInflater().inflate(R.layout.record_ack_popup, null);

        /**
         * Define the Popup Window.
         */
        final PopupWindow pw = new PopupWindow(
                popupView,
                TableLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        /**
         * Get current display message.
         */
        String recordSuccess = getString(R.string.recordSuccessful,
                btn.getText(), currentTime);

        /**
         * Get the corresponding view and add the text.
         */
        TextView textView = (TextView) popupView.findViewById(R.id.recordAckText);
        textView.setText(recordSuccess);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                act.getResources().getInteger(R.integer.popupWindowFontSize));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        /**
         * Add the dismiss button to the Popup Window.
         */
        Button buttonOK = (Button) popupView.findViewById(R.id.recordAckButton);
        buttonOK.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

        /**
         * Add the Popup Window itself.
         */
        pw.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    /**
     * For onLongClick: clear the history of the activity.
     *
     * @param btn The button been clicked.
     */
    public void showClearHistPopup(Button btn) {

        final Button buttonToClear = btn;
        /**
         * Get current activity and inflate the Popup Window layout.
         */
        Activity act = getActivity();
        View popupView = act.getLayoutInflater().inflate(R.layout.clear_act_popup, null);

        /**
         * Define the Popup Window.
         */
        final PopupWindow pw = new PopupWindow(
                popupView,
                TableLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        /**
         * Get current display message.
         */
        final String actName = btn.getText().toString();
        String confirmClear = getString(R.string.confirmClear, actName);

        /**
         * Get the corresponding view and add the text.
         */
        final TextView textView = (TextView) popupView.findViewById(R.id.confirmClearText);
        textView.setText(confirmClear);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                act.getResources().getInteger(R.integer.popupWindowFontSize));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        /**
         * Add the yes and no buttons to the Popup Window.
         */
        Button buttonNo = (Button) popupView.findViewById(R.id.clearNoButton);
        buttonNo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

        Button buttonYes = (Button) popupView.findViewById(R.id.clearYesButton);
        buttonYes.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Change to the database name and clear the database table.
                 */
                String dbName = buttonToClear.getText().toString().replace(" ", "_");
                SQLiteDatabase db = RecordTab.dbHelper.getWritableDatabase();
                db.execSQL(getString(R.string.sqlDelete) + dbName);

                pw.dismiss();

            }
        });

        /**
         * Add the Popup Window itself.
         */
        pw.showAtLocation(popupView, Gravity.CENTER, 0, 0);

    }

    /**
     * Insert the new recorded time to the appropriate database table.
     *
     * @param tableName The database table keeping track of that activity.
     * @param time      The time to be recorded.
     */
    private void recordActivity(String tableName, String time) {

        /**
         * Grab the database with the corrected name and just insert.
         */
        tableName = tableName.replace(" ", "_");
        SQLiteDatabase db = RecordTab.dbHelper.getWritableDatabase();

        ContentValues now = new ContentValues();
        now.put(getString(R.string.column_name_time), time);
        db.insert(tableName, null, now);
    }

}