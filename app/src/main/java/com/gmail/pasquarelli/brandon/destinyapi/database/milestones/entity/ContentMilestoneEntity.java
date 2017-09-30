package com.gmail.pasquarelli.brandon.destinyapi.database.milestones.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;

/**
 * This entity represents the DestinyMilestoneDefinition table provided by Bungie in the
 * 'world_sql_content' database. The only time this should be used is when we observe that the
 * 'world_sql_content' database's version has been updated. In such event, we need to drop our
 * own 'app_milestone_definition' table and rebuild it with information from this entity.
 *
 * @see AppMilestoneEntity
 */
@Entity(tableName = DatabaseStructure.CONTENT_MILESTONES_TABLE_NAME)
public class ContentMilestoneEntity {

    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "json")
    public String json;

}
