package com.michaelportillo.android.toolcheckout.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.michaelportillo.android.toolcheckout.database.ToolDbSchema.ToolTable;

/**
 * Created by USER on 12/4/18.
 */

public class ToolBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "toolBase.db";

    /**
     * Android provides the SQLiteOpenHelper class to:
     * (1)Check to see if the database already exists, (2) if not then create it and create tables and
     * initial data it needs.
     * (3) If it does, open it up and see what version of the ToolDbSchema it has (you may want to add
     * or remove things in future versions of ToolCheckOut)
     * (4) If it is an old version, upgrade it to a newer version.      d.3
     * @param context
     */
    public ToolBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * Don't forget to cursor over the word ToolTable and key in Option+Return (Alt+Enter).
     * Then select the first item, add import ..  This will allow you to refer to the String constants
     * in ToolDbSchema.ToolTable.
     *
     * Create the tool table        d.5
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + ToolTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ToolTable.Cols.UUID + ", " +
                ToolTable.Cols.TOOLNAME + ", " +
                ToolTable.Cols.DATE + ", " +
                ToolTable.Cols.RETURNED + ", " +
                ToolTable.Cols.CONTACT + " ," +
                ToolTable.Cols.RETURNDATE +
                ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

}
