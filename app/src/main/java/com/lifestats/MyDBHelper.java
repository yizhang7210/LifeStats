package com.lifestats;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The Database Helper.
 */
public class MyDBHelper extends SQLiteOpenHelper {

    /**
     * Initialize the various database variables.
     */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ActivitiesDB";
    private static final String[] DEFAULT_TABLES =
            {"Wakeup", "Gotobed", "Breakfast", "Lunch", "Dinner", "Workout", "Class"};

    /**
     * Constructor.
     * @param context Context.
     */
    public MyDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * On create, create the default tables we'll be using.
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        for(String act : DEFAULT_TABLES){
            db.execSQL(this.getCreateTableCommand(act));
        }

    }

    /**
     * On upgrade. Throw everything out and start over.
     * @param db The database.
     * @param oldVersion The older version.
     * @param newVersion The newer version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        for(String act : DEFAULT_TABLES){
            db.execSQL("DROP TABLE IF EXISTS " + act);
        }

        this.onCreate(db);
    }

    /**
     * Helper for generating the SQL commands for creating tables.
     * @param tableName The name of the table being created.
     * @return The SQL command for creating that table.
     */
    public String getCreateTableCommand(String tableName){
        String command = "CREATE TABLE "+tableName + "(TIME TEXT);";
        return(command);
    }
}
