package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.viewmodel;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.database.Cursor;
import android.util.Log;

import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.gmail.pasquarelli.brandon.destinyapi.database.QueryHelper;
import com.gmail.pasquarelli.brandon.destinyapi.database.databases.ContentDatabase;
import com.gmail.pasquarelli.brandon.destinyapi.database.entity.ContentInventoryItemEntity;
import com.gmail.pasquarelli.brandon.destinyapi.database.entity.ContentStatEntity;
import com.gmail.pasquarelli.brandon.destinyapi.model.InventoryItemDefinition;
import com.gmail.pasquarelli.brandon.destinyapi.model.InventoryProperties;
import com.gmail.pasquarelli.brandon.destinyapi.model.ItemStat;
import com.gmail.pasquarelli.brandon.destinyapi.model.SocketCategory;
import com.gmail.pasquarelli.brandon.destinyapi.model.StatDefinition;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model.SocketFilter;
import com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model.SocketFilterItem;
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
    private HashMap<Long,WeaponStatContainer> statContainerByHash;
    private HashMap<String,Boolean> hiddenStats;
    private HashMap<String, Boolean> socketHashesFound;
    private SocketFilter socketFilter = new SocketFilter();

    private MutableLiveData<SocketFilterItem[]> socketFilterList;

    public MutableLiveData<ArrayList<WeaponStatContainer>> getWeaponStats() {
        if (statsList == null) {
            statsList = new MutableLiveData<>();
        }
        return statsList;
    }

    public boolean getStatContainersInit() {
        return statContainerByHash != null;
    }

    public MutableLiveData<SocketFilterItem[]> getSocketFilterList() {
        if (socketFilterList == null)
            socketFilterList = new MutableLiveData<>();
        return socketFilterList;
    }

    public SocketFilterItem getSocketFilterItemAt(int position) {
        if (socketFilterList != null && socketFilterList.getValue() !=null
                && position < socketFilterList.getValue().length)
            return socketFilterList.getValue()[position];
        else
            return null;
    }

    /**
     * Obtain the full WeaponStatContainer list.
     * @return List of WeaponStatContainer
     */
    public ArrayList<WeaponStatContainer> getWeaponStatArray() {
        if (statsList == null)
            return null;
        return statsList.getValue();
    }

    /**
     * Clear the weapon list in each WeaponStatContainer
     */
    private void clearAllContainers() {
        if (statContainerByHash == null)
            return;

        for (Map.Entry entry : statContainerByHash.entrySet()) {
            WeaponStatContainer container = (WeaponStatContainer) entry.getValue();
            container.clearList();
        }
    }

    /**
     * Query the database for the list of perk (or Socket) names.
     * @param db Reference to Room database.
     * @param signedPerkHashes The list of signed integers representing the perk hashes
     */
    private void getWeaponPerks(ContentDatabase db, ArrayList<Integer> signedPerkHashes) {
        db.contentInventoryItemDao()
                .getPerksByHashList(signedPerkHashes)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<ContentInventoryItemEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onSuccess(List<ContentInventoryItemEntity> contentInventoryItemEntities) {
                        retrievePerkInfo(contentInventoryItemEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG,"getWeaponPerks onError");
                        e.printStackTrace();
                    }
                });
    }

    /**
     * Build the Array of perk item values (names).
     * @param entities List of entities from the database.
     */
    private void retrievePerkInfo(List<ContentInventoryItemEntity> entities) {
        Gson gson = new GsonBuilder().create();
        ArrayList<SocketFilterItem> perkFilter = new ArrayList<>();
        for (ContentInventoryItemEntity entity : entities) {
            JsonReader jsonReader = new JsonReader(entity.getJsonAsStream());
            InventoryItemDefinition item = gson.fromJson(jsonReader, InventoryItemDefinition.class);
            if (item.displayProperties != null && item.displayProperties.name != null) {
                SocketFilterItem socketFilterItem = new SocketFilterItem(item.hashCode);
                socketFilterItem.setPerkName(item.displayProperties.name);
                socketFilterItem.setPerkDescription(item.displayProperties.description);
                perkFilter.add(socketFilterItem);
            }
        }
        SocketFilterItem[] perkFilterItems = new SocketFilterItem[perkFilter.size()];
        for (int location = 0; location < perkFilter.size(); location++)
            perkFilterItems[location] = perkFilter.get(location);

        socketFilterList.setValue(perkFilterItems);
    }

    /**
     * Asynchronously query all the stats in the Content database provided by Bungie.
     * Then initialize the WeaponStatContainer hashmap.
     * @param db A reference to the Room database instance.
     */
    public void queryStats(ContentDatabase db, int weaponSelect) {
        db.contentStatDao()
                .getAllStats()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<ContentStatEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onSuccess(List<ContentStatEntity> contentStatEntity) {
                        Log.v(TAG,"queryStats onSuccess");
                        Log.v(TAG,"contentStatEntity: " + contentStatEntity.size());
                        initStatsHashMap(contentStatEntity, db, weaponSelect);
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
    @SuppressLint("UseSparseArrays")
    private void initStatsHashMap(List<ContentStatEntity> statsList, ContentDatabase db, int weaponSelect) {
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
                        getStats(db, weaponSelect);
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

        Completable.fromAction(() -> {
            clearAllContainers();
            if (socketHashesFound == null)
                socketHashesFound = new HashMap<>();

            socketHashesFound.clear();

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

                // extract the valid sockets and add to the arraylist for the filter
                if (item.socketBlock != null && item.socketBlock.socketCategories != null &&
                        item.socketBlock.socketCategories.length > 0) {
                    for (SocketCategory category : item.socketBlock.socketCategories) {
                        if (category.socketCategoryHash.equals(SocketCategory.WEAPON_PERK_CATEGORY_HASH)) {
                            // we've found the weapon socket perks. locate the indices in the socketEntries
                            // and add each entry to the list if it isn't already there.
                            for (int socketIndex : category.socketEntryIndices) {
                                if (!socketHashesFound.containsKey(item.socketBlock.socketEntries[socketIndex].unsignedSocketHash))
                                    socketHashesFound.put(item.socketBlock.socketEntries[socketIndex].unsignedSocketHash, true);
                            }
                            // stop looking for weapon socket perks
                            break;
                        }
                    }
                }

                addItemToAllContainers(item);
            }

            // Do some validation on the WeaponStatContainers before adding to the list.
            for (Map.Entry<Long,WeaponStatContainer> entry : statContainerByHash.entrySet()) {
                WeaponStatContainer container = entry.getValue();
                if (container.getWeaponListSize() == 0) {
                    continue;
                }

                if (!container.hasNonZeroValueItem()) {
                    container.clearList();
                    continue;
                }

                container.bubbleSortList();
                gridList.add(container);
            }

            // this is the list of unsigned integers. need to convert to signed int,
            // query the InventoryItemDefinition table to get the list of perks, populate the
            // multi-select spinner, and then we're good.
            ArrayList<String> hashKeys = new ArrayList<>();
            for (Map.Entry entry : socketHashesFound.entrySet())
                hashKeys.add((String) entry.getKey());

            socketFilter.setUnsignedSocketHashes(hashKeys);
            getWeaponPerks(db, socketFilter.getSignedSocketHashes());

            // Setting this value will notify observers that we've completed processing and begin updating Views.
            statsList.setValue(gridList);
        }).observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

    }

    /**
     * Add the weapon provided to each WeaponStatContainer.
     * @param item InventoryItemDefinition representing the weapon
     */
    private void addItemToAllContainers(InventoryItemDefinition item) {
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
