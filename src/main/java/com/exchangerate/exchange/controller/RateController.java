package com.exchangerate.exchange.controller;

import com.exchangerate.exchange.model.*;
import com.exchangerate.exchange.service.IRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class RateController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

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
    public ExchangeRateResponseModel exchangeRate(@RequestBody ExchangeRateRequestModel request) {
        ExchangeRateResponseModel response = rateService.getRate(request);
        return response;
    }

    @PostMapping("/calculate-rate")
    public CalculateRateResponseModel calculateRate(@RequestBody CalculateRateRequestModel request) {
        return rateService.calculateRate(request);
    }

    @PostMapping("/calculate-rate/list")
    public RateListResponseModel calculateRateList(@RequestBody RateListRequestModel request) {
        return rateService.getCalculatedList(request);
    }
}
