package com.mercadolibre.puboe.meli;

import com.mercadolibre.puboe.meli.model.Search;

/**
 * Created by puboe on 04/07/14.
 */
public interface SearchCallbackInterface {

    void onSearchSuccess(Search response);
}
