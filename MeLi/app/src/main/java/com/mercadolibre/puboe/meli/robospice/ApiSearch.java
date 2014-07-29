package com.mercadolibre.puboe.meli.robospice;

import com.mercadolibre.puboe.meli.model.Search;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by puboe on 28/07/14.
 */
public interface ApiSearch {

    @GET("/sites/MLA/search")
    Search search(@Query("q") String query, @Query("offset") Integer offset, @Query("limit") Integer limit);
}
