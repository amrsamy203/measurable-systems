package com.amrsamy.mep.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Contact and identity details of the (fictional) company a site represents.
 * Bound from {@code company.*} so every value can be overridden per environment.
 */
@ConfigurationProperties(prefix = "company")
public record CompanyProfile(
        String name,
        String tagline,
        String phone,
        String email,
        String whatsapp,
        String address,
        String hours,
        int founded,
        @DefaultValue("https://amrsamy203.github.io") String portfolioUrl,
        @DefaultValue("true") boolean demo) {

    public String phoneHref() {
        return "tel:" + digits(phone, true);
    }

    public String whatsappHref() {
        return whatsapp == null || whatsapp.isBlank() ? null : "https://wa.me/" + digits(whatsapp, false);
    }

    public String emailHref() {
        return "mailto:" + email;
    }

    private static String digits(String value, boolean keepPlus) {
        if (value == null) {
            return "";
        }
        String cleaned = value.replaceAll("[^0-9+]", "");
        return keepPlus ? cleaned : cleaned.replace("+", "");
    }
}
