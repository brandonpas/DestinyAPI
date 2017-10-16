package com.gmail.pasquarelli.brandon.destinyapi.api.response_models;

import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.model.PublicMilestoneObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

/**
 * Object representing JSON response received from calling the 'GetPublicMilestones' API
 */
public class PublicMilestonesResponse extends Response {

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

    public ArrayList<Integer> getMilestonesHash() {
        ArrayList<Integer> returnList = new ArrayList<>();
        if (milestones == null) {
            return returnList;
        }

        for (Map.Entry entry : milestones.entrySet()) {
            PublicMilestoneObject milestone = (PublicMilestoneObject) entry.getValue();
            returnList.add(milestone.getSignedHashCode());
        }
        return returnList;
    }
}
