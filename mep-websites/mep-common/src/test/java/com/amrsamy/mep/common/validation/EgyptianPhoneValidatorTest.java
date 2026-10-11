package com.amrsamy.mep.common.validation;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EgyptianPhoneValidatorTest {

    record AnyPhone(@EgyptianPhone String phone) {}

    record MobilePhone(@EgyptianPhone(mobileOnly = true) String phone) {}

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @ParameterizedTest
    @ValueSource(strings = {"01012345678", "011 2345 6789", "+20 12 3456 7890", "0020-155-123-4567", "(02) 2345 6789", "03 4567890"})
    void acceptsMobileAndLandlineFormats(String phone) {
        assertThat(validator.validate(new AnyPhone(phone))).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0101234567", "01312345678", "12345", "+44 20 7946 0958", "phone"})
    void rejectsInvalidNumbers(String phone) {
        assertThat(validator.validate(new AnyPhone(phone))).hasSize(1);
    }

    @Test
    void mobileOnlyRejectsLandlines() {
        assertThat(validator.validate(new MobilePhone("02 2345 6789"))).hasSize(1);
        assertThat(validator.validate(new MobilePhone("0101 234 5678"))).isEmpty();
    }

    @Test
    void blankIsLeftToNotBlank() {
        assertThat(validator.validate(new AnyPhone(""))).isEmpty();
        assertThat(validator.validate(new AnyPhone(null))).isEmpty();
    }
}
