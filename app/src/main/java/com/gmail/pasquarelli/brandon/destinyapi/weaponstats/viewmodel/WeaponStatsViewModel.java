package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.viewmodel;

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

    public int getStatsListCount() {
        if (statsList.getValue() == null)
            return 0;
        else
            return statsList.getValue().size();
    }

    public WeaponStatContainer getWeaponStatAt(int position) {
        if (statsList.getValue() == null)
            return null;
        else
            return statsList.getValue().get(position);
    }

    void clearAllContainers() {
        for (Map.Entry entry : statContainerByHash.entrySet()) {
            WeaponStatContainer container = (WeaponStatContainer) entry.getValue();
            container.clearList();
        }
    }

    public void queryStats(ContentDatabase db) {
        db.contentStatDao().getAllStats()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<ContentStatEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onSuccess(List<ContentStatEntity> contentStatEntity) {
                        Log.v(TAG,"initStatHashMap onSuccess");
                        initStatsHashMap(contentStatEntity);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v(TAG,"initStatHashMap onError");
                        e.printStackTrace();
                    }
                });
    }

    private void initStatsHashMap(List<ContentStatEntity> statsList) {
        Completable.fromAction(() -> {
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
                        statDefinition.displayProperties.name == null)
                    continue;

                statContainerByHash.put(statDefinition.hash,
                        new WeaponStatContainer(statDefinition.displayProperties.name, statDefinition.hash));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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

    public void getStats(ContentDatabase db, int weaponClass) {
        clearAllContainers();

        Cursor mCursor = db.query(QueryHelper.getItemsByTypeAndSubType(InventoryItemDefinition.ITEM_TYPE_WEAPON,
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

            container.sortList();
            gridList.add(container);
        }

        statsList.setValue(gridList);
    }

    void addItemToAllContainers(InventoryItemDefinition item) {
//        for (Map.Entry entry : item.itemStats.stats.entrySet()) {
//            WeaponStatContainer container = statContainerByHash.get(entry.getKey());
//            if (container == null)
//                continue;
//            container.insertDescendingOrder(item);
//        }
        if (item.displayProperties == null ||
                item.displayProperties.name == null)
            return;
        for (Map.Entry entry : statContainerByHash.entrySet()) {

            long statHash = (Long) entry.getKey();
            if (statHash == 1345609583L && item.displayProperties.name.equals("Merciless"))
                Log.v(TAG, "debug");

            WeaponStatContainer container = statContainerByHash.get(entry.getKey());
//            if (!item.itemStats.stats.containsKey(entry.getKey()))
//                continue;
            container.insertDescendingOrder(item);
        }
    }

    public SingleObserver<List<ContentInventoryItemEntity>> getItemObserver() {
        return new SingleObserver<List<ContentInventoryItemEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<ContentInventoryItemEntity> contentInventoryItemEntity) {
                Log.v(TAG,"getStats onSuccess");
            }

            @Override
            public void onError(Throwable e) {
                Log.v(TAG, "getStats onError");
                e.printStackTrace();
            }
        };
    }

}
