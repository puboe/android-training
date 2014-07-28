package com.mercadolibre.puboe.meli.robospice;

/**
 * Created by puboe on 28/07/14.
 */
public class ItemRetrofitSpiceService extends BaseRetrofitSpiceService {

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(ApiItem.class);
    }
}
