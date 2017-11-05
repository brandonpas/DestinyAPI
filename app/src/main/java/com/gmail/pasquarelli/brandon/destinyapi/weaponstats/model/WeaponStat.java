package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model;

import android.support.annotation.NonNull;

public class WeaponStat {

    private String weaponName;
    private int statValue;
    private int tierType;

    private WeaponStat() { }

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
}
