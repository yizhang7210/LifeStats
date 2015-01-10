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
 * Provides the fragment for the "Record Activities" Tab.
 */
public class RecordTab extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    public static MyDBHelper dbHelper;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        RecordTab.dbHelper = new MyDBHelper(activity);
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
            ButtonHelper.existButtons.add(button.getText().toString().replace(" ", "_"));
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

        SQLiteDatabase db = RecordTab.dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                String dbTableName = c.getString(c.getColumnIndex("name"));

                if(!dbTableName.equals("android_metadata") && !ButtonHelper.existButtons.contains(dbTableName)) {
                    Button savedBtn = ButtonHelper.addButton(getActivity(),dbTableName,false,ButtonHelper.RECORD_TAB);
                    savedBtn.setOnClickListener(this);
                    savedBtn.setOnLongClickListener(this);
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

        if (buttonId == R.id.addActButton) {

            Log.e("adding new button", "wtf");
            /**
             * Get button name from EditText.
             */
            Activity act = getActivity();
            EditText textBox = (EditText) act.findViewById(R.id.addActivityBox);
            String text = textBox.getText().toString();

            /**
             * Add the button to exist buttons.
             */
            ButtonHelper.existButtons.add(text.replace(" ", "_"));

            /**
             * Add the activity to the overall button layout.
             */

            Button newBtnR = ButtonHelper.addButton(act, text, true, ButtonHelper.RECORD_TAB);
            //Button newBtnS = ButtonHelper.addButton(act, text, true, ButtonHelper.SHOW_TAB);

            newBtnR.setOnClickListener(this);
            newBtnR.setOnLongClickListener(this);

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
        }else {
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

                String dbName = buttonToDelete.getText().toString().replace(" ","_");

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

    private void recordActivity(String tableName, String time){

        tableName = tableName.replace(" ","_");

        SQLiteDatabase db = RecordTab.dbHelper.getWritableDatabase();

        ContentValues now = new ContentValues();

        now.put("TIME", time);

        db.insert(tableName, null, now);

    }

    private void checkDB(String tableName){
        SQLiteDatabase check = RecordTab.dbHelper.getReadableDatabase();

        tableName = tableName.replace(" ", "_");

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