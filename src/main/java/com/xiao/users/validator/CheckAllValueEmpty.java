package com.xiao.users.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target( { ElementType.TYPE})
@Constraint(validatedBy = FieldValueEmptyValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckAllValueEmpty {
    String message() default "Enter at least 1 piece of information";
    String[] fields() default {};
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
