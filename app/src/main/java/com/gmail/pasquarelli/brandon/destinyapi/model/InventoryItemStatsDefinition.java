package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class InventoryItemStatsDefinition {

    @SerializedName("statsGroupHash")
    public int statsGroupHash;

    @SerializedName("stats")
    public HashMap<Long, ItemStat> stats;
}
