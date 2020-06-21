package at.ac.tuwien.sepm.groupphase.backend.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {
   public void initialize(NullOrNotBlank constraint) {
   }

   public boolean isValid(String str, ConstraintValidatorContext context) {
       return str == null || str.trim().length() > 0;
   }
}
