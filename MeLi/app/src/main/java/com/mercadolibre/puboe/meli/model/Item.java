package com.mercadolibre.puboe.meli.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by puboe on 03/07/14.
 */

public class Item implements Serializable {

    private String id;
    private String title;
    private String subtitle;
    private Double price;
    private String thumbnail;

    private List<Picture> pictures = new ArrayList<Picture>();
    private String condition;
    private Integer available_quantity;


    private Item(String id, String title, String subtitle, Double price, String thumbnail, Integer availableQuantity) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.price = price;
        this.thumbnail = thumbnail;
        this.available_quantity = availableQuantity;
    }

    public Item(String id, String title, String subtitle, Double price, String thumbnail, Integer availableQuantity, String imageUrl, String condition) {
        this(id, title, subtitle, price, thumbnail, availableQuantity);
//        this.imageUrl = imageUrl;
        this.condition = condition;
    }

    public static Item parseItemObject(JSONObject item) throws JSONException {
        String id = item.getString("id");
        String title = item.getString("title");
        String subtitle = item.getString("subtitle");
        Double price = item.getDouble("price");
        String thumbnail = item.getString("thumbnail");
        Integer availableQuantity = item.getInt("available_quantity");

        return new Item(id, title, subtitle, price,thumbnail, availableQuantity);
    }

    public static Item parseCompleteItem(JSONObject jsonItem) throws JSONException {
        Item item = parseItemObject(jsonItem);

//        try {
////            item.setImageUrl(jsonItem.getJSONArray("pictures").getJSONObject(0).getString("url"));
//        } catch(JSONException e) {
//            Log.e(Item.class.getSimpleName(), "Item no tiene imagen.");
////            item.setImageUrl(null);
//        }
        item.setCondition(jsonItem.getString("condition"));

        return item;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

//    public String getImageUrl() {
//        return imageUrl;
//    }

//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Integer getAvailableQuantity() {
        return available_quantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.available_quantity = availableQuantity;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (!id.equals(item.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
