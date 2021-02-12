package com.exchangerate.exchange.config;

import com.exchangerate.exchange.constants.ProjectConstants;
import com.exchangerate.exchange.utils.CustomUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

@Component
public class LogFilter extends GenericFilter {

    private static Logger logger = LoggerFactory.getLogger(LogFilter.class);
    private Gson gson = new Gson();

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) {
        HttpServletRequest request = (HttpServletRequest) req;
        String method = request.getMethod();
        String client = CustomUtils.getCurrentUserIP(request);
        String pathInfo = request.getRequestURI();
        String trxId = UUID.randomUUID().toString();
        MDC.put(ProjectConstants.TRX_KEY, trxId);

        long stime = System.currentTimeMillis();
        Throwable exception = null;
        try {
            chain.doFilter(request, response);

        } catch (Throwable th) {
            exception = th;
        } finally {

            long etime = System.currentTimeMillis() - stime;

            HttpServletResponse httpResponse = (HttpServletResponse) response;
            String json = "";

            Map<String, String[]> parameterMap = request.getParameterMap();
            if (parameterMap != null && !parameterMap.isEmpty()) {
                json = gson.toJson(parameterMap);
            }

            if (exception != null) {
                logger.error("::doFilter {} ip:{} path:{} time:{} response:{} params:{}", method, client, pathInfo,
                        etime, httpResponse.getStatus(), json, exception);
            } else {
                logger.info("::doFilter {} ip:{} path:{} time:{} response:{} params:{}", method, client, pathInfo,
                        etime, httpResponse.getStatus(), json);
            }
            MDC.clear();
        }

    }
}
