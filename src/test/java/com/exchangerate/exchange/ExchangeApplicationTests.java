package com.exchangerate.exchange;

import com.exchangerate.exchange.config.RatesProperties;
import com.exchangerate.exchange.model.*;
import com.exchangerate.exchange.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class ExchangeApiApplicationTests {

    @Mock
    private IRateService exchangeRateService;

    @Mock
    private IHttpClient2 httpClient2;

    @Mock
    private RatesProperties ratesProperties;

    @Mock
    private IRateDBService exchangeDBService;

    @Test
    void testGetRateCase(){
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        ExchangeRateRequestModel request = new ExchangeRateRequestModel();
        request.setFromCurrency(fromCurrency);
        request.setToCurrency(toCurrency);
        request.setDate(LocalDate.now());

        ExchangeRateResponseModel response = exchangeRateService.getRate(request);
        Assertions.assertNotNull(response);
    }

    @Test
    void testRateNotFoundCase(){
        IRateService exchangeRateService = new RatesApiService(ratesProperties, exchangeDBService,
                httpClient2);

        ExchangeRateRequestModel request = new ExchangeRateRequestModel();
        request.setFromCurrency("EUR");
        request.setToCurrency(null);
        request.setDate(LocalDate.now());

        ExchangeRateResponseModel exchangeRateResponseModel = exchangeRateService.getRate(request);
        Assertions.assertNotNull(exchangeRateResponseModel);
    }

    @Test
    void testCalculateCase(){
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal amount = new BigDecimal("100");
        BigDecimal rate = new BigDecimal("2");
        BigDecimal calculatedAmount = amount.multiply(rate);

        CalculateRateRequestModel request = new CalculateRateRequestModel();
        request.setFromCurrency(fromCurrency);
        request.setToCurrency(toCurrency);
        request.setDate(LocalDate.now());
        request.setAmount(amount);

        CalculateRateResponseModel response = exchangeRateService.calculateRate(request);
        Assertions.assertNotNull(response);
    }

    @Test
    void testCalculateRateListCase(){
        RateListRequestModel request = new RateListRequestModel();
        request.setPage(2);
        request.setSize(2);
        request.setTrxDate(LocalDate.now());

        RateListResponseModel response = exchangeRateService.getCalculatedList(request);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getTrxList());
    }
}