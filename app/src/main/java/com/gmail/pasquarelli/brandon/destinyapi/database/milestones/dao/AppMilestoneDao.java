package com.gmail.pasquarelli.brandon.destinyapi.database.milestones.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.entity.AppMilestoneEntity;

import java.util.List;

/**
 * Data Access Object for our milestone entity.
 */
@Dao
public interface AppMilestoneDao {

    // get milestone details by milestone hash
    @Query("SELECT * FROM " + DatabaseStructure.APP_MILESTONES_TABLE_NAME +
            " WHERE " + DatabaseStructure.Milestones.HASH_CODE + " = :milestoneHash")
    AppMilestoneEntity getMilestone(String milestoneHash);


    // get milestone details for each milestone in list
    @Query("SELECT * FROM " + DatabaseStructure.APP_MILESTONES_TABLE_NAME +
            " WHERE " + DatabaseStructure.Milestones.HASH_CODE + " IN (:milestones)")
    List<AppMilestoneEntity> getMilestoneFromList(List<String> milestones);

}
