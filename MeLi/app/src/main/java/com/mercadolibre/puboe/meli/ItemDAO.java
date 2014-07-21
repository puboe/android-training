package com.mercadolibre.puboe.meli;

import java.util.List;

/**
 * Created by puboe on 21/07/14.
 */
public interface ItemDAO {

    boolean exists(String id);
    Item getItem(String id);
    void saveItem(Item item);
    void deleteItem(Item item);
    List<Item> getAllItems();
}
