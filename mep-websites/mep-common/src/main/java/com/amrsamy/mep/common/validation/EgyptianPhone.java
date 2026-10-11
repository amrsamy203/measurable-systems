package com.amrsamy.mep.common.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An Egyptian mobile (010/011/012/015) or landline number, with or without +20.
 * Spaces, dashes, dots and brackets are ignored. Blank values are valid; combine with
 * {@code @NotBlank} when the number is required.
 */
@Documented
@Constraint(validatedBy = EgyptianPhoneValidator.class)
@Target({FIELD, PARAMETER, RECORD_COMPONENT})
@Retention(RUNTIME)
public @interface EgyptianPhone {

    String message() default "Enter a valid Egyptian phone number, e.g. 010 1234 5678";

    boolean mobileOnly() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
