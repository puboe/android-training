package com.mercadolibre.puboe.meli;

import com.mercadolibre.puboe.meli.model.Item;

import java.util.List;

/**
 * Created by puboe on 21/07/14.
 */
public interface ItemDAO {

    boolean exists(String id);
    Item getItem(String id);
    void saveItem(Item item);
    void updateItem(Item item);
    void deleteItem(Item item);
    List<Item> getAllItems();
}
