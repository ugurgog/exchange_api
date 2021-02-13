package com.exchangerate.exchange.service;


import com.exchangerate.exchange.model.*;

public interface IRateService {
    RateListResponseModel getCalculatedList(RateListRequestModel request);
    CalculateRateResponseModel calculateRate(CalculateRateRequestModel request);
    ExchangeRateResponseModel getRate(ExchangeRateRequestModel request);
}
