package com.exchangerate.exchange.service;

import com.exchangerate.exchange.entity.ExchangeEntity;
import com.exchangerate.exchange.model.CalculateRateResponseModel;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IRateDBService {
    ExchangeEntity save(CalculateRateResponseModel request);
    ExchangeEntity update(ExchangeEntity entityRequest, CalculateRateResponseModel request);
    List<ExchangeEntity> getCalculatedRateList(String trxId, LocalDate trxDate, Pageable pageable);
}
