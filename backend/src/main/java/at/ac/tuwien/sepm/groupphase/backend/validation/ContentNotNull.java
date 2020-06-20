package at.ac.tuwien.sepm.groupphase.backend.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ContentNotNullValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentNotNull {
    String message() default "Cannot save card with an empty side.";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}

