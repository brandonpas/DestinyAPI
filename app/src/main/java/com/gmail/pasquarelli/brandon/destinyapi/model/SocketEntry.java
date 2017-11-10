package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.gmail.pasquarelli.brandon.destinyapi.utils.Conversions;
import com.google.gson.annotations.SerializedName;

/**
 * Represents an individual Socket within a SocketBlock
 */
public class SocketEntry {

    @SerializedName("socketTypeHash")
    public long unsignedSocketTypeHash;

    @SerializedName("singleInitialItemHash")
    public String unsignedSocketHash;

    public int getSocketHash() {
        return Conversions.parseUnsignedInt(unsignedSocketHash);
    }
}
