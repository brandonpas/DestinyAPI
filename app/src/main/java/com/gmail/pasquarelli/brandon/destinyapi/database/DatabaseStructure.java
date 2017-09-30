package com.gmail.pasquarelli.brandon.destinyapi.database;

public class DatabaseStructure {

    private static final String CONTENT_PREFIX = "content_";
    private static final String APP_PREFIX = "app_";

    // Generic table names
    private static final String MILESTONE_TABLE_NAME = "milestone_definition";

    // Content Database
    public static final String CONTENT_DB_NAME = CONTENT_PREFIX + "database.db";

    // Content Tables
    public static final String CONTENT_MILESTONES_TABLE_NAME = CONTENT_PREFIX + MILESTONE_TABLE_NAME;


    // App Database
    public static final String APP_DB_NAME = APP_PREFIX + "database.db";

    // App Tables
    public static final String APP_MILESTONES_TABLE_NAME = APP_PREFIX + MILESTONE_TABLE_NAME;


    // Columns
    public class Milestones {
        public static final String HASH_CODE = "hashCode";
    }

}
