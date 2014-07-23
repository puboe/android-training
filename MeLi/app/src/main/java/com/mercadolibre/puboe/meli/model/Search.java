package com.mercadolibre.puboe.meli.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by puboe on 04/07/14.
 */
public class Search implements Serializable {

    private Paging paging;
    private List<Item> results;

    private Search(Paging paging, List<Item> results) {
        this.paging = paging;
        this.results = results;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public List<Item> getResults() {
        return results;
    }

    public void setResults(List<Item> results) {
        this.results = results;
    }

    public static Search parseSearchObject(JSONObject search) throws JSONException {

        JSONObject pag = search.getJSONObject("paging");
        Paging paging = new Paging(pag.getInt("total"), pag.getInt("offset"), pag.getInt("limit"));
        JSONArray res = search.getJSONArray("results");
        List<Item> results = new ArrayList<Item>();

        for(int i=0; i < res.length(); i++) {
            JSONObject jsonItem = res.getJSONObject(i);
            Item item = Item.parseItemObject(jsonItem);
            results.add(item);
        }

        return new Search(paging, results);
    }
}
