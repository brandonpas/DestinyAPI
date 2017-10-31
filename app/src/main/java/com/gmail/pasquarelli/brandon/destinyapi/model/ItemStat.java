package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.google.gson.annotations.SerializedName;

public class ItemStat {

    @SerializedName("statHash")
    public long statHash;

    @SerializedName("value")
    public int statValue;

    @SerializedName("minimum")
    public int minValue;

    @SerializedName("maximum")
    public int maxValue;

}
