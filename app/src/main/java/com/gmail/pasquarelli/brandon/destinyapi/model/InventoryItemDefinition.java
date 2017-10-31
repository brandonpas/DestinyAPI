package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class InventoryItemDefinition {

    public static final int ITEM_TYPE_NONE = 0;
    public static final int ITEM_TYPE_CURRENCY = 1;
    public static final int ITEM_TYPE_ARMOR = 2;
    public static final int ITEM_TYPE_WEAPON = 3;
    public static final int ITEM_TYPE_MESSAGE = 7;
    public static final int ITEM_TYPE_ENGRAM = 8;
    public static final int ITEM_TYPE_CONSUMABLE = 9;
    public static final int ITEM_TYPE_EXCHANGEMATERIAL = 10;
    public static final int ITEM_TYPE_MISSIONREWARD = 11;
    public static final int ITEM_TYPE_QUESTSTEP = 12;
    public static final int ITEM_TYPE_QUESTSTEPCOMPLETE = 13;
    public static final int ITEM_TYPE_EMBLEM = 14;
    public static final int ITEM_TYPE_QUEST = 15;
    public static final int ITEM_TYPE_SUBCLASS = 16;
    public static final int ITEM_TYPE_CLANBANNER = 17;
    public static final int ITEM_TYPE_AURA = 18;
    public static final int ITEM_TYPE_MOD = 19;

    public static final int ITEM_SUB_TYPE_NONE = 0;
    public static final int ITEM_SUB_TYPE_CRUCIBLE = 1;
    public static final int ITEM_SUB_TYPE_VANGUARD = 2;
    public static final int ITEM_SUB_TYPE_EXOTIC = 5;
    public static final int ITEM_SUB_TYPE_AUTORIFLE = 6;
    public static final int ITEM_SUB_TYPE_SHOTGUN = 7;
    public static final int ITEM_SUB_TYPE_MACHINEGUN = 8;
    public static final int ITEM_SUB_TYPE_HANDCANNON = 9;
    public static final int ITEM_SUB_TYPE_ROCKETLAUNCHER = 10;
    public static final int ITEM_SUB_TYPE_FUSIONRIFLE = 11;
    public static final int ITEM_SUB_TYPE_SNIPERRIFLE = 12;
    public static final int ITEM_SUB_TYPE_PULSERIFLE = 13;
    public static final int ITEM_SUB_TYPE_SCOUTRIFLE = 14;
    public static final int ITEM_SUB_TYPE_CRM = 16;
    public static final int ITEM_SUB_TYPE_SIDEARM = 17;
    public static final int ITEM_SUB_TYPE_SWORD = 18;
    public static final int ITEM_SUB_TYPE_MASK = 19;
    public static final int ITEM_SUB_TYPE_SHADER = 20;

    @SerializedName("displayProperties")
    public DisplayProperties displayProperties;

    @SerializedName("inventory")
    public InventoryProperties inventoryProperties;

    @SerializedName("itemType")
    public int itemType;

    @SerializedName("itemSubType")
    public int itemSubType;

    @SerializedName("stats")
    public InventoryItemStatsDefinition itemStats;

    @SerializedName("computedStats")
    public HashMap<String, ItemStat> itemComputedStats;

    @SerializedName("hash")
    public String hashCode;
}
