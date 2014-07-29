package com.mercadolibre.puboe.meli.model;

import java.io.Serializable;

/**
 * Created by puboe on 28/07/14.
 */
public class Picture implements Serializable {

    String url;

    public Picture(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
