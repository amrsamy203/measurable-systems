package com.amrsamy.mep.thermiq.service;

public enum Billing {
    MONTHLY,
    YEARLY;

    /** Anything other than "yearly" falls back to monthly, so a mistyped link still shows prices. */
    public static Billing fromParam(String value) {
        return "yearly".equalsIgnoreCase(value) ? YEARLY : MONTHLY;
    }

    public String getParam() {
        return name().toLowerCase();
    }
}
