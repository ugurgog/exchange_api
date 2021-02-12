package com.exchangerate.exchange.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange")
public class ExchangeEntity extends BaseEntity {
    @Column
    private String trxId;
    @Column(precision = 10, scale = 6)
    private BigDecimal calculatedAmount;
    @Column(precision = 10, scale = 6)
    private BigDecimal amount;
    @Column(precision = 10, scale = 6)
    private BigDecimal rate;
    @Column
    private String toCurrency;
    @Column
    private String fromCurrency;
    @Column
    private LocalDate date;
    @Column
    private LocalDateTime trxDateTime;

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }


    public BigDecimal getCalculatedAmount() {
        return calculatedAmount;
    }

    public void setCalculatedAmount(BigDecimal calculatedAmount) {
        this.calculatedAmount = calculatedAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getTrxDateTime() {
        return trxDateTime;
    }

    public void setTrxDateTime(LocalDateTime trxDateTime) {
        this.trxDateTime = trxDateTime;
    }
}
