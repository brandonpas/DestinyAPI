package com.gmail.pasquarelli.brandon.destinyapi.database.milestones.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.gmail.pasquarelli.brandon.destinyapi.database.milestones.entity.ContentMilestoneEntity;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Data Access Object for the {@link ContentMilestoneEntity}.
 * @see ContentMilestoneEntity
 */
@Dao
public interface ContentMilestoneDao {

    /**
     * Get each ContentMilestoneEntity definition object
     */
    @Query("SELECT * FROM " + DatabaseStructure.CONTENT_MILESTONES_TABLE_NAME)
    Single<List<ContentMilestoneEntity>> getMilestoneFromList();
}
