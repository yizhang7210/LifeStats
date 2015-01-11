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
 */

package com.lifestats;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

/**
 * Button helper: Responsible for adding buttons to views.
 */
public class ButtonHelper {
    public static final int RECORD_TAB = 0;
    public static final int SHOW_TAB = 1;
    public static ArrayList<String> existButtons = new ArrayList<>();

    /**
     * Depending on the tab and if its new, add the button to the layout.
     *
     * @param act         The running activity.
     * @param dbTableName The database table name corresponding to the button.
     * @param isNew       If is the button is being added the first time.
     * @param whichTab    RECORD_TAB or SHOW_TAB.
     * @return The Button object that is constructed.
     */
    public static Button addButton(Activity act, String dbTableName, boolean isNew, int whichTab) {

        /**
         * Get the last row of buttons.
         * Deal with odd and even number of buttons accordingly.
         */
        Button btn;
        String buttonName = dbTableName.replace("_", " ");

        int tableId;
        switch (whichTab) {
            case ButtonHelper.RECORD_TAB:
                tableId = R.id.recordButtonsTable;
                break;
            case ButtonHelper.SHOW_TAB:
                tableId = R.id.showButtonsTable;
                break;
            default:
                throw new RuntimeException("Adding button to non-existent tab");
        }

        /**
         * Get the last row of the table where the button will be added to.
         * Deal with the left and right button separately.
         */
        TableLayout table = (TableLayout) act.findViewById(tableId);
        TableRow lastRow = (TableRow) table.getChildAt(table.getChildCount() - 1);

        if (lastRow.getChildAt(1).getVisibility() == View.VISIBLE) {
            btn = addButtonAsFirst(act, table, lastRow, buttonName);
        } else {
            btn = addButtonAsSecond(lastRow, buttonName);
        }

        /**
         * Also add to database if its a new button.
         */
        if (isNew) {
            ButtonHelper.addActivityToDB(buttonName);
        }

        return btn;
    }

    /**
     * Add the new activity represented by the button to database.
     *
     * @param buttonName The text/name of the button.
     */
    private static void addActivityToDB(String buttonName) {

        /**
         * Convert to the right name format and execute the command.
         */
        String dbName = buttonName.replace(" ", "_");
        SQLiteDatabase db = RecordTab.dbHelper.getWritableDatabase();
        db.execSQL(RecordTab.dbHelper.getCreateTableCommand(dbName));
    }

    /**
     * Add button to the layout as the left one on a row.
     *
     * @param act     The running activity.
     * @param table   The table of buttons.
     * @param lastRow The previous row of buttons.
     * @param actName Name of the activity.
     * @return The Button object that is constructed.
     */
    private static Button addButtonAsFirst(Activity act, TableLayout table,
                                           TableRow lastRow, String actName) {

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
        left.setText(actName);
        left.setLayoutParams(old.getLayoutParams());

        Button right = new Button(act);
        right.setText("TempButton");
        right.setLayoutParams(old.getLayoutParams());

        right.setVisibility(View.INVISIBLE);

        /**
         * Add them on.
         */
        newRow.addView(left);
        newRow.addView(right);

        table.addView(newRow);

        return left;
    }

    /**
     * Add button to the layout as the right one on a row
     *
     * @param theRow  The row the button will be added to.
     * @param actName Name of the activity.
     * @return The Button object that is constructed.
     */
    private static Button addButtonAsSecond(TableRow theRow, String actName) {

        /**
         * Get the second button. Set text and visibility.
         */
        Button btn = (Button) theRow.getChildAt(1);

        btn.setText(actName);
        btn.setVisibility(View.VISIBLE);

        return btn;
    }
}
