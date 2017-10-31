package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model;

import android.support.annotation.NonNull;

public class WeaponStat {

    private String weaponName;
    private int statValue;

    private WeaponStat() { }

    public WeaponStat(@NonNull String itemName, int value) {
        weaponName = itemName;
        statValue = value;
    }

    public String getWeaponName() {
        return weaponName;
    }

    public int getStatValue() {
        return statValue;
    }
}
