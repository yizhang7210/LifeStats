package com.lifestats;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by yzhang on 10/01/15.
 */
public class ButtonHelper {
    public static ArrayList<String> existButtons = new ArrayList<>();
    public static final int RECORD_TAB = 0;
    public static final int SHOW_TAB = 1;

    public static Button addButton(Activity act, String dbTableName, boolean isNew, int whichTab) {
        /**
         * Get the last row of buttons.
         * Deal with odd and even number of buttons accordingly.
         */

        Button btn;
        int tableId;
        switch (whichTab){
            case ButtonHelper.RECORD_TAB:
                tableId = R.id.recordButtonsTable;
                break;
            case ButtonHelper.SHOW_TAB:
                tableId = R.id.showButtonsTable;
                break;
            default:
                throw new RuntimeException("Adding button to non-existent tab");
        }

        TableLayout table = (TableLayout) act.findViewById(tableId);
        TableRow lastRow = (TableRow) table.getChildAt(table.getChildCount() - 1);

        String buttonName = dbTableName.replace("_"," ");
        if (lastRow.getChildAt(1).getVisibility() == View.VISIBLE) {
            btn = addButtonAsFirst(act, table, lastRow, buttonName, whichTab);
        } else {
            btn = addButtonAsSecond(lastRow, buttonName);
        }

        if(isNew){
            ButtonHelper.addActivityToDB(buttonName);
        }

        return btn;
    }

    private static void addActivityToDB(String buttonName) {

        String dbName = buttonName.replace(" ","_");

        SQLiteDatabase db = RecordTab.dbHelper.getWritableDatabase();

        db.execSQL(RecordTab.dbHelper.getCreateTableCommand(dbName));
    }

    private static Button addButtonAsFirst(Activity act, TableLayout table,
                                         TableRow lastRow, String actName, int whichTab) {

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
         * Add OnClickListener and OnLongClickListener
         */

        /*
        switch (whichTab){
            case ButtonHelper.RECORD_TAB:

                RecordTab r = new RecordTab();
                left.setOnClickListener(r);
                left.setOnLongClickListener(r);
                right.setOnClickListener(r);
                right.setOnLongClickListener(r);
                break;
            case ButtonHelper.SHOW_TAB:
                ShowTab s = new ShowTab();

                left.setOnClickListener(s);
                right.setOnClickListener(s);
        }
        */

        /**
         * Add them on.
         */
        newRow.addView(left);
        newRow.addView(right);

        table.addView(newRow);

        return left;
    }

    private static Button addButtonAsSecond(TableRow theRow, String tableName) {

        /**
         * Get the second button. Set text and visibility.
         */
        Button btn = (Button) theRow.getChildAt(1);

        btn.setText(tableName);
        btn.setVisibility(View.VISIBLE);

        return btn;
    }
}
