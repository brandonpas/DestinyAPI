package com.gmail.pasquarelli.brandon.destinyapi.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.gmail.pasquarelli.brandon.destinyapi.database.DatabaseStructure;
import com.gmail.pasquarelli.brandon.destinyapi.database.QueryHelper;
import com.gmail.pasquarelli.brandon.destinyapi.database.entity.ContentInventoryItemEntity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ContentInventoryItemDao {

    /**
     * Get each ContentInventoryItem definition object from a list.
//     * @param weaponClass An integer representing the item subclass
     * @return The ContentInventoryItemEntity.
     */
    @Query("SELECT * FROM " + DatabaseStructure.CONTENT_INVENTORY_ITEMS_TABLE_NAME +
    " WHERE " + DatabaseStructure.COLUMN_JSON + " like '%itemType\":3,%' " )
    Single<List<ContentInventoryItemEntity>> getWeaponsByClass();

    @Query("SELECT * FROM " + DatabaseStructure.CONTENT_INVENTORY_ITEMS_TABLE_NAME +
    " WHERE " + DatabaseStructure.COLUMN_ID + " in (:perkHashList)")
    Single<List<ContentInventoryItemEntity>> getPerksByHashList(List<Integer> perkHashList);
}
