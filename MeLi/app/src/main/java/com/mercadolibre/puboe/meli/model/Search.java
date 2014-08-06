package com.mercadolibre.puboe.meli.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedHashSet;

/**
 * Created by puboe on 04/07/14.
 */
public class Search implements Serializable {

    private String query;
    private Paging paging;
    private LinkedHashSet<Item> results;

    private Search(Paging paging, LinkedHashSet<Item> results) {
        this.paging = paging;
        this.results = results;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public LinkedHashSet<Item> getResults() {
        return results;
    }

    public void setResults(LinkedHashSet<Item> results) {
        this.results = results;
    }

    public String getQuery() {
        return query;
    }

    public static Search parseSearchObject(JSONObject search) throws JSONException {

        JSONObject pag = search.getJSONObject("paging");
        Paging paging = new Paging(pag.getInt("total"), pag.getInt("offset"), pag.getInt("limit"));
        JSONArray res = search.getJSONArray("results");
        LinkedHashSet<Item> results = new LinkedHashSet<Item>();

        for(int i=0; i < res.length(); i++) {
            JSONObject jsonItem = res.getJSONObject(i);
            Item item = Item.parseItemObject(jsonItem);
            results.add(item);
        }

        return new Search(paging, results);
    }
}
