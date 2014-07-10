package com.mercadolibre.puboe.meli;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by puboe on 04/07/14.
 */
public class SearchAsyncTask extends CustomAsyncTask {

    private final String searchBaseUrl = "https://api.mercadolibre.com/sites/MLA/search?q=";
    private final String searchParameters = "&limit=15";
    private SearchResults activity;

    public SearchAsyncTask(SearchResults activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... query) {
        String url = searchBaseUrl + query[0] + searchParameters;
        Log.w("doInBackgroudURL", url);
        return super.doInBackground(url);
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonSearch = new JSONObject(result);
            Search response = Search.parseSearchObject(jsonSearch);
            activity.onSearchSuccess(response);
        } catch (JSONException e) {
            Log.w("onPostExecute", e.getLocalizedMessage());
        }
    }
}
