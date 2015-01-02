package com.lifestats;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by yzhang on 25/12/14.
 * Not default anymore.
 */
public class ShowTab extends Fragment implements View.OnClickListener{

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button button = (Button) findView
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.showtab, container, false);


        ArrayList<View> allButtons;
        allButtons = rootView.findViewById(R.id.showButtons).getTouchables();

        for(View buttonView : allButtons){
            Button button = (Button) buttonView;
            button.setOnClickListener(this);
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {

    }
}
