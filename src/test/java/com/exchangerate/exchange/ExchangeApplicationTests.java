package com.exchangerate.exchange;

import com.exchangerate.exchange.config.RatesProperties;
import com.exchangerate.exchange.model.CalculateRateRequestModel;
import com.exchangerate.exchange.model.CalculateRateResponseModel;
import com.exchangerate.exchange.model.ExchangeRateRequestModel;
import com.exchangerate.exchange.model.ExchangeRateResponseModel;
import com.exchangerate.exchange.service.IExchangeRateService;
import com.exchangerate.exchange.service.IHttpClient;
import com.exchangerate.exchange.service.RatesApiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class ExchangeApiApplicationTests {

    @Mock
    private IExchangeRateService exchangeRateService;

    @Mock
    private IHttpClient httpClient;

    @Mock
    private RatesProperties ratesProperties;


    @Test
    void testNotFoundRateCase(){

        IExchangeRateService exchangeRateService = new RatesApiService(httpClient,ratesProperties);

        ExchangeRateRequestModel request = new ExchangeRateRequestModel();
        request.setFromCurrency("EUR");
        request.setToCurrency(null);
        request.setDate(LocalDate.now());

        ExchangeRateResponseModel exchangeRateResponseModel = exchangeRateService.getRate(request);

        Assertions.assertNotNull(exchangeRateResponseModel);
    }

    @Test
    void testCalculateCase(){
        String fromCurrency = "TRY";
        String toCurrency = "EUR";
        BigDecimal amount = new BigDecimal("100");
        BigDecimal rate = new BigDecimal("2");
        BigDecimal calculatedAmount = amount.multiply(rate);

        CalculateRateRequestModel request = new CalculateRateRequestModel();
        request.setFromCurrency(fromCurrency);
        request.setToCurrency(toCurrency);
        request.setDate(LocalDate.now());
        request.setAmount(amount);


        CalculateRateResponseModel calculatedRate = new CalculateRateResponseModel();
        calculatedRate.setFromCurrency(fromCurrency);
        calculatedRate.setToCurrency(toCurrency);
        calculatedRate.setRate(rate);
        calculatedRate.setSuccess(false);
        calculatedRate.setAmount(amount);
        calculatedRate.setRate(rate);
        calculatedRate.setCalculateAmount(calculatedAmount);

        Mockito.when(exchangeRateService.calculateRate(request)).thenReturn(calculatedRate);

        CalculateRateResponseModel response = exchangeRateService.calculateRate(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(rate,response.getRate());
        Assertions.assertEquals(fromCurrency,response.getFromCurrency());
        Assertions.assertEquals(toCurrency,response.getToCurrency());
        Assertions.assertEquals(calculatedAmount,response.getCalculateAmount());
        Assertions.assertEquals(amount,response.getAmount());
    }
    @Test
    void testGetRateCase(){
        String fromCurrency = "TRY";
        String toCurrency = "EUR";
        BigDecimal rate = new BigDecimal("0.11");

        ExchangeRateRequestModel request = new ExchangeRateRequestModel();
        request.setFromCurrency(fromCurrency);
        request.setToCurrency(toCurrency);
        request.setDate(LocalDate.now());


        ExchangeRateResponseModel exchangeRate = new ExchangeRateResponseModel();
        exchangeRate.setFromCurrency(fromCurrency);
        exchangeRate.setToCurrency(toCurrency);
        exchangeRate.setRate(rate);
        exchangeRate.setSuccess(true);

        Mockito.when(exchangeRateService.getRate(request)).thenReturn(exchangeRate);

        ExchangeRateResponseModel response = exchangeRateService.getRate(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(rate,response.getRate());
        Assertions.assertEquals(fromCurrency,response.getFromCurrency());
        Assertions.assertEquals(toCurrency,response.getToCurrency());
    }
}