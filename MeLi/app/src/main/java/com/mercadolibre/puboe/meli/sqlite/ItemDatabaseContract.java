package com.mercadolibre.puboe.meli.sqlite;

import android.provider.BaseColumns;

/**
 * Created by puboe on 21/07/14.
 */
public class ItemDatabaseContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ItemDatabaseContract() {}

    /* Inner class that defines the table contents */
    public static abstract class ItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "item";
        public static final String COLUMN_NAME_ITEM_ID = "itemId";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_THUMBNAIL = "thumbnail";
        public static final String COLUMN_NAME_IMAGEURL = "imageurl";
        public static final String COLUMN_NAME_CONDITION = "condition";
        public static final String COLUMN_NAME_AVAILABLE = "available";
    }
}
