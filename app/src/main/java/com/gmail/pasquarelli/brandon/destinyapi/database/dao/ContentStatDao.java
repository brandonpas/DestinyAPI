package com.gmail.pasquarelli.brandon.destinyapi.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.gmail.pasquarelli.brandon.destinyapi.database.entity.ContentStatEntity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ContentStatDao {

    @Query("SELECT * FROM " + DatabaseStructure.CONTENT_STAT_TABLE_NAME +
    " WHERE json like :likeQuery")
    Single<List<ContentStatEntity>> getStatsByType(String likeQuery);

    @Query("SELECT * FROM " + DatabaseStructure.CONTENT_STAT_TABLE_NAME)
    Single<List<ContentStatEntity>> getAllStats();
}
