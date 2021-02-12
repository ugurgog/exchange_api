package com.exchangerate.exchange.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CalculateRateResponseModel extends BaseResponseModel {
    private String trxId;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal calculateAmount;
    private LocalDate date;

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
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

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCalculateAmount() {
        return calculateAmount;
    }

    public void setCalculateAmount(BigDecimal calculateAmount) {
        this.calculateAmount = calculateAmount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


}
