package com.exchangerate.exchange.model;

import java.util.List;

public class RateListResponseModel extends BaseResponseModel {
    private List<RateListModel> trxList;
    public List<RateListModel> getTrxList() {
        return trxList;
    }

    public void setTrxList(List<RateListModel> trxList) {
        this.trxList = trxList;
    }


}
