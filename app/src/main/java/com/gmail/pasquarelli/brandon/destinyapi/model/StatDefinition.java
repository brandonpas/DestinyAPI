package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.google.gson.annotations.SerializedName;

public class StatDefinition {

    @SerializedName("displayProperties")
    public DisplayProperties displayProperties;

    @SerializedName("hasComputedBlock")
    public boolean hasComputedBlock;

    @SerializedName("hash")
    public Long hash;
}
