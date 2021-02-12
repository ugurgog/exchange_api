package com.exchangerate.exchange.service;


import com.exchangerate.exchange.model.*;

public interface IExchangeRateService {
    RateListResponseModel getCalculatedList(RateListRequestModel request);
    CalculateRateResponseModel calculateRate(CalculateRateRequestModel request);
    ExchangeRateResponseModel getRate(ExchangeRateRequestModel request);
}
