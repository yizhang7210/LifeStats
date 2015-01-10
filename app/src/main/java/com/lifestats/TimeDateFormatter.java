package com.lifestats;

import android.content.Context;

import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;

/**
 * Created by yzhang on 10/01/15.
 */
public class TimeDateFormatter extends DateAsXAxisLabelFormatter {
    public TimeDateFormatter(Context context) {
        super(context);
    }

    @Override
    public String formatLabel(double value, boolean isValueX) {
        if(isValueX){
            return super.formatLabel(value, isValueX);
        }else{

            return doubleToTimeString(-Double.parseDouble(super.formatLabel(value, isValueX)));
        }
    }

    public static String doubleToTimeString(double time){
        int hour = (int) Math.floor(time);
        time = (time - hour)*60;
        int min = (int) Math.floor(time);
        time = (time - min)*60;
        int sec = (int) Math.floor(time);

        return(hour+":"+min+":"+sec);
    }

}
