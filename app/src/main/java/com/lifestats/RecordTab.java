package com.lifestats;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Provides the right fragment for the "Record Activities" Tab.
 */
public class RecordTab extends Fragment implements View.OnClickListener{

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
                R.layout.record_tab, container, false);

        /**
         * Get all "Record Activity" type buttons and register OnClickListener.
         */
        ArrayList<View> allButtons;
        allButtons = rootView.findViewById(R.id.recordButtons).getTouchables();
        for(View buttonView : allButtons){
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
     * @param v The view been clicked.
     */
    @Override
    public void onClick(View v) {
        int buttonId = v.getId();

        switch (buttonId){
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
                addActivity((Button) v, text);
                break;
            default:
                showPopup((Button) v);
        }
    }

    /**
     * Add a new button for customized activities.
     * @param btn The "Add" button.
     */
    private void addActivity(Button btn, String text) {
        Log.e("addButton", text);




    }

    /**
     * Create a Popup Window acknowledging recording the activity.
     * @param btn The button been clicked.
     */
    public void showPopup(Button btn) {

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
         * Get current time.
         */
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String recordSuccess = getString(R.string.recordSuccessful,
                btn.getText(), dateFormat.format(new Date()));

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
        buttonOK.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }});

        /**
         * Add the Popup Window itself.
         */
        pw.showAtLocation(popupView, Gravity.CENTER, 0, 0);

    }
}