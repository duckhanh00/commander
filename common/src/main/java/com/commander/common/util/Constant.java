package com.commander.common.util;

public class Constant {
    public static final String PREFIX_RESPONSE_CODE;
    public static final String SYSTEM_USER = "system";

    static {
        PREFIX_RESPONSE_CODE = System.getProperty("response.prefix-code", "PMH-");
    }

    private Constant() {
        throw new UnsupportedOperationException();
    }
}
