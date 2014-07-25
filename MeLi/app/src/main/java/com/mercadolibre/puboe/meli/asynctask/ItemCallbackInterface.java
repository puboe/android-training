package com.mercadolibre.puboe.meli.asynctask;

import com.mercadolibre.puboe.meli.model.Item;

/**
 * Created by puboe on 10/07/14.
 */
public interface ItemCallbackInterface {

   void onItemRequestSuccess(Item response);

}
