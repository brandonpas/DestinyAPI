package com.gmail.pasquarelli.brandon.destinyapi.database.milestones.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.entity.AppMilestoneEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Data Access Object for our milestone entity.
 */
@Dao
public interface AppMilestoneDao {

    @Query("SELECT * FROM " + DatabaseStructure.APP_MILESTONES_TABLE_NAME +
            " WHERE " + DatabaseStructure.Milestones.HASH_CODE + " = :milestoneHash")
    AppMilestoneEntity getMilestone(String milestoneHash);


    @Query("SELECT * FROM " + DatabaseStructure.APP_MILESTONES_TABLE_NAME +
            " WHERE " + DatabaseStructure.Milestones.HASH_CODE + " IN (:milestones)")
    List<AppMilestoneEntity> getMilestoneFromList(List<String> milestones);

    @Query("SELECT * FROM " + DatabaseStructure.APP_MILESTONES_TABLE_NAME)
    Single<List<AppMilestoneEntity>> getAllMilestones();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMilestone(AppMilestoneEntity milestone);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMilestoneList(List<AppMilestoneEntity> milestoneList);
}
