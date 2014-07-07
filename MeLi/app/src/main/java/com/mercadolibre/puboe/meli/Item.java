package com.mercadolibre.puboe.meli;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by puboe on 03/07/14.
 */
public class Item implements Serializable {

    String title;
    Double price;

    public Item(String title, Double price) {
        this.title = title;
        this.price = price;
    }

    public static Item parseItemObject(JSONObject item) throws JSONException {
        String title = item.getString("title");
        Double price = item.getDouble("price");
        return new Item(title, price);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
