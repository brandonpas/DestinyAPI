package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.google.gson.annotations.SerializedName;

public class InventoryProperties {

    public static int TIER_TYPE_UNKNOWN = 0;
    public static int TIER_TYPE_CURRENCY = 1;
    public static int TIER_TYPE_BASIC = 2;
    public static int TIER_TYPE_COMMON = 3;
    public static int TIER_TYPE_RARE = 4;
    public static int TIER_TYPE_SUPERIOR = 5;
    public static int TIER_TYPE_EXOTIC = 6;

    @SerializedName("tierType")
    public int tierType;
}
