package com.mercadolibre.puboe.meli.robospice;

import com.mercadolibre.puboe.meli.model.Search;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import roboguice.util.temp.Ln;

/**
 * Created by puboe on 28/07/14.
 */
public class SearchRetrofitRequest extends RetrofitSpiceRequest<Search, ApiSearch> {

    private String query;
    private Integer offset;
    private Integer limit;

    public SearchRetrofitRequest(String query, Integer offset, Integer limit) {
        super(Search.class, ApiSearch.class);
        this.query = query;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public Search loadDataFromNetwork() {
        Ln.d("Call search web service ");
        return getService().search(query, offset, limit);
    }
}
