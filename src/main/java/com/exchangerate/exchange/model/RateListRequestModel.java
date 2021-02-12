package com.exchangerate.exchange.model;


import java.time.LocalDate;

public class RateListRequestModel {
    private String trxId;
    private LocalDate trxDate;
    private int page;
    private int size;

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public LocalDate getTrxDate() {
        return trxDate;
    }

    public void setTrxDate(LocalDate trxDate) {
        this.trxDate = trxDate;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }
}
