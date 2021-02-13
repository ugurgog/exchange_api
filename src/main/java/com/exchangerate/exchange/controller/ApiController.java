package com.exchangerate.exchange.controller;

import com.exchangerate.exchange.model.*;
import com.exchangerate.exchange.service.IExchangeRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class ApiController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final IExchangeRateService exchangeRateService;

    public ApiController(IExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/status")
    public String stat() {
        return "OK";
    }

    @ApiIgnore
    @GetMapping("/")
    public RedirectView home() {
        return new RedirectView("swagger-ui/index.html");
    }

    @PostMapping("/get-rate")
    public ExchangeRateResponseModel exchangeRate(@RequestBody ExchangeRateRequestModel request) {
        ExchangeRateResponseModel response = exchangeRateService.getRate(request);
        return response;
    }

    @PostMapping("/calculate-rate")
    public CalculateRateResponseModel calculateRate(@RequestBody CalculateRateRequestModel request) {
        return exchangeRateService.calculateRate(request);
    }

    @PostMapping("/calculate-rate/list")
    public RateListResponseModel calculateRateList(@RequestBody RateListRequestModel request) {
        return exchangeRateService.getCalculatedList(request);
    }
}
