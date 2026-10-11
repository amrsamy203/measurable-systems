package com.amrsamy.mep.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EgyptianPhoneValidator implements ConstraintValidator<EgyptianPhone, String> {

    private static final Pattern SEPARATORS = Pattern.compile("[\\s\\-.()]");
    private static final Pattern MOBILE = Pattern.compile("^(?:\\+20|0020|0)?1[0125]\\d{8}$");
    // Cairo/Giza 02 + 8 digits, Alexandria 03 + 7 digits, other governorates 0XX + 7 digits.
    private static final Pattern LANDLINE = Pattern.compile("^(?:\\+20|0020|0)[2-9]\\d{7,8}$");

    private boolean mobileOnly;

    @Override
    public void initialize(EgyptianPhone annotation) {
        this.mobileOnly = annotation.mobileOnly();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        String number = SEPARATORS.matcher(value.trim()).replaceAll("");
        return MOBILE.matcher(number).matches() || (!mobileOnly && LANDLINE.matcher(number).matches());
    }
}
