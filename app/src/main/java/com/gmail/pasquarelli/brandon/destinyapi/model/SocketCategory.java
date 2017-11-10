package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.google.gson.annotations.SerializedName;

public class SocketCategory {

    // This is probably volatile, just adding here for now to get the weapon perk filter working.
    // But this will need to be removed and a Dao/Entity created for the DestinySocketCategoryDefinition
    // table to query for the 'WEAPON PERKS' category name.
    public static final String WEAPON_PERK_CATEGORY_HASH = "4241085061";

    @SerializedName("socketCategoryHash")
    public String socketCategoryHash;

    /**
     * The position in the SocketBlock.socketEntries array for each Socket
     * of this category.
     */
    @SerializedName("socketIndexes")
    public int[] socketEntryIndices;
}
