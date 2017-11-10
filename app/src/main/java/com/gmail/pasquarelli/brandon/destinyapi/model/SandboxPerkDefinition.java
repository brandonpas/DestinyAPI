package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.google.gson.annotations.SerializedName;

public class SandboxPerkDefinition {

    @SerializedName("displayProperties")
    public DisplayProperties displayProperties;

    @SerializedName("perkIdentifier")
    public String perkIdentifier;

    @SerializedName("hash")
    public String hash;
}
