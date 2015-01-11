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
 *
 * Acknowledgement:
 * This app used the GraphView library from: http://www.jjoe64.com/p/graphview-library.html,
 * and examples from the official Android documentation: http://developer.android.com
 * and http://stackoverflow.com.
 */
package com.lifestats;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * ShowTab: Provides the Fragment for the "Show Activities" Tab.
 */
public class ShowTab extends Fragment implements View.OnClickListener {

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
                R.layout.show_tab, container, false);

        /**
         * Get all "Show Activity" type buttons and register OnClickListener.
         */
        ArrayList<View> allButtons;
        allButtons = rootView.findViewById(R.id.showButtons).getTouchables();

        for (View buttonView : allButtons) {
            Button button = (Button) buttonView;
            button.setOnClickListener(this);
        }

        return rootView;
    }

    /**
     * onStart: Recreate the user added buttons from database.
     */
    @Override
    public void onStart() {
        super.onStart();

        /**
         * Get all existing table names and add them as Buttons. Set listeners.
         */
        SQLiteDatabase db = RecordTab.dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery(getString(R.string.sqlQuerySavedTables), null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String dbTableName = c.getString(c.getColumnIndex(getString(R.string.name)));

                if (!dbTableName.equals(getString(R.string.androidMetadata)) &&
                        !ButtonHelper.existButtons.contains(dbTableName)) {
                    Button btn = ButtonHelper.addButton(getActivity(),
                            dbTableName, false, ButtonHelper.SHOW_TAB);
                    btn.setOnClickListener(this);
                }
                c.moveToNext();
            }
        }
    }

    /**
     * onClick: Implement OnClickListener for the buttons.
     * Draw a graph of the recorded historical time of particular activity.
     *
     * @param v The view been clicked.
     */
    @Override
    public void onClick(View v) {
        /**
         * Get the button text, change to the name of the database table.
         */
        Button btn = (Button) v;
        String actName = btn.getText().toString();
        String dbTableName = actName.replace(" ", "_");

        /**
         * Initialize the array for the stored times get the query results.
         */
        ArrayList<String> timeHist = new ArrayList<>();

        SQLiteDatabase db = RecordTab.dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(getString(R.string.sqlSelectAll) + dbTableName, null);

        /**
         * If there is a history, get all out and plot.
         * Else warn that there is no record.
         */
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String recordTime = c.getString(c.getColumnIndex(getString(R.string.column_name_time)));
                timeHist.add(recordTime);
                c.moveToNext();
            }

            this.plotGraph(timeHist, actName);
        } else {
            this.warnNoData(actName);
        }
    }

    /**
     * Plot the historical time of a particular activity.
     *
     * @param timeHist The String array containing the recorded times.
     * @param actName  Name of the activity to be plotted.
     */
    private void plotGraph(ArrayList<String> timeHist, String actName) {
        /**
         * Use again PopupWindow. Initialize it.
         */
        Activity act = getActivity();
        /**
         * Set the height and weight to be exactly the same as the buttons table.
         * Therefore still leave the ActionBar visible.
         * Set a GraphView inside the PopupWindow.
         */
        View buttonsTable = act.findViewById(R.id.showButtonsTable);
        int height = buttonsTable.getMeasuredHeight();
        int width = buttonsTable.getMeasuredWidth();

        View graphView = act.getLayoutInflater().inflate(R.layout.show_graph, null);
        final PopupWindow pw = new PopupWindow(
                graphView,
                width,
                height);

        /**
         * Set the background drawable.
         */
        Drawable background = getResources().getDrawable(R.drawable.black_background);
        pw.setBackgroundDrawable(background);

        /**
         * Exit the pop up window with back button pressed.
         */
        pw.setFocusable(true);
        pw.setOutsideTouchable(true);
        pw.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });

        /**
         * Create GraphView and the DataPoint array for GraphSeries. Getting ready to draw.
         */
        GraphView graph = (GraphView) graphView.findViewById(R.id.theGraph);

        int size = timeHist.size();

        DataPoint[] dataPoints = new DataPoint[size];
        for (int i = 0; i < size; ++i) {
            String dateTime = timeHist.get(i);
            dataPoints[i] = new DataPoint(this.getDateFromString(dateTime),
                    this.getTimeFromString(dateTime));
        }

        /**
         * Create and draw the series and add the graph to view.
         */
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(dataPoints);
        graph.addSeries(series);

        /**
         * Set title.
         */
        graph.setTitle(getString(R.string.graphTitle, actName));
        graph.setTitleTextSize(act.getResources().getInteger(R.integer.graphTitleFontSize));

        /**
         * Set the axes using TimeDateFormatter, inherited from DateAsXAxisLabelFormatter.
         */
        TimeDateFormatter formatter = new TimeDateFormatter(getActivity());

        /**
         * The y-axis. Get the min and max from the values.
         */
        double maxY = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minX = Double.POSITIVE_INFINITY;

        for (DataPoint dataPoint : dataPoints) {
            double thisX = dataPoint.getX();
            double thisY = -dataPoint.getY();

            if (thisX > maxX)
                maxX = thisX;

            if (thisX < minX)
                minX = thisX;

            if (thisY > maxY)
                maxY = thisY;

            if (thisY < minY)
                minY = thisY;
        }

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(-Math.floor(2 * minY) / 2);
        graph.getViewport().setMinY(-Math.ceil(2 * maxY) / 2);

        /**
         * The x-axis. Use the dates.
         */
        graph.getGridLabelRenderer().setLabelFormatter(formatter);
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(minX - act.getResources().getInteger(R.integer.millis));
        graph.getViewport().setMaxX(maxX + act.getResources().getInteger(R.integer.millis));

        /**
         * Finally add the PopupWindow to UI.
         */
        pw.showAtLocation(graphView, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
    }

    /**
     * Yet another PopupWindow warning the user if there is no history of an activity.
     *
     * @param actName Name of the activity selected.
     */
    private void warnNoData(String actName) {

        /**
         * Get current activity and inflate the Popup Window layout.
         */
        Activity act = getActivity();
        View popupView = act.getLayoutInflater().inflate(R.layout.record_ack_popup, null);

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
        String warnMessage = getString(R.string.warnNoData, actName);

        /**
         * Get the corresponding view and add the text.
         */
        TextView textView = (TextView) popupView.findViewById(R.id.recordAckText);
        textView.setText(warnMessage);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                act.getResources().getInteger(R.integer.popupWindowFontSize));
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

    /**
     * Convert a String of date of the form yyyy:MM:dd:HH:mm:ss to a date.
     *
     * @param dateTimeString The string representing the time and date.
     * @return A Date object representing the time and date.
     */
    private Date getDateFromString(String dateTimeString) {

        /**
         * Fetch the date/time format and sanity check.
         */
        String dateFormatString = getString(R.string.dateTimeFormat);
        if (dateTimeString.length() != dateFormatString.length()) {
            throw new RuntimeException(getString(R.string.wrongDateFormatError, dateFormatString));
        }

        /**
         * Parse the string to the right format.
         */
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        try {
            return (dateFormat.parse(dateTimeString));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert a string of date of the form HH:mm:ss to a number between -24 and 0.
     * Want morning to be plotted higher and evenings lower.
     *
     * @param dateTimeString The string representing the time.
     * @return Converted number representing the time.
     */
    private double getTimeFromString(String dateTimeString) {

        /**
         * Again, fetch the right format with sanity check.
         */
        String dateFormatString = getString(R.string.dateTimeFormat);

        if (dateTimeString.length() != dateFormatString.length()) {
            throw new RuntimeException(getString(R.string.wrongDateFormatError, dateFormatString));
        }

        /**
         * Do the calculation.
         */
        double hour = Double.parseDouble(dateTimeString.substring(11, 13));
        double min = Double.parseDouble(dateTimeString.substring(14, 16));
        double sec = Double.parseDouble(dateTimeString.substring(17, 19));

        return (-(hour + min / 60 + sec / 3600));
    }

}
