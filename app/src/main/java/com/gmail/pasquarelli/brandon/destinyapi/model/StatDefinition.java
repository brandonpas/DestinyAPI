package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.google.gson.annotations.SerializedName;

public class StatDefinition {

    public static final int AGGREGATE_TYPE_CHARACTER_AVERAGE = 0;
    public static final int AGGREGATE_TYPE_CHARACTER = 1;
    public static final int AGGREGATE_TYPE_ITEM = 2;

    @SerializedName("displayProperties")
    public DisplayProperties displayProperties;

    @SerializedName("hasComputedBlock")
    public boolean hasComputedBlock;

    @SerializedName("aggregationType")
    public int aggregationType;

    @SerializedName("hash")
    public Long hash;
}
