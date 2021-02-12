package com.exchangerate.exchange.model;

public enum ProtocolEnum {
    Rate_SymbolAndBase("{1}?base={2}&symbols={3}", true, true,3)
    ;

    private final String path;
    private final boolean usePath;
    private final boolean retryOnException;
    private final int retryCount;

    private ProtocolEnum(String path, boolean usePath, boolean retryOnException, int retryCount) {
        this.path = path;
        this.usePath = usePath;
        this.retryOnException = retryOnException;
        this.retryCount = retryCount;
    }

    public String getPath() {
        return path;
    }

    public boolean isUsePath() {
        return usePath;
    }

    public boolean isRetryOnException() {
        return retryOnException;
    }

    public int getRetryCount() {
        return retryCount;
    }
}
