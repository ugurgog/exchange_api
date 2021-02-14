package com.exchangerate.exchange.utils;

import com.exchangerate.exchange.model.ExchangeRateRequestModel;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class CustomUtils {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String getCurrentUserIP(HttpServletRequest request) {
        if (request == null)
            return null;
        return request.getRemoteAddr();
    }

    public static boolean isNullOrEmpty(String text) {
        if(text == null || text.isEmpty())
            return true;
        else
            return false;
    }

    public static String generateTrxId(){
        return UUID.randomUUID().toString();
    }

    public static String getRateServiceParams(ExchangeRateRequestModel request, String apiKey){
        String paramData = request.getDate() == null ? "latest" : dateTimeFormatter.format(request.getDate()).concat("/");
        paramData += apiKey.concat("/");
        paramData += request.getFromCurrency().concat("/").concat(request.getToCurrency());
        return paramData;
    }
}
