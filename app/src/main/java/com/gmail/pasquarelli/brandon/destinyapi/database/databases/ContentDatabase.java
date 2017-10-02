package com.gmail.pasquarelli.brandon.destinyapi.database.databases;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.dao.ContentMilestoneDao;
import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.entity.ContentMilestoneEntity;

@Database(entities = {
        ContentMilestoneEntity.class },
        version = 2,
        exportSchema = false)
public abstract class ContentDatabase extends RoomDatabase {
    public abstract ContentMilestoneDao contentMilestoneDao();
}
