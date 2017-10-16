package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a Quest JSON object
 */
public class Quest {

    @SerializedName("questItemHash")
    public String questItemHash;

    @SerializedName("displayProperties")
    public DisplayProperties displayProps;

    @SerializedName("overrideImage")
    public String overrideImage;

    public String getQuestItemHash() {
        return questItemHash;
    }

    public void setQuestItemHash(String questItemHash) {
        this.questItemHash = questItemHash;
    }

    public DisplayProperties getDisplayProps() {
        return displayProps;
    }

    public void setDisplayProps(DisplayProperties displayProps) {
        this.displayProps = displayProps;
    }

    public String getOverrideImage() {
        return overrideImage;
    }

    public void setOverrideImage(String overrideImage) {
        this.overrideImage = overrideImage;
    }
}
