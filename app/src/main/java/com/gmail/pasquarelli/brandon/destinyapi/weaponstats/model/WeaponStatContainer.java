package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model;

import com.gmail.pasquarelli.brandon.destinyapi.model.InventoryItemDefinition;

import java.util.ArrayList;

public class WeaponStatContainer {

    private String statName;
    private Long statHash;
    private ArrayList<WeaponStat> statList;

    private WeaponStatContainer() { }

    public WeaponStatContainer(String stat, Long hash) {
        statName = stat;
        statList = new ArrayList<>();
        statHash = hash;
    }

    public void setStatList(ArrayList<WeaponStat> list) {
        statList.clear();
        statList.addAll(list);
    }

    public int getWeaponListSize() {
        return statList.size();
    }

    public void clearList() {
        statList.clear();
    }

    /**
     * Bubble sort for now. I'd like to improve this later at some point.
     */
    public void sortList() {
        for (int x= 0; x < statList.size()-1; x++) {
            for (int y= 0; y < statList.size()-1; y++) {
                if (statList.get(y).getStatValue() < statList.get(y+1).getStatValue()) {
                    WeaponStat temp = statList.get(y+1);
                    statList.set(y+1, statList.get(y));
                    statList.set(y, temp);
                }
            }
        }
    }

    public void insertDescendingOrder(InventoryItemDefinition item) {
//        if (statList.size() == 0) {
//            statList.add(new WeaponStat(item.displayProperties.name,
//                    item.itemStats.stats.get(statHash).statValue));
//            return;
//        }
        if (item.itemStats.stats.get(statHash) == null) {
            statList.add(new WeaponStat(item.displayProperties.name,0));
        } else {
            statList.add(new WeaponStat(item.displayProperties.name,
                    item.itemStats.stats.get(statHash).statValue));
        }
    }

    public ArrayList<WeaponStat> getWeapons() {
        return statList;
    }

    public String getStatName() {
        return statName;
    }

}
