package com.mercadolibre.puboe.meli;

import java.io.Serializable;

/**
 * Created by puboe on 04/07/14.
 */
public class Paging implements Serializable {

    private Integer total;
    private Integer offset;
    private Integer limit;

    public Paging(Integer total, Integer offset, Integer limit) {
        this.total = total;
        this.offset = offset;
        this.limit = limit;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
