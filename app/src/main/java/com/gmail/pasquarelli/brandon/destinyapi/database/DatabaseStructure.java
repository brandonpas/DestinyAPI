package com.gmail.pasquarelli.brandon.destinyapi.database;

public class DatabaseStructure {

    private static final String CONTENT_PREFIX = "content_";


    // Content Database
    public static final String CONTENT_DB_NAME = CONTENT_PREFIX + "database.db";

    // Content Tables
    public static final String CONTENT_MILESTONES_TABLE_NAME = "DestinyMilestoneDefinition";

    // Columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_JSON = "json";

}
