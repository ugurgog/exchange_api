package com.exchangerate.exchange.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class CustomUtils {

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
}
