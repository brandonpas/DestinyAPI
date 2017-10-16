package com.gmail.pasquarelli.brandon.destinyapi.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.gmail.pasquarelli.brandon.destinyapi.database.entity.ContentMilestoneEntity;

import java.util.List;

import io.reactivex.Single;

/**
 * Data Access Object for the {@link ContentMilestoneEntity}.
 * @see ContentMilestoneEntity
 */
@Dao
public interface ContentMilestoneDao {

    /**
     * Get each ContentMilestoneEntity definition object from a list.
     * @param milestone A signed 32 bit Integer representing the milestone hashcode
     * @return The Milestone.
     */
    @Query("SELECT * FROM " + DatabaseStructure.CONTENT_MILESTONES_TABLE_NAME +
            " WHERE " + DatabaseStructure.COLUMN_ID + " = :milestone")
    ContentMilestoneEntity getMilestone(Integer milestone);

    /**
     * Get each ContentMilestoneEntity definition object from a list.
     * @param milestones A List of signed 32 bit Integers representing the milestone hashcode
     * @return List milestones.
     */
    @Query("SELECT * FROM " + DatabaseStructure.CONTENT_MILESTONES_TABLE_NAME +
            " WHERE " + DatabaseStructure.COLUMN_ID + " IN (:milestones)")
    List<ContentMilestoneEntity> getMilestoneFromList(List<Integer> milestones);

    /**
     * Get each ContentMilestoneEntity definition object from a list.
     * @param milestones A List of signed 32 bit Integers representing the milestone hashcode
     * @return An observable of the list milestones.
     */
    @Query("SELECT * FROM " + DatabaseStructure.CONTENT_MILESTONES_TABLE_NAME +
            " WHERE " + DatabaseStructure.COLUMN_ID + " IN (:milestones)")
    Single<List<ContentMilestoneEntity>> getMilestoneFromListAsync(List<Integer> milestones);

}
