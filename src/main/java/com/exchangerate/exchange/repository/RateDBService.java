package com.exchangerate.exchange.repository;

import com.exchangerate.exchange.entity.ExchangeEntity;
import com.exchangerate.exchange.model.CalculateRateResponseModel;
import com.exchangerate.exchange.service.IRateDBService;
import com.exchangerate.exchange.service.IRateRepositoryService;
import com.exchangerate.exchange.utils.ConvertUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RateDBService implements IRateDBService {
    private final IRateRepositoryService exchangeRepositoryService;

    public RateDBService(IRateRepositoryService exchangeRepositoryService) {
        this.exchangeRepositoryService = exchangeRepositoryService;
    }

    @Override
    public ExchangeEntity save(CalculateRateResponseModel request) {
        ExchangeEntity exEntity = ConvertUtils.convertCalculateRateResponseModelToExchangeEntity(request);
        ExchangeEntity response = exchangeRepositoryService.save(exEntity);
        return response;
    }

    @Override
    public ExchangeEntity update(ExchangeEntity entityRequest, CalculateRateResponseModel request) {
        entityRequest.setCalculatedAmount(request.getCalculateAmount());
        entityRequest.setSuccess(request.isSuccess());
        entityRequest.setErrorCode(request.getErrorCode());
        entityRequest.setErrorMessage(request.getErrorMessage());
        ExchangeEntity response = exchangeRepositoryService.save(entityRequest);
        return response;
    }

    @Override
    public List<ExchangeEntity> getRateListByTrxId(String trxId, Pageable pageable) {
        return exchangeRepositoryService.findByTrxId(trxId, pageable).getContent();
    }

    @Override
    public List<ExchangeEntity> getRateListByTrxDate(LocalDate date, Pageable pageable) {
        return exchangeRepositoryService.findByDate(date, pageable).getContent();
    }
}
