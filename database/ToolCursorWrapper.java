package com.michaelportillo.android.toolcheckout.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.michaelportillo.android.toolcheckout.Tool;

import java.util.Date;
import java.util.UUID;

/**
 * Created by USER on 12/5/18.
 */

public class ToolCursorWrapper extends CursorWrapper {
    /**
     * This lets you wrap a Cursor you received from another place and add new methods on top of it.
     * @param cursor
     * d.11
     */
    public ToolCursorWrapper(Cursor cursor){
        super(cursor);
    }

    /**
     * This method will pull out relevant column data.
     * You will need to return a Tool object with an appropriate UUID from this method, thus
     * go ahead and add another constructor in the Tool class.      d.12, d.14
     * @return
     */
    public Tool getTool(){
        String uuidString = getString(getColumnIndex(ToolDbSchema.ToolTable.Cols.UUID));
        String toolName = getString(getColumnIndex(ToolDbSchema.ToolTable.Cols.TOOLNAME));
        long date = getLong(getColumnIndex(ToolDbSchema.ToolTable.Cols.DATE));
        int isReturned = getInt(getColumnIndex(ToolDbSchema.ToolTable.Cols.RETURNED));
        String contact = getString(getColumnIndex(ToolDbSchema.ToolTable.Cols.CONTACT));
        long returnDate = getLong(getColumnIndex(ToolDbSchema.ToolTable.Cols.RETURNDATE));

        Tool tool = new Tool(UUID.fromString(uuidString));
        tool.setToolName(toolName);
        tool.setDate(new Date(date));
        tool.setReturned(isReturned != 0);
        tool.setContact(contact);
        tool.setReturnDate(new Date(returnDate));

        return tool;
    }
}
