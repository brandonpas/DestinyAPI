package com.gmail.pasquarelli.brandon.destinyapi.database.databases;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.entity.AppMilestoneEntity;
import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.dao.AppMilestoneDao;

@Database(entities = {
        AppMilestoneEntity.class },
        version = 2,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppMilestoneDao appMilestoneDao();
}
