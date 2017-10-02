package com.gmail.pasquarelli.brandon.destinyapi.database;

public class DatabaseStructure {

    private static final String CONTENT_PREFIX = "content_";
    private static final String APP_PREFIX = "app_";


    // Content Database
    public static final String CONTENT_DB_NAME = CONTENT_PREFIX + "database.db";

    // Content Tables
    public static final String CONTENT_MILESTONES_TABLE_NAME = "DestinyMilestoneDefinition";


    // App Database
    public static final String APP_DB_NAME = APP_PREFIX + "database.db";

    // App Tables
    public static final String APP_MILESTONES_TABLE_NAME = APP_PREFIX + "milestone_definition";


    // Columns
    public class Milestones {
        public static final String HASH_CODE = "hashCode";
    }

}
