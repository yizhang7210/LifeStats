package com.lifestats;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;


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
        this.trueOnClick(v, buttonId);
    }

    private void trueOnClick(View v, int buttonId){
        Log.e("button", Integer.toString(buttonId));

        Button button = (Button) v.findViewById(buttonId);
        button.setText(button.getText()+"1");
    }
}