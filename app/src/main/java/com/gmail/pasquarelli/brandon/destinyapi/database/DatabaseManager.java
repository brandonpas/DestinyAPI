package com.gmail.pasquarelli.brandon.destinyapi.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

public class DatabaseManager {

    static final Migration persistDatabaseProvided = new Migration(1, 1) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Don't do anything. This is to prevent Room from dropping our data
            // provided in the
        }
    };

}
