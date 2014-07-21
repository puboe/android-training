package com.mercadolibre.puboe.meli.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by puboe on 21/07/14.
 */
public class ItemDatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Item.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String DOUBLE_TYPE = " REAL";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + ItemDatabaseContract.ItemEntry.TABLE_NAME + " (" +
                    ItemDatabaseContract.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ItemDatabaseContract.ItemEntry.COLUMN_NAME_ITEM_ID + TEXT_TYPE + COMMA_SEP +
                    ItemDatabaseContract.ItemEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    ItemDatabaseContract.ItemEntry.COLUMN_NAME_SUBTITLE + TEXT_TYPE + COMMA_SEP +
                    ItemDatabaseContract.ItemEntry.COLUMN_NAME_PRICE + DOUBLE_TYPE + COMMA_SEP +
                    ItemDatabaseContract.ItemEntry.COLUMN_NAME_THUMBNAIL + TEXT_TYPE + COMMA_SEP +
                    ItemDatabaseContract.ItemEntry.COLUMN_NAME_IMAGEURL + TEXT_TYPE + COMMA_SEP +
                    ItemDatabaseContract.ItemEntry.COLUMN_NAME_CONDITION + TEXT_TYPE + COMMA_SEP +
                    ItemDatabaseContract.ItemEntry.COLUMN_NAME_AVAILABLE + INTEGER_TYPE  +
            " )";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + ItemDatabaseContract.ItemEntry.TABLE_NAME;

    public ItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        Log.w(ItemDatabaseHelper.class.getSimpleName(), "Creating item database");
        db.execSQL(SQL_CREATE_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        Log.w(ItemDatabaseHelper.class.getSimpleName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
