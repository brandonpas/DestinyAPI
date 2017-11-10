package com.gmail.pasquarelli.brandon.destinyapi.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.gmail.pasquarelli.brandon.destinyapi.database.entity.ContentSandboxPerkEntity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ContentSandboxPerkDao {

    @Query("SELECT * FROM " + DatabaseStructure.CONTENT_SANDBOX_PERK_TABLE_NAME)
    Single<List<ContentSandboxPerkEntity>> getAllPerks();
}
