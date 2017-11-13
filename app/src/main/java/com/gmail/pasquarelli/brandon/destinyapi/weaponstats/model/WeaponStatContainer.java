package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model;

import android.util.Log;

import com.gmail.pasquarelli.brandon.destinyapi.model.InventoryItemDefinition;

import java.util.ArrayList;
import java.util.List;

public class WeaponStatContainer {

    private String TAG="WSContainer";
    private String statName;
    private Long statHash;
    private ArrayList<WeaponStat> statList;
    private boolean containsNonZeroItem;

    private WeaponStatContainer() { }

    public WeaponStatContainer(String stat, Long hash) {
        statName = stat;
        statList = new ArrayList<>();
        statHash = hash;
        containsNonZeroItem = false;
    }

    public boolean hasNonZeroValueItem() {
        return containsNonZeroItem;
    }

    public int getWeaponListSize() {
        return statList.size();
    }

    public void clearList() {
        statList.clear();
        containsNonZeroItem = false;
    }

    /**
     * Bubble sort of stats list from highest to lowest. Testing took 4 milliseconds to complete for all containers.
     */
    public void bubbleSortList() {
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

    /**
     * Merge sort of stats list from highest to lowest. Testing took 10 milliseconds to complete for all containers.
     */
    public void mergeSort() {
        if (this.statName.equals("Handling"))
            Log.v(TAG, "statList size:" + statList.size());
        List<WeaponStat> sortedList = mergeSplit(statList);

        if (this.statName.equals("Handling"))
            Log.v(TAG, "sortedList size:" + sortedList.size());
        statList.clear();
        statList.addAll(sortedList);
    }

    private List<WeaponStat> mergeSplit(List<WeaponStat> list) {
        if (list.size() <= 1)
            return list;
        int size = list.size();
        int mid = size >>> 1;
        List<WeaponStat> partOne = new ArrayList<>();
        List<WeaponStat> partTwo = new ArrayList<>();

        partOne.addAll(list.subList(0, mid));
        partOne = mergeSplit(partOne);

        partTwo.addAll(list.subList(mid, size));
        partTwo = mergeSplit(partTwo);
        return mergeList(partOne, partTwo);
    }

    private ArrayList<WeaponStat> mergeList(List<WeaponStat> partOne, List<WeaponStat> partTwo) {
        ArrayList<WeaponStat> mergedList = new ArrayList<>();
        while (!partOne.isEmpty() && !partTwo.isEmpty()) {
            if (partOne.get(0).getStatValue() > partTwo.get(0).getStatValue()) {
                mergedList.add(partOne.get(0));
                partOne.remove(0);
            } else {
                mergedList.add(partTwo.get(0));
                partTwo.remove(0);
            }
        }

        while (!partOne.isEmpty()) {
            mergedList.add(partOne.get(0));
            partOne.remove(0);
        }

        while (!partTwo.isEmpty()) {
            mergedList.add(partTwo.get(0));
            partTwo.remove(0);
        }

        return mergedList;
    }

    /**
     * Public call to insert the item into the WeaponStat list
     * @param item Weapon to be inserted into the container
     */
    public void insertToList(InventoryItemDefinition item) {

        if (item.itemStats.stats.get(statHash) != null) {
            int statValue = item.itemStats.stats.get(statHash).statValue;
            if (statValue > 0)
                containsNonZeroItem = true;
            statList.add(new WeaponStat(item, statHash));
        }
        else
            statList.add(new WeaponStat(item, statHash));
    }

    /**
     * Retrieve the WeaponStat list
     * @return The WeaponStat list.
     */
    public ArrayList<WeaponStat> getWeapons() {
        return statList;
    }

    /**
     * Retrieve the name of the stat that this container represents.
     * @return The name of the stat.
     */
    public String getStatName() {
        return statName;
    }

}
