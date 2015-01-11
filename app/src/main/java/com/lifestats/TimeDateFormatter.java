/**
 * LifeStats
 * Copyright (C) 2014  Yi Zhang
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License,
 * with the "Linking Exception", which can be found at the license.txt
 * file in this program.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * with the "Linking Exception" along with this program; if not,
 * write to the author Yi Zhang <yi.zhang7210@gmail.com>.
 */

package com.lifestats;

import android.content.Context;

import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;

/**
 * TimeDateFormatter: Add y-axis formatting on the DateAsXAxisLabelFormatter.
 */
public class TimeDateFormatter extends DateAsXAxisLabelFormatter {
    /**
     * Constructor.
     *
     * @param context context.
     */
    public TimeDateFormatter(Context context) {
        super(context);
    }

    /**
     * Parse a negative number between -24 and 0 to time of the form HH:mm.
     *
     * @param time The double representing time.
     * @return A String of the form HH:mm representing time.
     */
    public static String doubleToTimeString(double time) {
        int hour = (int) Math.floor(time);
        time = (time - hour) * 60;
        int min = (int) Math.floor(time);
        time = (time - min) * 60;
        int sec = (int) Math.floor(time);

        return (String.format("%02d:%02d", hour, min));
    }

    /**
     * Override to format y-axis labels.
     *
     * @param value    The value of the DataPoint.
     * @param isValueX True if formatting x-axis.
     * @return String that will be used as axis labels.
     */
    @Override
    public String formatLabel(double value, boolean isValueX) {
        if (isValueX) {
            return super.formatLabel(value, isValueX);
        } else {
            /**
             * Parse a negative number between -24 and 0 to time.
             */
            return doubleToTimeString(-Double.parseDouble(super.formatLabel(value, isValueX)));
        }
    }

}
