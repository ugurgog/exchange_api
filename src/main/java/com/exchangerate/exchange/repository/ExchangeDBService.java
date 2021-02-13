package com.exchangerate.exchange.repository;

import com.exchangerate.exchange.entity.ExchangeEntity;
import com.exchangerate.exchange.model.CalculateRateResponseModel;
import com.exchangerate.exchange.service.IExchangeDBService;
import com.exchangerate.exchange.service.IExchangeRepositoryService;
import com.exchangerate.exchange.utils.ConvertUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExchangeDBService implements IExchangeDBService {
    private final IExchangeRepositoryService exchangeRepositoryService;

    public ExchangeDBService(IExchangeRepositoryService exchangeRepositoryService) {
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
    public List<ExchangeEntity> getCalculatedRateList(String trxId, LocalDate date, Pageable pageable) {
        if(trxId == null || trxId.trim().length() == 0)
            return exchangeRepositoryService.findByDate(date, pageable).getContent();
        else
            return exchangeRepositoryService.findByTrxId(trxId, pageable).getContent();
    }
}
