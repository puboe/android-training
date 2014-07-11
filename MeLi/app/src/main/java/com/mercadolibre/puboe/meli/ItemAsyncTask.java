package com.mercadolibre.puboe.meli;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by puboe on 10/07/14.
 */
public class ItemAsyncTask extends CustomAsyncTask {

    private final String itemsBaseUrl = "https://api.mercadolibre.com/items/";
    private ItemViewActivity activity;

    public ItemAsyncTask(ItemViewActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... query) {
        String url = itemsBaseUrl + query[0];
        Log.w("doInBackgroudURL", url);
        return super.doInBackground(url);
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonItem = new JSONObject(result);
            Item response = Item.parseItem(jsonItem);
            activity.onItemRequestSuccess(response);
        } catch (JSONException e) {
            Log.w("onPostExecute", e.getLocalizedMessage());
        }
    }
}
