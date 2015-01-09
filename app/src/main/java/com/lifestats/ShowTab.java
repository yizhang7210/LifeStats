package com.lifestats;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Provides the fragment for the "Show Activities" Tab.
 */
public class ShowTab extends Fragment implements View.OnClickListener {

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

    @Override
    public void onStart() {

        super.onStart();

        SQLiteDatabase db = RecordTab.dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                String tableName = c.getString(c.getColumnIndex("name"));

                if(!tableName.equals("android_metadata") && !RecordTab.existButtons.contains(tableName)) {

                    RecordTab r = new RecordTab();

                    this.addActivityToShowTab(tableName);
                }

                c.moveToNext();
            }
        }
    }

    private void addActivityToShowTab(String text) {

        Activity act = getActivity();

        /**
         * Get the last row of buttons.
         * Deal with odd and even number of buttons accordingly.
         */

        TableLayout showTable = (TableLayout) act.findViewById(R.id.showButtonsTable);
        TableRow showLastRow = (TableRow) showTable.getChildAt(showTable.getChildCount() - 1);

        if (showLastRow.getChildAt(1).getVisibility() == View.VISIBLE) {
            this.addButtonAsFirst(act, showTable, showLastRow, text);
        } else {
            this.addButtonAsSecond(showLastRow, text);
        }
    }



    private void addButtonAsFirst(Context act, TableLayout theTable, TableRow lastRow, String text) {

        /**
         * Create new row for the table.
         */
        TableRow newRow = new TableRow(act);
        newRow.setLayoutParams(lastRow.getLayoutParams());

        /**
         * Create both buttons. Set the second invisible.
         */
        Button old = (Button) lastRow.getChildAt(0);

        Button left = new Button(act);
        left.setText(text);
        left.setLayoutParams(old.getLayoutParams());

        Button right = new Button(act);
        right.setText("TempButton");
        right.setLayoutParams(old.getLayoutParams());

        right.setVisibility(View.INVISIBLE);

        /**
         * Add OnClickListener and OnLongClickListener
         */

        left.setOnClickListener(this);
        right.setOnClickListener(this);


        /**
         * Add them on.
         */
        newRow.addView(left);
        newRow.addView(right);

        theTable.addView(newRow);
    }

    /**
     * Deal with add activity if there are odd number of buttons.
     *
     * @param theRow The current row that already has 1 button.
     * @param text   The name of the new activity.
     */
    private void addButtonAsSecond(TableRow theRow, String text) {

        /**
         * Get the second button. Set text and visibility.
         */
        Button btn = (Button) theRow.getChildAt(1);

        btn.setText(text);
        btn.setVisibility(View.VISIBLE);
    }

    /**
     * Implement OnClickListener for the buttons.
     * @param v The view been clicked.
     */
    @Override
    public void onClick(View v) {

    }

}
