package com.exchangerate.exchange.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExchangeRateResponseModel extends BaseResponseModel {

    private BigDecimal rate;
    private String date;
    private String fromCurrency;
    private String toCurrency;

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }


}
