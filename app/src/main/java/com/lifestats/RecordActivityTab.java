package com.lifestats;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by yzhang on 25/12/14.
 */

public class RecordActivityTab extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.recordtab, container, false);
        TextView textview = (TextView) view.findViewById(R.id.addNewActivities);
        textview.setText(R.string.addNewActivities);
        return view;
    }
}
