package com.michaelportillo.android.toolcheckout.database;

/**
 * Created by USER on 12/4/18.
 */

public class ToolDbSchema {

    /**
     * This defines the name of the table to be used.
     *
     * The ToolDbSchema class only exists to define the String constants needed to describe the
     * moving pieces of the table definition. The first piece of that definition is th name of the
     * table in the database, ToolTable.NAME        d.1
     *
     */
    public static final class ToolTable{
        public static final String NAME = "tools";

        /**
         * This defines the name of the table columns. d.2
         *
         */
        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TOOLNAME = "toolname";
            public static final String DATE = "date";
            public static final String RETURNED = "returned";
            public static final String CONTACT = "contact";
            public static final String RETURNDATE = "returndate";

        }
    }
}
