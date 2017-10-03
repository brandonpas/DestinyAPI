package com.gmail.pasquarelli.brandon.destinyapi.database.milestones.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.google.gson.annotations.SerializedName;

/**
 * This entity/table will need to be created/re-created each time the content database
 * version is updated. This is a custom table derived from the content table which allows
 * us faster access as the primary key will be the milestone hashcode.
 */
@Entity(tableName = DatabaseStructure.APP_MILESTONES_TABLE_NAME,
        indices = {@Index(DatabaseStructure.Milestones.HASH_CODE)})
public class AppMilestoneEntity {

    @PrimaryKey
    @SerializedName("hash")
    public String hashCode;

    @ColumnInfo(name = "friendlyName")
    @SerializedName("friendlyName")
    public String friendlyName;

    @Embedded
    @SerializedName("displayProperties")
    public DisplayProperties displayProperties;

    public String getName() {
        if (displayProperties != null) {
            return this.displayProperties.name;
        } else if (friendlyName != null) {
            return friendlyName;
        } else {
            return hashCode;
        }
    }

    public String getDescription() {
        if (displayProperties != null) {
            return displayProperties.description;
        } else {
            return "";
        }
    }

    public String getIconUrl() {
        if (displayProperties != null) {
            return "http://www.bungie.net" + displayProperties.iconUrl;
        } else {
            return null;
        }
    }

    public static class DisplayProperties {

        @ColumnInfo(name = "name")
        public String name;

        @ColumnInfo(name = "description")
        public String description;

        @ColumnInfo(name = "icon")
        @SerializedName("icon")
        public String iconUrl;
    }

}
