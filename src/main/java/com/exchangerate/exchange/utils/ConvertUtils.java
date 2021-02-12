package com.exchangerate.exchange.utils;

import com.exchangerate.exchange.entity.ExchangeEntity;
import com.exchangerate.exchange.model.RateListModel;

import javax.servlet.http.HttpServletRequest;

public class ConvertUtils {
    public static RateListModel convertExchangeEntityToRateListModel(ExchangeEntity exchangeEntity) {

        if(exchangeEntity == null)
            return null;

        RateListModel rateListModel = new RateListModel();
        rateListModel.setRate(exchangeEntity.getRate());
        rateListModel.setCalculatedAmount(exchangeEntity.getCalculatedAmount());
        rateListModel.setAmount(exchangeEntity.getAmount());
        rateListModel.setFromCurrency(exchangeEntity.getFromCurrency());
        rateListModel.setToCurrency(exchangeEntity.getToCurrency());
        rateListModel.setTransactionDate(exchangeEntity.getTrxDateTime());
        rateListModel.setId(exchangeEntity.getTrxId());

        return rateListModel;
    }
}
