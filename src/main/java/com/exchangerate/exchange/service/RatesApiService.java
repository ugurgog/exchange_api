package com.exchangerate.exchange.service;

import com.exchangerate.exchange.config.RatesProperties;
import com.exchangerate.exchange.entity.ExchangeEntity;
import com.exchangerate.exchange.model.*;
import com.exchangerate.exchange.utils.ConvertUtils;
import com.exchangerate.exchange.utils.CustomUtils;
import com.google.gson.Gson;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.exchangerate.exchange.utils.CustomUtils.isNullOrEmpty;

@Service
public class RatesApiService implements IRateService {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final RatesProperties properties;
    private final IHttpClient2 httpClient2;
    private IRateDBService exchangeDBService;
    private Gson gson = new Gson();

    public RatesApiService(RatesProperties properties,
                           IRateDBService exchangeDBService, IHttpClient2 httpClient2) {
        this.properties = properties;
        this.exchangeDBService = exchangeDBService;
        this.httpClient2 = httpClient2;
    }

    @Override
    public ExchangeRateResponseModel getRate(ExchangeRateRequestModel request) {
        ExchangeRateResponseModel response = new ExchangeRateResponseModel();

        LOG.info("::getRate request:{}", gson.toJson(request));

        if(isNullOrEmpty(request.getFromCurrency()) || isNullOrEmpty(request.getToCurrency())){
            LOG.error("::getRate invalid request request:{}", gson.toJson(request));
            response.setErrorCode("INVALID_REQUEST");
            response.setErrorMessage("From currency or To currency value is invalid!");
            return response;
        }

        String paramData = request.getDate() == null ? "latest" : dateTimeFormatter.format(request.getDate()).concat("/");
        paramData += request.getFromCurrency().concat("/").concat(request.getToCurrency());

        try {
            Map<String, String> reqProps = new HashMap<>();
            reqProps.put("Content-Type", "application/json");
            reqProps.put("Accept", "*/*");

            RateChannelResponseModel provResponse = httpClient2.get(properties.getUrl(),
                    paramData, reqProps, RateChannelResponseModel.class);

            if(provResponse == null || CollectionUtils.isEmpty(provResponse.getRates())){
                LOG.error("::getRate RateChannelResponseModel error request:{}, provResponse:{}",
                        gson.toJson(request), gson.toJson(provResponse));
                response.setErrorCode("NO_CONTENT");
                response.setErrorMessage("No valid data found!");
                return response;
            }

            response.setRate(provResponse.getRates().get(request.getToCurrency()));
            response.setDate(provResponse.getDate());
            response.setSuccess(true);
            response.setFromCurrency(request.getFromCurrency());
            response.setToCurrency(request.getToCurrency());

        } catch (Exception ex) {
            LOG.error("::getRate RateChannelResponseModel exception error request:{}",
                    gson.toJson(request), ex);
            response.setErrorCode("EXCEPTION");
            response.setErrorMessage("Error occurred while getting rate error:".concat(ex.getMessage()));
            return response;
        }
        return response;
    }

    @Override
    public RateListResponseModel getCalculatedList(RateListRequestModel request) {
        RateListResponseModel response = new RateListResponseModel();

        if(request == null || (request.getSize() <= 0)){
            response.setErrorCode("INVALID_REQUEST");
            response.setErrorMessage("Request model is not valid!");
            return response;
        }

        if(request.getTrxDate() == null && CustomUtils.isNullOrEmpty(request.getTrxId())){
            response.setErrorCode("INVALID_REQUEST");
            response.setErrorMessage("Transaction date and Transaction id cannot be null!");
            return response;
        }

        Pageable paging = PageRequest.of(request.getPage(), request.getSize());
        List<ExchangeEntity> exchangeEntityList = exchangeDBService.getCalculatedRateList(request.getTrxId(),request.getTrxDate(),paging);

        if(!CollectionUtils.isEmpty(exchangeEntityList)){
            response.setTrxList(new ArrayList<>());
            for(ExchangeEntity exchangeEntity : exchangeEntityList){
                response.getTrxList().add(ConvertUtils.convertExchangeEntityToRateListModel(exchangeEntity));
            }
            response.setSuccess(true);
        }else{
            response.setErrorCode("NO_CONTENT");
            response.setErrorMessage("Calculated rate list is empty!");
            return response;
        }

        return response;
    }

    @Override
    public CalculateRateResponseModel calculateRate(CalculateRateRequestModel request) {
        CalculateRateResponseModel response =
                ConvertUtils.convertCalculateRateRequestModelToCalculateRateResponseModel(request);

        ExchangeEntity savedEntity = exchangeDBService.save(response);
        if (savedEntity == null) {
            LOG.error("::calculateRate calculate rate save error response:{}", gson.toJson(response));
            response.setErrorCode("DB_ERROR");
            response.setErrorMessage("Calculated rate model cannot be saved!");
            return response;
        }

        LOG.info("::calculateRate savedEntity:{}", gson.toJson(savedEntity));

        ExchangeRateRequestModel exRequest = new ExchangeRateRequestModel();
        exRequest.setDate(request.getDate());
        exRequest.setFromCurrency(request.getFromCurrency());
        exRequest.setToCurrency(request.getToCurrency());

        ExchangeRateResponseModel exchangeRateResponseModel = getRate(exRequest);
        if (!exchangeRateResponseModel.isSuccess()) {
            LOG.error("::calculateRate rate not found req:{} exRate:{}", gson.toJson(request), gson.toJson(exchangeRateResponseModel));
            response.setErrorCode(exchangeRateResponseModel.getErrorCode());
            response.setErrorMessage(exchangeRateResponseModel.getErrorMessage());
            exchangeDBService.update(savedEntity,response);
            return response;
        }
        BigDecimal rate = exchangeRateResponseModel.getRate();
        BigDecimal calculateAmount = request.getAmount().multiply(rate).setScale(6, RoundingMode.HALF_UP);

        response.setRate(rate);
        response.setCalculateAmount(calculateAmount);
        response.setSuccess(true);

        exchangeDBService.update(savedEntity,response);
        return response;
    }
}
