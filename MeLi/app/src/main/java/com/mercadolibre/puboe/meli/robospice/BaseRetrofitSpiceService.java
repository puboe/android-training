package com.mercadolibre.puboe.meli.robospice;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

/**
 * Created by puboe on 28/07/14.
 */
public class BaseRetrofitSpiceService extends RetrofitGsonSpiceService {

    private final static String BASE_URL = "https://api.mercadolibre.com";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }
}