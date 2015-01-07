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
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Provides the fragment for the "Record Activities" Tab.
 */
public class RecordTab extends Fragment implements View.OnClickListener {

    private MyDBHelper dbHelper;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.dbHelper = new MyDBHelper(activity);
    }

    /**
     * onCreateView: inflate the UI and register the buttons.
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
         * Get all "Record Activity" type buttons and register OnClickListener.
         */
        ArrayList<View> allButtons;
        allButtons = rootView.findViewById(R.id.recordButtons).getTouchables();
        for (View buttonView : allButtons) {
            Button button = (Button) buttonView;
            button.setOnClickListener(this);
        }

        /**
         * Get the add activity button.
         */
        Button addButton = (Button) rootView.findViewById(R.id.addActButton);
        addButton.setOnClickListener(this);
        return rootView;
    }

    /**
     * Implement OnClickListener for the buttons.
     *
     * @param v The view been clicked.
     */
    @Override
    public void onClick(View v) {
        int buttonId = v.getId();

        switch (buttonId) {
            case R.id.addActButton:

                /**
                 * Get button name from EditText.
                 */
                Activity currentAct = getActivity();
                EditText textBox = (EditText) currentAct.findViewById(R.id.addActivityBox);
                String text = textBox.getText().toString();

                /**
                 * Add the activity to the overall button layout.
                 */
                this.addActivity(text);
                break;
            default:

                /**
                 * Get the name of the activity, record it to database.
                 */
                Button btn = (Button) v;
                String actName = btn.getText().toString();

                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                String currentTime = dateFormat.format(new Date());
                this.recordActivity(actName, currentTime);
                this.checkDB(actName);
                /**
                 * Produce the record successful message.
                 */
                this.showPopup(btn, currentTime);
        }
    }

    /**
     * Add a new button for customized activities.
     *
     * @param text Name of new activity.
     */
    private void addActivity(String text) {

        Activity act = getActivity();
        /**
         * Add the relevant activity to the database first.
         */
        this.addActivityTable(text);

        /**
         * Get the last row of buttons.
         * Deal with odd and even number of buttons accordingly.
         */
        TableLayout recordTable = (TableLayout) act.findViewById(R.id.recordButtonsTable);
        TableLayout showTable = (TableLayout) act.findViewById(R.id.showButtonsTable);
        TableRow recordTableLastRow = (TableRow) recordTable.getChildAt(recordTable.getChildCount() - 1);
        TableRow showTableLastRow = (TableRow) showTable.getChildAt(showTable.getChildCount() - 1);

        if (recordTableLastRow.getChildAt(1).getVisibility() == View.VISIBLE) {
            this.addButtonAsFirst(act, recordTable, recordTableLastRow, text);
            this.addButtonAsFirst(act, showTable, showTableLastRow, text);
        } else {
            this.addButtonAsSecond(recordTableLastRow, text);
            this.addButtonAsSecond(showTableLastRow, text);
        }

        /**
         * Hide the soft keyboard.
         */
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(recordTable.getWindowToken(), 0);

        /**
         * Clear the edit box.
         */
        EditText inputBox = (EditText) act.findViewById(R.id.addActivityBox);
        inputBox.setText("");
        inputBox.clearFocus();
    }

    /**
     * Deals with add activity if there are even number of buttons.
     *
     * @param act      Current Activity.
     * @param theTable The table containing the buttons.
     * @param lastRow  The previous complete row with 2 buttons.
     * @param text     The name of the new activity.
     */
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
        left.setOnClickListener(this);

        Button right = new Button(act);
        right.setText("TempButton");
        right.setLayoutParams(old.getLayoutParams());
        right.setOnClickListener(this);
        right.setVisibility(View.INVISIBLE);

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
     * Create a Popup Window acknowledging recording the activity.
     *
     * @param btn The button been clicked.
     */
    public void showPopup(Button btn, String currentTime) {

        /**
         * Get current activity and inflate the Popup Window layout.
         */
        Activity currentAct = getActivity();
        View popupView = currentAct.getLayoutInflater().inflate(R.layout.record_ack_popup, null);

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
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
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

    private void addActivityTable(String activityName) {

        activityName = activityName.replace(" ","");

        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        db.execSQL(this.dbHelper.getCreateTableCommand(activityName));

    }

    private void recordActivity(String tableName, String time){

        tableName = tableName.replace(" ","");

        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        ContentValues now = new ContentValues();

        now.put("TIME", time);

        db.insert(tableName, null, now);

    }



    private void checkDB(String tableName){
        SQLiteDatabase check = this.dbHelper.getReadableDatabase();

        tableName = tableName.replace(" ", "");

        Log.e("SQL", tableName);

        Cursor c = check.rawQuery("SELECT * FROM "+tableName, null);

        c.moveToLast();

        Log.e("SQL", ""+c.getString(0));
    }
}