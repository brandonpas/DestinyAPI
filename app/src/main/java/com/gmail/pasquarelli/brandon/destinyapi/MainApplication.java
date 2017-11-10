package com.gmail.pasquarelli.brandon.destinyapi;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.crashlytics.android.Crashlytics;
import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseManager;
import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.gmail.pasquarelli.brandon.destinyapi.database.databases.ContentDatabase;

import io.fabric.sdk.android.Fabric;

public class MainApplication extends Application{

    private ContentDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        boolean logCrashes = false;

        if (logCrashes)
            Fabric.with(this, new Crashlytics());


        database = Room.databaseBuilder(this, ContentDatabase.class,
                DatabaseStructure.CONTENT_DB_NAME)
                .addMigrations(DatabaseManager.MIGRATION_2_3)
                .addMigrations(DatabaseManager.MIGRATION_3_4)
                .addMigrations(DatabaseManager.MIGRATION_4_5)
                .build();
    }

    public ContentDatabase getDatabase() {
        return database;
    }
}
