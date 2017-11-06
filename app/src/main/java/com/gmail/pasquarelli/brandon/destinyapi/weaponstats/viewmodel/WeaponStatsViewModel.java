package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.database.Cursor;
import android.util.Log;

import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.gmail.pasquarelli.brandon.destinyapi.database.QueryHelper;
import com.gmail.pasquarelli.brandon.destinyapi.database.databases.ContentDatabase;
import com.gmail.pasquarelli.brandon.destinyapi.database.entity.ContentStatEntity;
import com.gmail.pasquarelli.brandon.destinyapi.model.InventoryItemDefinition;
import com.gmail.pasquarelli.brandon.destinyapi.model.InventoryProperties;
import com.gmail.pasquarelli.brandon.destinyapi.model.ItemStat;
import com.gmail.pasquarelli.brandon.destinyapi.model.StatDefinition;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model.WeaponStatContainer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class WeaponStatsViewModel extends ViewModel {

    private String TAG = "WeaponStatVM";
    private MutableLiveData<ArrayList<WeaponStatContainer>> statsList;
    private MutableLiveData<Boolean> containersInitialized;
    private HashMap<Long,WeaponStatContainer> statContainerByHash;
    private HashMap<String,Boolean> hiddenStats;

    public MutableLiveData<ArrayList<WeaponStatContainer>> getWeaponStats() {
        if (statsList == null) {
            statsList = new MutableLiveData<>();
        }
        return statsList;
    }

    public MutableLiveData<Boolean> getContainersInitialized() {
        if (containersInitialized == null) {
            containersInitialized = new MutableLiveData<>();
        }
        return containersInitialized;
    }

    /**
     * Obtain the current number of containers in the list.
     * @return Integer representing the number of weapon stat containers.
     */
    public int getStatsListCount() {
        if (statsList.getValue() == null)
            return 0;
        else
            return statsList.getValue().size();
    }

    /**
     * Obtain the WeaponStatContainer at the designated position.
     * @param position Position in list
     * @return The WeaponStatContainer
     */
    public WeaponStatContainer getWeaponStatAt(int position) {
        if (statsList.getValue() == null)
            return null;
        else
            return statsList.getValue().get(position);
    }

    public ArrayList<WeaponStatContainer> getWeaponStatArray() {
        if (statsList == null)
            return null;
        return statsList.getValue();
    }

    /**
     * Clear the weapon list in each WeaponStatContainer
     */
    void clearAllContainers() {
        if (statContainerByHash == null)
            return;

        for (Map.Entry entry : statContainerByHash.entrySet()) {
            WeaponStatContainer container = (WeaponStatContainer) entry.getValue();
            container.clearList();
        }
    }

    /**
     * Asynchronously query all the stats in the Content database provided by Bungie.
     * Then initialize the WeaponStatContainer hashmap.
     * @param db A reference to the Room database instance.
     */
    public void queryStats(ContentDatabase db) {
        db.contentStatDao()
                .getAllStats()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<ContentStatEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onSuccess(List<ContentStatEntity> contentStatEntity) {
                        Log.v(TAG,"initStatHashMap onSuccess");
                        Log.v(TAG,"contentStatEntity: " + contentStatEntity.size());
                        initStatsHashMap(contentStatEntity);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG,"initStatHashMap onError");
                        e.printStackTrace();
                    }
                });
    }

    /**
     * Initialize the list of stats that should not have a WeaponStatContainer.
     * The hashcode for the StatDefinition can change with each database version,
     * so build this list according to the display name.
     */
    private void initHiddenStats() {
        hiddenStats.put("Inventory Size", true);
        hiddenStats.put("Recoil Direction", true);
        hiddenStats.put("Attack", true);
    }

    /**
     * Asyncrhonously build the HashMap using the stat's hashcode as the key and the
     * WeaponStatContainer as the value. This allows us to dynamically create WeaponStatContainers
     * and reference the container to add to/remove from/clear the weapon list within the container.
     * @param statsList The list of records representing each stat returned by the database.
     */
    private void initStatsHashMap(List<ContentStatEntity> statsList) {
        Completable.fromAction(() -> {
            if (hiddenStats == null) {
                hiddenStats = new HashMap<>();
                initHiddenStats();
            }

            if (statContainerByHash == null)
                statContainerByHash = new HashMap<>();
            statContainerByHash.clear();

            Gson gson = new GsonBuilder().create();
            for (ContentStatEntity statRecord : statsList) {
                Reader reader = new InputStreamReader(new ByteArrayInputStream(statRecord.json));
                JsonReader jsonReader = new JsonReader(reader);
                StatDefinition statDefinition = gson.fromJson(jsonReader, StatDefinition.class);

                if (statDefinition == null ||
                        statDefinition.displayProperties == null ||
                        statDefinition.displayProperties.name == null ||
                        hiddenStats.containsKey(statDefinition.displayProperties.name) ||
                        statDefinition.aggregationType != StatDefinition.AGGREGATE_TYPE_ITEM)
                    continue;

                statContainerByHash.put(statDefinition.hash,
                        new WeaponStatContainer(statDefinition.displayProperties.name, statDefinition.hash));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onComplete() {
                        Log.v(TAG,"initStatsHashMap onComplete");
                        containersInitialized.setValue(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG,"initStatsHashMap onError");
                        e.printStackTrace();
                    }
                });

    }

    /**
     * Query the Content database for all weapons of the class type provided and add the weapon
     * to each WeaponStatContainer.
     * @param db A reference to the Room database.
     * @param weaponClass Integer representing the weapon class.
     */
    public void getStats(ContentDatabase db, int weaponClass) {
        clearAllContainers();

        Cursor mCursor = db.query(QueryHelper.queryItemsByTypeAndSubType(InventoryItemDefinition.ITEM_TYPE_WEAPON,
                weaponClass), new String[0]);
        ArrayList<WeaponStatContainer> gridList = new ArrayList<>();

        Gson gson = new GsonBuilder().create();
        while (mCursor.moveToNext()) {
            byte[] json = mCursor.getString(mCursor.getColumnIndex(DatabaseStructure.COLUMN_JSON)).getBytes();
            Reader reader = new InputStreamReader(new ByteArrayInputStream(json));
            JsonReader jsonReader = new JsonReader(reader);

            InventoryItemDefinition item = gson.fromJson(jsonReader, InventoryItemDefinition.class);
            if (item.inventoryProperties.tierType != InventoryProperties.TIER_TYPE_SUPERIOR &&
                    item.inventoryProperties.tierType != InventoryProperties.TIER_TYPE_EXOTIC)
                continue;
            addItemToAllContainers(item);
        }
        for (Map.Entry entry : statContainerByHash.entrySet()) {
            WeaponStatContainer container = (WeaponStatContainer) entry.getValue();
            if (container.getWeaponListSize() == 0)
                continue;

            if (!container.hasNonZeroValueItem())
                continue;

            container.bubbleSortList();
            gridList.add(container);
        }
        statsList.setValue(gridList);
    }

    /**
     * Add the weapon provided to each WeaponStatContainer.
     * @param item InventoryItemDefinition representing the weapon
     */
    void addItemToAllContainers(InventoryItemDefinition item) {
        if (item.displayProperties == null || item.displayProperties.name == null)
            return;
        if (statContainerByHash == null)
            return;

        for (Map.Entry entry : statContainerByHash.entrySet()) {
            long statHash = (Long) entry.getKey();
            WeaponStatContainer container = statContainerByHash.get(statHash);
            if (!item.itemStats.stats.containsKey(statHash))
                item.itemStats.stats.put(statHash, new ItemStat(statHash));
            container.insertToList(item);
        }
    }
}
