package com.gmail.pasquarelli.brandon.destinyapi.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

/**
 * Object representing JSON response received from calling the 'GetPublicMilestones' API
 */
public class GetPublicMilestonesResponse extends Response {

    @SerializedName("Response")
    Map<String,PublicMilestoneObject> milestones;

    /**
     * Convert the milestones Map object to an ArrayList
     * @return ArrayList of PublicMilestoneObject
     */
    public ArrayList<PublicMilestoneObject> getMilestoneArray() {
        ArrayList<PublicMilestoneObject> returnArray = new ArrayList<>();

        if (milestones == null) {
            return returnArray;
        }

        for (Map.Entry entry : milestones.entrySet()) {
            PublicMilestoneObject milestone = (PublicMilestoneObject) entry.getValue();
            returnArray.add(milestone);
        }
        return returnArray;
    }
}
