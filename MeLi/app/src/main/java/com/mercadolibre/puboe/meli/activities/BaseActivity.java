package com.mercadolibre.puboe.meli.activities;

import android.app.Activity;

import com.mercadolibre.puboe.meli.robospice.ItemRetrofitSpiceService;
import com.mercadolibre.puboe.meli.robospice.SearchRetrofitSpiceService;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by puboe on 28/07/14.
 */
public class BaseActivity extends Activity {

    private SpiceManager searchSpiceManager = new SpiceManager(SearchRetrofitSpiceService.class);
    private SpiceManager itemSpiceManager = new SpiceManager(ItemRetrofitSpiceService.class);

    @Override
    protected void onStart() {
        searchSpiceManager.start(this);
        itemSpiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        searchSpiceManager.shouldStop();
        itemSpiceManager.shouldStop();
        super.onStop();
    }

    protected SpiceManager getSearchSpiceManager() {
        return searchSpiceManager;
    }

    public SpiceManager getItemSpiceManager() {
        return itemSpiceManager;
    }
}
