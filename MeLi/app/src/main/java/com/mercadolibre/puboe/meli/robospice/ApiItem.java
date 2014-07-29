package com.mercadolibre.puboe.meli.robospice;

import com.mercadolibre.puboe.meli.model.Item;

import retrofit.http.GET;
import retrofit.http.Path;

public interface ApiItem {
    @GET("/items/{itemId}")
    Item getItem(@Path("itemId") String itemId);
}
