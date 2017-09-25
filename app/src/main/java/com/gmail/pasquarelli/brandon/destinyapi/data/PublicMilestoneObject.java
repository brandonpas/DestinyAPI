package com.gmail.pasquarelli.brandon.destinyapi.data;

import com.google.gson.annotations.SerializedName;

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
