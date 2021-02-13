package com.exchangerate.exchange.utils;

import com.exchangerate.exchange.entity.ExchangeEntity;
import com.exchangerate.exchange.model.CalculateRateRequestModel;
import com.exchangerate.exchange.model.CalculateRateResponseModel;
import com.exchangerate.exchange.model.RateListModel;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

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

    public static ExchangeEntity convertCalculateRateResponseModelToExchangeEntity(CalculateRateResponseModel request){
        if(request == null)
            return null;

        ExchangeEntity exEntity = new ExchangeEntity();
        exEntity.setDate(request.getDate());
        exEntity.setAmount(request.getAmount());
        exEntity.setRate(request.getRate());
        exEntity.setFromCurrency(request.getFromCurrency());
        exEntity.setToCurrency( request.getToCurrency());
        exEntity.setCalculatedAmount(request.getCalculateAmount());
        exEntity.setTrxId(request.getTrxId());
        exEntity.setTrxDateTime(LocalDateTime.now());
        exEntity.setSuccess(request.isSuccess());
        exEntity.setErrorCode(request.getErrorCode());
        exEntity.setErrorMessage(request.getErrorMessage());
        return exEntity;
    }

    public static CalculateRateResponseModel convertCalculateRateRequestModelToCalculateRateResponseModel(CalculateRateRequestModel request){

        if (request == null){
            return null;
        }
        CalculateRateResponseModel response = new CalculateRateResponseModel();
        response.setTrxId(CustomUtils.generateTrxId());
        response.setFromCurrency(request.getFromCurrency());
        response.setToCurrency(request.getToCurrency());
        response.setAmount(request.getAmount());
        response.setDate(request.getDate());
        return response;
    }
}
