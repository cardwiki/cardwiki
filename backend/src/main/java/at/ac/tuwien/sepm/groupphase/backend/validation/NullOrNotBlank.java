package at.ac.tuwien.sepm.groupphase.backend.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NullOrNotBlankValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NullOrNotBlank {
    String message() default "Invalid text";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
