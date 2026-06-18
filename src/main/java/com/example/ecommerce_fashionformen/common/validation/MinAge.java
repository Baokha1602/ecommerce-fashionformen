package com.example.ecommerce_fashionformen.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MinAgeValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MinAge {
    String message() default "Bạn phải từ {value} tuổi trở lên";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    int value() default 13;
}
