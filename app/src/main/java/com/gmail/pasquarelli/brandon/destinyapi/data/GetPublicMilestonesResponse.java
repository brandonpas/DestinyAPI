package com.gmail.pasquarelli.brandon.destinyapi.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

/**
 * Object representing JSON response received from calling the 'GetPublicMilestones' API
 */
public class GetPublicMilestonesResponse extends Response {

    @SerializedName("Response")
    Map<String,PublicMilestonesObject> milestones;

    public ArrayList<PublicMilestonesObject> getMilestoneArray() {
        ArrayList<PublicMilestonesObject> returnArray = new ArrayList<>();

        if (milestones == null) {
            return returnArray;
        }

        for (Map.Entry entry : milestones.entrySet()) {
            PublicMilestonesObject milestone = (PublicMilestonesObject) entry.getValue();
            returnArray.add(milestone);
        }
        return returnArray;
    }
}
