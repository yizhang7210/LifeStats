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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * MyDBHelper: The Database Helper dealing with read and write from database.
 */
public class MyDBHelper extends SQLiteOpenHelper {

    public static final String[] DEFAULT_TABLES =
            {"Wake_up", "Go_to_bed", "Breakfast", "Lunch", "Dinner", "Workout"};
    /**
     * Initialize the various database variables.
     */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ActivitiesDB";

    /**
     * Constructor.
     *
     * @param context Context.
     */
    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * On create, create the default tables we'll be using.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        for (String act : DEFAULT_TABLES) {
            db.execSQL(this.getCreateTableCommand(act));
        }

    }

    /**
     * On upgrade. Throw everything out and start over.
     *
     * @param db         The database.
     * @param oldVersion The older version.
     * @param newVersion The newer version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        for (String act : DEFAULT_TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + act);
        }

        this.onCreate(db);
    }

    /**
     * Helper for generating the SQL commands for creating tables.
     *
     * @param tableName The name of the table being created.
     * @return The SQL command for creating that table.
     */
    public String getCreateTableCommand(String tableName) {
        String command = "CREATE TABLE IF NOT EXISTS " + tableName + "(TIME TEXT);";
        return (command);
    }
}
