package com.gmail.pasquarelli.brandon.destinyapi.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * This entity represents the DestinyMilestoneDefinition table provided by Bungie in the
 * 'world_sql_content' database.
 */
@Entity(tableName = DatabaseStructure.CONTENT_MILESTONES_TABLE_NAME)
public class ContentMilestoneEntity {

    @PrimaryKey
    @NonNull
    public int id;

    @ColumnInfo(name = "json", typeAffinity = ColumnInfo.BLOB)
    public byte[] json;

    public Reader getJsonAsStream() {
        return new InputStreamReader(new ByteArrayInputStream(this.json));
    }

    public JsonReader getJsonStream() {
        JsonReader reader = new JsonReader(getJsonAsStream());
        reader.setLenient(true);
        return reader;
    }

    public int getId() {
        return id;
    }
}
