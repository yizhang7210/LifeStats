package com.lifestats;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class RecordTab extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.recordtab, container, false);

        ArrayList<View> allButtons;
        allButtons = rootView.findViewById(R.id.recordButtons).getTouchables();

        for(View buttonView : allButtons){
            Button button = (Button) buttonView;
            button.setOnClickListener(this);
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int buttonId = v.getId();

        Button button = (Button) v.findViewById(buttonId);

        showPopup(button);
    }

    public void showPopup(Button btn) {

        Activity currentAct = getActivity();
        View popupView = currentAct.getLayoutInflater().inflate(R.layout.record_ack_popup, null);

        final PopupWindow pw = new PopupWindow(
                popupView,
                TableLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView textView = (TextView) popupView.findViewById(R.id.recordAckText);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        String recordSuccess = getString(R.string.recordSuccessful,
                btn.getText(),dateFormat.format(new Date()));

        textView.setText(recordSuccess);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        Button buttonOK = (Button) popupView.findViewById(R.id.recordAckButton);
        buttonOK.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                pw.dismiss();
            }});

        pw.showAtLocation(popupView, Gravity.CENTER, 0, 0);

    }
}