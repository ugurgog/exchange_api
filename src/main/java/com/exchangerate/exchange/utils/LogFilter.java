package com.exchangerate.exchange.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

@Component
public class LogFilter implements Filter {

	private static Logger logger = LoggerFactory.getLogger(LogFilter.class);
	private Gson gson = new Gson();
	private boolean logParams = false;

	private int THRESHOLD_VALUE = 10000;

	private Set<String> excluded;

	private boolean logAll = true;

	@Override
	public void init(FilterConfig config) throws ServletException {
		String logAllStr = config.getInitParameter("logAll");
		String thresholdStr = config.getInitParameter("threshold");
		String logParamsStr = config.getInitParameter("logParams");

		if (logParamsStr != null && !logParamsStr.isEmpty()) {
			logParams = "true".equals(logParamsStr);
		}

		if (logAllStr != null && !logAllStr.isEmpty()) {
			logAll = "true".equals(config.getInitParameter("logAll"));
		}

		if (thresholdStr != null) {
			try {
				THRESHOLD_VALUE = Integer.parseInt(thresholdStr);
			} catch (Exception e) {
			}
		}

		excluded = new HashSet<>();
		excluded.add("/act/stat");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;

		String pathInfo = request.getRequestURI();

		if (pathInfo == null || pathInfo.isEmpty()) {
			pathInfo = (String) request
					.getAttribute("org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping");
		}

		if (excluded != null && excluded.contains(pathInfo)) {
			chain.doFilter(request, response);
			return;
		}

		String method = request.getMethod();
		String client = CustomUtils.getCurrentUserIP(request);

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

			if (logParams) {
				Map<String, String[]> parameterMap = request.getParameterMap();
				if (parameterMap != null && !parameterMap.isEmpty()) {
					json = gson.toJson(parameterMap);
				}
			}

			if (exception != null) {
				logger.error("::doFilter {} ip:{} path:{} time:{} response:{} params:{}", method, client, pathInfo,
						etime, httpResponse.getStatus(), json, exception);

			} else if (etime > THRESHOLD_VALUE) {
				logger.warn("::doFilter {} slow ip:{} path:{} time:{} response:{} params:{}", method, client, pathInfo,
						etime, httpResponse.getStatus(), json);

			} else if (logAll && (excluded == null || !excluded.contains(pathInfo))) {

				logger.info("::doFilter {} ip:{} path:{} time:{} response:{} params:{}", method, client, pathInfo,
						etime, httpResponse.getStatus(), json);
			}
		}
	}

	@Override
	public void destroy() {

	}

	public Gson getGson() {
		return gson;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}

	public boolean isLogParams() {
		return logParams;
	}

	public void setLogParams(boolean logParams) {
		this.logParams = logParams;
	}

	public Set<String> getExcluded() {
		return excluded;
	}

	public void setExcluded(Set<String> excluded) {
		this.excluded = excluded;
	}
}