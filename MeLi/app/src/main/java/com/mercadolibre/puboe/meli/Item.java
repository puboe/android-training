package com.mercadolibre.puboe.meli;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by puboe on 03/07/14.
 */
public class Item implements Serializable {

    private String title;
    private Double price;
    private String thumbnail;

    public Item(String title, Double price, String thumbnail) {
        this.title = title;
        this.price = price;
        this.thumbnail = thumbnail;
    }

    public static Item parseItemObject(JSONObject item) throws JSONException {
        String title = item.getString("title");
        Double price = item.getDouble("price");
        String thumbnail = item.getString("thumbnail");

        return new Item(title, price,thumbnail);
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

}
