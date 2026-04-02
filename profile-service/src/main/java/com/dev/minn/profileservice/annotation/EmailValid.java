package com.dev.minn.profileservice.annotation;

import com.dev.minn.profileservice.validator.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = EmailValidator.class)
public @interface EmailValid {

    String message() default "Email is invalid";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
