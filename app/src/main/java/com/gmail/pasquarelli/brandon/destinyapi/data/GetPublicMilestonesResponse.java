package com.gmail.pasquarelli.brandon.destinyapi.data;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GetPublicMilestonesResponse {

    @SerializedName("Response")
    Map<String,PublicMilestonesObject> milestones;

}
