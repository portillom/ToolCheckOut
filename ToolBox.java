package com.michaelportillo.android.toolcheckout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.michaelportillo.android.toolcheckout.database.ToolBaseHelper;
import com.michaelportillo.android.toolcheckout.database.ToolCursorWrapper;
import com.michaelportillo.android.toolcheckout.database.ToolDbSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by USER on 12/4/18.
 */

public class ToolBox {

    private static ToolBox sToolBox;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    /**
     * Setting up a singleton
     * @param context
     * @return
     */
    public static ToolBox get(Context context){
        if(sToolBox == null){
            sToolBox = new ToolBox(context);
        }
        return sToolBox;
    }

    /**
     * When getWritableDatabase() gets called, ToolBaseHelper will do the following:
     * (1) Open up /data/data/com.michaelportillo.android.toolcheckout/databases/toolBase.db,
     * creating a new database file if it does not already exist.
     * (2) If this is the first time the database has been created, call onCreate(SQLiteDatabase),
     * then save out the latest version number.
     * (3) If this is not the first time, check the version number in the database. If the version
     * number in ToolBaseHelper is higher, call onUpgrade(SQLiteDatabase, int, int).
     * @param context
     */
    private ToolBox(Context context){
        mContext = context.getApplicationContext();     //d.4
        mDatabase = new ToolBaseHelper(mContext).getWritableDatabase();     //d.4
    }

    /**
     * The insert(String, String, ContentValues) method has two important arguments and one that is rarely used.
     * 1st argument - is the table you want to insert into, ToolTable.NAME
     * 3rd argument - is the data you want to put in.
     * 2nd argument - is called nullColumnHack. This will not be used in this app       d.7
     * @param t
     */
    public void addTool(Tool t){
        ContentValues values = getContentValues(t);
        mDatabase.insert(ToolDbSchema.ToolTable.NAME, null, values);
    }

    public void removeTool(Tool t){
        mDatabase.delete(ToolDbSchema.ToolTable.NAME, ToolDbSchema.ToolTable.Cols.UUID + " = ?",
                new String[]{t.getId().toString()});
    }

    /**
     * This method will query for all tools, walk the cursor, and populate a Tool list
     * Very important to close your cursors.        d.16
     *
     * @return tools arrayList of Tool objects
     */
    public List<Tool> getTools(){
        List<Tool> tools = new ArrayList<>();

        ToolCursorWrapper cursor = queryTools(null, null);

        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                tools.add(cursor.getTool());
                cursor.moveToNext();
            }
        }finally{
            cursor.close();
        }

        return tools;
    }

    /**
     * d.17 , (don't forget to d.18 add setTools(List<Tool> tools){mTool = tools} & adapter in TListFragment)
     * @param id
     * @return
     */
    public Tool getTool(UUID id){
        ToolCursorWrapper cursor = queryTools(
                ToolDbSchema.ToolTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );
        try{
            if(cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getTool();

        }finally{
            cursor.close();
        }
    }

    public File getPhotoFile(Tool tool){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, tool.getPhotoFileName());
    }

    /**
     * The update(String, ContentValues, String, String[]) method starts off similarly to
     * insert(...) - you pass in the table name you want to update and the ContentValues you want
     * to assign to each row you update.
     *
     * The last row you want to specify which rows get updated.
     * You build a where clause (3rd argument) and then specifying values for the arguments in the
     * where clause (the final String[] array).         d.8
     *
     * @param tool
     */
   public void updateTool(Tool tool){
        String uuidString = tool.getId().toString();
        ContentValues values = getContentValues(tool);

        mDatabase.update(ToolDbSchema.ToolTable.NAME, values,
                ToolDbSchema.ToolTable.Cols.UUID + " = ?",
                new String[] {uuidString});

   }

    /**
     *Querying for Tool objects
     * This will wrap the cursor you get back from query in a ToolCursorWrapper.
     * d.10, d.15(replace Cursor with CursorWrapper)
     *
     * @param whereClause
     * @param whereArgs
     * @return cursor - it gives you raw column values
     */
   private ToolCursorWrapper queryTools(String whereClause, String[] whereArgs){
       Cursor cursor = mDatabase.query(
               ToolDbSchema.ToolTable.NAME,
               null, //columns - null selects all columns
               whereClause,
               whereArgs,
               null, //groupBy
               null, //having
               null // orderBy
       );
       return new ToolCursorWrapper(cursor);
   }

    /**
     * Don't forget to add import for the database.
     * This method will be used to store the kinds of data SQLite can hold.
     * This will shuttle a Tool object into a ContentValues.        d.6
     *
     * @param tool
     * @return the ContentValues
     */
    private static ContentValues getContentValues(Tool tool){
        ContentValues values = new ContentValues();
        values.put(ToolDbSchema.ToolTable.Cols.UUID, tool.getId().toString());
        values.put(ToolDbSchema.ToolTable.Cols.TOOLNAME, tool.getToolName());
        values.put(ToolDbSchema.ToolTable.Cols.DATE, tool.getDate().getTime());
        values.put(ToolDbSchema.ToolTable.Cols.RETURNED, tool.isReturned() ? 1 : 0);
        values.put(ToolDbSchema.ToolTable.Cols.CONTACT, tool.getContact());
        values.put(ToolDbSchema.ToolTable.Cols.RETURNDATE, tool.getReturnDate().getTime());

        return values;
    }
}
