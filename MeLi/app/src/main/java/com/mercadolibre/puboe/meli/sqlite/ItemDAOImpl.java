package com.mercadolibre.puboe.meli.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mercadolibre.puboe.meli.Item;
import com.mercadolibre.puboe.meli.ItemDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by puboe on 21/07/14.
 */
public class ItemDAOImpl implements ItemDAO {

    private static ItemDAOImpl instance;
    // Database fields
    private SQLiteDatabase database;
    private ItemDatabaseHelper dbHelper;
    private String[] allColumns = { ItemDatabaseContract.ItemEntry.COLUMN_NAME_ITEM_ID,
            ItemDatabaseContract.ItemEntry.COLUMN_NAME_TITLE, ItemDatabaseContract.ItemEntry.COLUMN_NAME_SUBTITLE,
            ItemDatabaseContract.ItemEntry.COLUMN_NAME_PRICE, ItemDatabaseContract.ItemEntry.COLUMN_NAME_THUMBNAIL,
            ItemDatabaseContract.ItemEntry.COLUMN_NAME_IMAGEURL, ItemDatabaseContract.ItemEntry.COLUMN_NAME_CONDITION,
            ItemDatabaseContract.ItemEntry.COLUMN_NAME_AVAILABLE};

    public static ItemDAOImpl getInstance(Context context) {
        if(instance == null) {
            instance = new ItemDAOImpl(context);
        }
        return instance;
    }

    private ItemDAOImpl(Context context) {
        dbHelper = new ItemDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    @Override
    public boolean exists(String id) {
        return getItem(id) != null;
    }

    @Override
    public Item getItem(String itemId) {
        open();
        Item newItem = null;
        Log.i(ItemDAOImpl.class.getSimpleName(), "database: " + database);
        Cursor cursor = database.rawQuery("select * from " + ItemDatabaseContract.ItemEntry.TABLE_NAME +
                        " where " + ItemDatabaseContract.ItemEntry.COLUMN_NAME_ITEM_ID + "='" + itemId + "'", null);
        cursor.moveToFirst();
        if(cursor.getCount() != 0) {
            newItem = cursorToItem(cursor);
        }
        cursor.close();
        close();
        return newItem;

    }

    @Override
    public void saveItem(Item item) {
        open();
        ContentValues values = new ContentValues();
        values.put(ItemDatabaseContract.ItemEntry.COLUMN_NAME_ITEM_ID, item.getId());
        values.put(ItemDatabaseContract.ItemEntry.COLUMN_NAME_TITLE, item.getTitle());
        values.put(ItemDatabaseContract.ItemEntry.COLUMN_NAME_SUBTITLE, item.getSubtitle());
        values.put(ItemDatabaseContract.ItemEntry.COLUMN_NAME_PRICE, item.getPrice());
        values.put(ItemDatabaseContract.ItemEntry.COLUMN_NAME_THUMBNAIL, item.getThumbnail());
        values.put(ItemDatabaseContract.ItemEntry.COLUMN_NAME_IMAGEURL, item.getImageUrl());
        values.put(ItemDatabaseContract.ItemEntry.COLUMN_NAME_CONDITION, item.getCondition());
        values.put(ItemDatabaseContract.ItemEntry.COLUMN_NAME_AVAILABLE, item.getAvailableQuantity());
        long insertId = database.insert(ItemDatabaseContract.ItemEntry.TABLE_NAME, null,
                values);
        System.out.println("Item added with id: " + item.getId());
        close();
        return;
    }

    @Override
    public void deleteItem(Item item) {
        open();
        String itemId = item.getId();
        System.out.println("Item deleted with id: " + itemId);
        database.execSQL("delete from " + ItemDatabaseContract.ItemEntry.TABLE_NAME +
                " where " + ItemDatabaseContract.ItemEntry.COLUMN_NAME_ITEM_ID + "='" + itemId + "'");
        close();
    }

    @Override
    public List<Item> getAllItems() {
        open();
        List<Item> items = new ArrayList<Item>();

        Cursor cursor = database.query(ItemDatabaseContract.ItemEntry.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = cursorToItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return items;
    }

    private Item cursorToItem(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(ItemDatabaseContract.ItemEntry.COLUMN_NAME_ITEM_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(ItemDatabaseContract.ItemEntry.COLUMN_NAME_TITLE));
        String subtitle = cursor.getString(cursor.getColumnIndexOrThrow(ItemDatabaseContract.ItemEntry.COLUMN_NAME_SUBTITLE));
        Double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ItemDatabaseContract.ItemEntry.COLUMN_NAME_PRICE));
        String thumbnail = cursor.getString(cursor.getColumnIndexOrThrow(ItemDatabaseContract.ItemEntry.COLUMN_NAME_THUMBNAIL));
        String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(ItemDatabaseContract.ItemEntry.COLUMN_NAME_IMAGEURL));
        String condition = cursor.getString(cursor.getColumnIndexOrThrow(ItemDatabaseContract.ItemEntry.COLUMN_NAME_CONDITION));
        Integer availableQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ItemDatabaseContract.ItemEntry.COLUMN_NAME_AVAILABLE));

        Item item = new Item(id, title, subtitle, price, thumbnail, availableQuantity, imageUrl, condition);
        return item;
    }
}
