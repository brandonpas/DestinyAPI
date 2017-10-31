package com.gmail.pasquarelli.brandon.destinyapi.database;

public class QueryHelper {

    public static String getItemsByTypeAndSubType(int type, int subType) {
        return "SELECT * FROM " + DatabaseStructure.CONTENT_INVENTORY_ITEMS_TABLE_NAME + " WHERE " +
                DatabaseStructure.COLUMN_JSON + " like '%itemType\":" + type + ",%' AND " +
                DatabaseStructure.COLUMN_JSON + " like '%itemSubType\":" + subType + ",%'";
    }
}
