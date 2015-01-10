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
public class RecordTab extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    public static MyDBHelper dbHelper;
    public static ArrayList<String> existButtons = new ArrayList<>();

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
            button.setOnLongClickListener(this);
            Log.e("button", button.getText().toString());
            this.existButtons.add(button.getText().toString().replace(" ", ""));
        }

        /**
         * Get the add activity button.
         */
        Button addButton = (Button) rootView.findViewById(R.id.addActButton);
        addButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onStart() {

        super.onStart();

        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                String tableName = c.getString(c.getColumnIndex("name"));

                if(!tableName.equals("android_metadata") && !this.existButtons.contains(tableName)) {
                    this.addActivityToRecordTab(tableName);
                }

                c.moveToNext();
            }
        }
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
                this.addActivityToShowTab(text);
                this.addActivityToRecordTab(text);
                break;
            default:

                /**
                 * Get the name of the activity, record it to database.
                 */
                Button btn = (Button) v;
                String actName = btn.getText().toString();

                String dateTimeFormat = getString(R.string.dateTimeFormat);
                String timeFormat = getString(R.string.timeFormat);

                DateFormat dateFormatToRecord = new SimpleDateFormat(dateTimeFormat);
                DateFormat dateFormatToShow = new SimpleDateFormat(timeFormat);
                Date now = new Date();
                this.recordActivity(actName, dateFormatToRecord.format(now));
                this.checkDB(actName);//Debug
                /**
                 * Produce the record successful message.
                 */
                this.showRecordPopup(btn, dateFormatToShow.format(now));
        }
    }

    /**
     * Add a new button for customized activities.
     *
     * @param text Name of new activity.
     */
    private void addActivityToRecordTab(String text) {

        Activity act = getActivity();
        /**
         * Add the relevant activity to the database first.
         */
        this.addActivityToDB(text);
        this.existButtons.add(text);
        /**
         * Get the last row of buttons.
         * Deal with odd and even number of buttons accordingly.
         */
        TableLayout recordTable = (TableLayout) act.findViewById(R.id.recordButtonsTable);
        TableRow recordLastRow = (TableRow) recordTable.getChildAt(recordTable.getChildCount() - 1);

        if (recordLastRow.getChildAt(1).getVisibility() == View.VISIBLE) {
            this.addButtonAsFirst(act, recordTable, recordLastRow, text, true);
        } else {
            this.addButtonAsSecond(recordLastRow, text);
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

    public void addActivityToShowTab(String text) {

        Activity act = getActivity();

        /**
         * Get the last row of buttons.
         * Deal with odd and even number of buttons accordingly.
         */

        TableLayout showTable = (TableLayout) act.findViewById(R.id.showButtonsTable);
        TableRow showLastRow = (TableRow) showTable.getChildAt(showTable.getChildCount() - 1);

        if (showLastRow.getChildAt(1).getVisibility() == View.VISIBLE) {
            this.addButtonAsFirst(act, showTable, showLastRow, text, false);
        } else {
            this.addButtonAsSecond(showLastRow, text);
        }
    }

    /**
     * Deals with add activity if there are even number of buttons.
     *
     * @param act      Current Activity.
     * @param theTable The table containing the buttons.
     * @param lastRow  The previous complete row with 2 buttons.
     * @param text     The name of the new activity.
     */
    private void addButtonAsFirst(Context act, TableLayout theTable,
                                  TableRow lastRow, String text, Boolean isOnRecordTab) {

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
        if(isOnRecordTab){
            left.setOnClickListener(this);
            left.setOnLongClickListener(this);
            right.setOnClickListener(this);
            right.setOnLongClickListener(this);
        }else{

            ShowTab showTab = new ShowTab();

            left.setOnClickListener(showTab);
            right.setOnClickListener(showTab);
        }


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
    public void showRecordPopup(Button btn, String currentTime) {

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

    public void showClearHistPopup(Button btn) {

        final Button buttonToDelete = btn;
        /**
         * Get current activity and inflate the Popup Window layout.
         */
        Activity currentAct = getActivity();
        View popupView = currentAct.getLayoutInflater().inflate(R.layout.delete_act_popup, null);

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
        String recordSuccess = getString(R.string.confirmDelete,
                actName);

        /**
         * Get the corresponding view and add the text.
         */
        final TextView textView = (TextView) popupView.findViewById(R.id.confirmDeleteText);
        textView.setText(recordSuccess);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        /**
         * Add the yes and no buttons to the Popup Window.
         */
        Button buttonNo = (Button) popupView.findViewById(R.id.deleteNoButton);
        buttonNo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

        Button buttonYes = (Button) popupView.findViewById(R.id.deleteYesButton);
        buttonYes.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                String dbName = buttonToDelete.getText().toString().replace(" ","");

                SQLiteDatabase db = RecordTab.dbHelper.getWritableDatabase();

                db.execSQL("DELETE FROM " + dbName);

                pw.dismiss();

            }
        });


        /**
         * Add the Popup Window itself.
         */
        pw.showAtLocation(popupView, Gravity.CENTER, 0, 0);

    }

    private void addActivityToDB(String activityName) {

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

    @Override
    public boolean onLongClick(View v) {

        this.showClearHistPopup((Button) v);

        return true;
    }
}