package com.mercadolibre.puboe.meli.robospice;

import com.mercadolibre.puboe.meli.model.Item;
import com.mercadolibre.puboe.meli.model.Search;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import roboguice.util.temp.Ln;

/**
 * Created by puboe on 28/07/14.
 */
public class ItemRetrofitRequest extends RetrofitSpiceRequest<Item, ApiItem> {

    private String itemId;

    public ItemRetrofitRequest(String itemId) {
        super(Item.class, ApiItem.class);
        this.itemId = itemId;
    }

    @Override
    public Item loadDataFromNetwork() throws Exception {
        Ln.d("Call item web service ");
        return getService().getItem(itemId);
    }
}
