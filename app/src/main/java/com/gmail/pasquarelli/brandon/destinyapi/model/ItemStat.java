package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.google.gson.annotations.SerializedName;

public class ItemStat {

    public ItemStat(long hash) {
        statHash = hash;
        statValue = 0;
        minValue = 0;
        maxValue = 0;
    }

    @SerializedName("statHash")
    public long statHash;

    @SerializedName("value")
    public int statValue;

    @SerializedName("minimum")
    public int minValue;

    @SerializedName("maximum")
    public int maxValue;

}
