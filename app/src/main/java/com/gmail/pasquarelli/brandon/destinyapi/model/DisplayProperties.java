package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the DisplayProperties JSON object for definitions in the Manifest
 */
public class DisplayProperties {

    @SerializedName("description")
    public String description;

    @SerializedName("name")
    public String name;

    @SerializedName("icon")
    public String iconUrl;

    @SerializedName("hasIcon")
    public boolean hasIcon;

    public String getIconUrl() {
        if (iconUrl == null)
            return null;
        return "https://www.bungie.net" + iconUrl;
    }

}
