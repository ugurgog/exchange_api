package com.exchangerate.exchange.controller;

import com.exchangerate.exchange.model.*;
import com.exchangerate.exchange.service.IRateService;
import com.exchangerate.exchange.utils.CustomUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import springfox.documentation.annotations.ApiIgnore;

import static com.exchangerate.exchange.utils.CustomUtils.isNullOrEmpty;

@RestController
public class RateController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final Gson gson = new Gson();

    private final IRateService rateService;

    public RateController(IRateService rateService) {
        this.rateService = rateService;
    }

    @ApiIgnore
    @GetMapping("/")
    public RedirectView home() {
        return new RedirectView("swagger-ui/index.html");
    }

    @PostMapping("/get-rate")
    public ExchangeRateResponseModel getRate(@RequestBody ExchangeRateRequestModel request) {
        ExchangeRateResponseModel response = new ExchangeRateResponseModel();
        LOG.info("::getRate request:{}", gson.toJson(request));

        if(isNullOrEmpty(request.getFromCurrency()) || isNullOrEmpty(request.getToCurrency())){
            LOG.error("::getRate invalid request request:{}", gson.toJson(request));
            response.setErrorCode("INVALID_REQUEST");
            response.setErrorMessage("From currency or To currency value is invalid!");
            return response;
        }

        return rateService.getRate(request);
    }

    @PostMapping("/calculate-rate")
    public CalculateRateResponseModel calculateRate(@RequestBody CalculateRateRequestModel request) {
        return rateService.calculateRate(request);
    }

    @PostMapping("/calculate-rate/list")
    public RateListResponseModel calculateRateList(@RequestBody RateListRequestModel request) {
        RateListResponseModel response = new RateListResponseModel();
        LOG.info("::calculateRateList request:{}", gson.toJson(request));

        if(request == null){
            LOG.error("::calculateRateList rate not found request:{}", gson.toJson(request));
            response.setErrorCode("INVALID_REQUEST");
            response.setErrorMessage("Request model is not valid!");
            return response;
        }

        if(request.getTrxDate() == null && CustomUtils.isNullOrEmpty(request.getTrxId())){
            response.setErrorCode("INVALID_REQUEST");
            response.setErrorMessage("Transaction date and Transaction id cannot be null!");
            return response;
        }
        return rateService.getCalculatedList(request);
    }
}
