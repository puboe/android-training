package com.mercadolibre.puboe.meli.asynctask;

import android.util.Log;

import com.mercadolibre.puboe.meli.model.Search;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by puboe on 04/07/14.
 */
public class SearchAsyncTask extends CustomAsyncTask {

    private final String searchBaseUrl = "https://api.mercadolibre.com/sites/MLA/search?q=";
    private final String searchParameters = "&limit=15";
    private SearchCallbackInterface callbackInterface;

    public SearchAsyncTask(SearchCallbackInterface callbackInterface) {
        this.callbackInterface = callbackInterface;
    }

    @Override
    protected String doInBackground(String... query) {
        setQuery(query[0]);
        String url = searchBaseUrl + query[0] + searchParameters;
        Log.w(SearchAsyncTask.class.getSimpleName(), "doInBackgroudURL: " + url);
        return super.doInBackground(url);
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonSearch = new JSONObject(result);
            Search response = Search.parseSearchObject(jsonSearch);
            Log.i(SearchAsyncTask.class.getSimpleName(), "callbackInterface: " + callbackInterface);
            callbackInterface.onSearchSuccess(response);
        } catch (JSONException e) {
            Log.w(SearchAsyncTask.class.getSimpleName(), "onPostExecute: " + e.getLocalizedMessage());
        }
    }
}
