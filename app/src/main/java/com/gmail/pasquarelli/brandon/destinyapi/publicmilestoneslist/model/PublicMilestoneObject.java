package com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.model;

import com.gmail.pasquarelli.brandon.destinyapi.utils.Conversions;
import com.google.gson.annotations.SerializedName;

/**
 * Object representing JSON object for each 'Milestone' that is received
 * when calling the 'GetPublicMilestones' API
 */
public class PublicMilestoneObject {

    @SerializedName("milestoneHash")
    String milestoneHash;

    @SerializedName("startDate")
    String startDate;

    @SerializedName("endDate")
    String endDate;

    public String getMilestoneHash() {
        return milestoneHash;
    }

    public int getSignedHashCode() {
        return Conversions.parseUnsignedInt(milestoneHash);
    }

    public void setMilestoneHash(String milestoneHash) {
        this.milestoneHash = milestoneHash;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
