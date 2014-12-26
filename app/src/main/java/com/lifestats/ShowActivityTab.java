package com.lifestats;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yzhang on 25/12/14.
 * Not default anymore.
 */
public class ShowActivityTab extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.showtab, container, false);
        return view;
    }
}
