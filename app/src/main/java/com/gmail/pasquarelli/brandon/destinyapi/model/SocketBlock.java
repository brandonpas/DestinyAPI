package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the socket information for an InventoryItemDefinition
 */
public class SocketBlock {

    @SerializedName("detail")
    public String detail;

    @SerializedName("socketEntries")
    public SocketEntry[] socketEntries;

    @SerializedName("socketCategories")
    public SocketCategory[] socketCategories;
}
