package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategorySimpleDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ParentIdValidator implements ConstraintValidator<ParentIdValid, CategorySimpleDto> {

        @Override
        public void initialize(ParentIdValid idToValidate) {
        }

        @Override
        public boolean isValid(CategorySimpleDto parent, ConstraintValidatorContext cxt) {
            return (parent == null || parent.getId() != null);
        }

}
