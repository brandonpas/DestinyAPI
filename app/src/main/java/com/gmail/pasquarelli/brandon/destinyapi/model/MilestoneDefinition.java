package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Represents a Complete Milestone object with all quests/activities, etc.
 */
public class MilestoneDefinition {

    @SerializedName("hash")
    public String hashCode;

    @SerializedName("friendlyName")
    public String friendlyName;

    @SerializedName("displayProperties")
    public DisplayProperties displayProperties = new DisplayProperties();

    @SerializedName("quests")
    HashMap<String, Quest> quests;
}
