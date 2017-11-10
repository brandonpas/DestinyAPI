package com.gmail.pasquarelli.brandon.destinyapi.database.databases;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.gmail.pasquarelli.brandon.destinyapi.database.dao.ContentInventoryItemDao;
import com.gmail.pasquarelli.brandon.destinyapi.database.dao.ContentMilestoneDao;
import com.gmail.pasquarelli.brandon.destinyapi.database.dao.ContentSandboxPerkDao;
import com.gmail.pasquarelli.brandon.destinyapi.database.dao.ContentStatDao;
import com.gmail.pasquarelli.brandon.destinyapi.database.entity.ContentInventoryItemEntity;
import com.gmail.pasquarelli.brandon.destinyapi.database.entity.ContentMilestoneEntity;
import com.gmail.pasquarelli.brandon.destinyapi.database.entity.ContentSandboxPerkEntity;
import com.gmail.pasquarelli.brandon.destinyapi.database.entity.ContentStatEntity;
import com.gmail.pasquarelli.brandon.destinyapi.model.InventoryItemDefinition;

@Database(entities = {
        ContentMilestoneEntity.class,
        ContentInventoryItemEntity.class,
        ContentStatEntity.class,
        ContentSandboxPerkEntity.class},
        version = 5,
        exportSchema = false)
public abstract class ContentDatabase extends RoomDatabase {
    public abstract ContentMilestoneDao contentMilestoneDao();
    public abstract ContentInventoryItemDao contentInventoryItemDao();
    public abstract ContentStatDao contentStatDao();
    public abstract ContentSandboxPerkDao contentSandboxPerkDao();
}
