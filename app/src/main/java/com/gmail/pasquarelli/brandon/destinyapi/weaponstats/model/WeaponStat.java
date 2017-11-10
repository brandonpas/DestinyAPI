package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model;

import android.support.annotation.NonNull;

import com.gmail.pasquarelli.brandon.destinyapi.model.InventoryItemDefinition;

public class WeaponStat {

    private String weaponName;
    private int statValue;
    private int tierType;
    private String weaponHash;

    private WeaponStat() { }

    public WeaponStat(InventoryItemDefinition item, long statHash) {
        statValue = 0;
        weaponName = item.displayProperties.name;

        if (item.inventoryProperties != null)
            tierType = item.inventoryProperties.tierType;

        if (item.itemStats != null && item.itemStats.stats != null &&
                item.itemStats.stats.get(statHash) != null)
            statValue = item.itemStats.stats.get(statHash).statValue;

        weaponHash = item.hashCode;
    }

    public WeaponStat(@NonNull String itemName, int value, int tier) {
        weaponName = itemName;
        statValue = value;
        tierType = tier;
    }

    public String getWeaponName() {
        return weaponName;
    }

    public int getStatValue() {
        return statValue;
    }

    public int getTierType() {
        return tierType;
    }

    public String getWeaponHash() { return weaponHash; }
}
