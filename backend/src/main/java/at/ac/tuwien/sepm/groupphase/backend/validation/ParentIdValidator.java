package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ParentIdValidator implements ConstraintValidator<ParentIdValid, Category> {

        @Override
        public void initialize(ParentIdValid idToValidate) {
        }

        @Override
        public boolean isValid(Category parent, ConstraintValidatorContext cxt) {
            return (parent == null || parent.getId() != null);
        }

}
