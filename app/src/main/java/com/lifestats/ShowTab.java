package com.lifestats;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import android.widget.TableRow;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Provides the fragment for the "Show Activities" Tab.
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

    @Override
    public void onStart() {

        super.onStart();

        SQLiteDatabase db = RecordTab.dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String tableName = c.getString(c.getColumnIndex("name"));

                if (!tableName.equals("android_metadata") && !RecordTab.existButtons.contains(tableName)) {

                    RecordTab r = new RecordTab();

                    this.addActivityToShowTab(tableName);
                }

                c.moveToNext();
            }
        }
    }

    private void addActivityToShowTab(String text) {

        Activity act = getActivity();

        /**
         * Get the last row of buttons.
         * Deal with odd and even number of buttons accordingly.
         */

        TableLayout showTable = (TableLayout) act.findViewById(R.id.showButtonsTable);
        TableRow showLastRow = (TableRow) showTable.getChildAt(showTable.getChildCount() - 1);

        if (showLastRow.getChildAt(1).getVisibility() == View.VISIBLE) {
            this.addButtonAsFirst(act, showTable, showLastRow, text);
        } else {
            this.addButtonAsSecond(showLastRow, text);
        }
    }


    private void addButtonAsFirst(Context act, TableLayout theTable, TableRow lastRow, String text) {

        /**
         * Create new row for the table.
         */
        TableRow newRow = new TableRow(act);
        newRow.setLayoutParams(lastRow.getLayoutParams());

        /**
         * Create both buttons. Set the second invisible.
         */
        Button old = (Button) lastRow.getChildAt(0);

        Button left = new Button(act);
        left.setText(text);
        left.setLayoutParams(old.getLayoutParams());

        Button right = new Button(act);
        right.setText("TempButton");
        right.setLayoutParams(old.getLayoutParams());

        right.setVisibility(View.INVISIBLE);

        /**
         * Add OnClickListener and OnLongClickListener
         */

        left.setOnClickListener(this);
        right.setOnClickListener(this);


        /**
         * Add them on.
         */
        newRow.addView(left);
        newRow.addView(right);

        theTable.addView(newRow);
    }

    /**
     * Deal with add activity if there are odd number of buttons.
     *
     * @param theRow The current row that already has 1 button.
     * @param text   The name of the new activity.
     */
    private void addButtonAsSecond(TableRow theRow, String text) {

        /**
         * Get the second button. Set text and visibility.
         */
        Button btn = (Button) theRow.getChildAt(1);

        btn.setText(text);
        btn.setVisibility(View.VISIBLE);
    }

    /**
     * Implement OnClickListener for the buttons.
     *
     * @param v The view been clicked.
     */
    @Override
    public void onClick(View v) {

        /**
         * Get the button text, i.e. name of the database table.
         */
        Button btn = (Button) v;
        String actName = btn.getText().toString();
        String tableName = actName.replace(" ", "");

        /**
         * Initialize the array for the stored times and iterate through.
         */
        ArrayList<String> timeHist = new ArrayList<>();

        SQLiteDatabase db = RecordTab.dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + tableName, null);

        if (c.getCount() > 1) {

            c.moveToFirst();

            while (!c.isAfterLast()) {
                String recordedTime = c.getString(c.getColumnIndex("TIME"));
                timeHist.add(recordedTime);
                c.moveToNext();
            }

            /**
             * Plot the graph.
             */
            this.plotGraph(timeHist, actName);

        } else {
            this.warnNoData(actName);
        }

    }

    private void warnNoData(String actName) {

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
        String warnMessage = getString(R.string.warnNoData, actName);

        /**
         * Get the corresponding view and add the text.
         */
        TextView textView = (TextView) popupView.findViewById(R.id.recordAckText);
        textView.setText(warnMessage);
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

    private void plotGraph(ArrayList<String> timeHist, String actName) {

        /**
         * Use again popup window. Initialize it.
         */
        Activity currentAct = getActivity();

        View buttonsTable = currentAct.findViewById(R.id.showButtonsTable);

        int height = buttonsTable.getMeasuredHeight();
        int width = buttonsTable.getMeasuredWidth();

        View graphView = currentAct.getLayoutInflater().inflate(R.layout.show_graph, null);

        final PopupWindow pw = new PopupWindow(
                graphView,
                width,
                height);

        /**
         * Set the properties.
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
            dataPoints[i] = new DataPoint(this.getDateFromString(dateTime), this.getTimeFromString(dateTime));
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);

        /**
         * Draw the series and add the graph to view.
         */
        graph.addSeries(series);

        /**
         * Set title.
         */
        graph.setTitle("Your recorded history of " + actName + ".");
        graph.setTitleTextSize(55);


        TimeDateFormatter formatter = new TimeDateFormatter(getActivity());
        /**
         * The y-axis.
         */
        double maxY = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        for (int i = 0; i < dataPoints.length; ++i) {
            double thisY = dataPoints[i].getY();

            if (thisY > maxY) {
                maxY = thisY;
            }

            if (thisY < minY) {
                minY = thisY;
            }
        }

        graph.getViewport().setMinY(Math.floor(2 * minY) / 2);
        graph.getViewport().setMaxY(Math.ceil(2 * maxY) / 2);
        graph.getViewport().setYAxisBoundsManual(true);

        /**
         * The x-axis.
         */
        graph.getGridLabelRenderer().setLabelFormatter(formatter);
        graph.getGridLabelRenderer().setNumHorizontalLabels(2);

        graph.getViewport().setMinX(dataPoints[0].getX());
        graph.getViewport().setMaxX(dataPoints[dataPoints.length - 1].getX());
        graph.getViewport().setXAxisBoundsManual(true);

        pw.showAtLocation(graphView, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);

    }

    private Date getDateFromString(String dateTimeString) {

        String dateFormatString = getString(R.string.dateTimeFormat);

        if (dateTimeString.length() != dateFormatString.length()) {
            throw new RuntimeException("Recorded time in database has wrong format. " +
                    "It should be " + dateFormatString);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateTimeFormat));

        try {
            Date date = dateFormat.parse(dateTimeString);

            return (date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Convert a string of date of the form HH:mm:ss to a number between 0 and -25.
     * Want morning to be plotted higher and evenings lower.
     *
     * @param dateTimeString The string representing the time.
     * @return Converted number representing the time.
     */

    private double getTimeFromString(String dateTimeString) {

        String dateFormatString = getString(R.string.dateTimeFormat);

        if (dateTimeString.length() != dateFormatString.length()) {
            throw new RuntimeException("Recorded time in database has wrong format. " +
                    "It should be " + dateFormatString);
        }

        double hour = Double.parseDouble(dateTimeString.substring(11, 13));
        double min = Double.parseDouble(dateTimeString.substring(14, 16));
        double sec = Double.parseDouble(dateTimeString.substring(17, 19));

        double time = -(hour + min / 60 + sec / 3600);

        return (time);
    }
}
