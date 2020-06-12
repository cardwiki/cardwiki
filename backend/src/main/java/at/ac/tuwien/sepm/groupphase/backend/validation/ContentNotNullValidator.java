package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RevisionEditInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ContentNotNullValidator implements ConstraintValidator<ContentNotNull, Object> {
   public void initialize(ContentNotNull constraint) {
   }

   public boolean isValid(Object object, ConstraintValidatorContext context) {
       if (object instanceof RevisionEdit) {
           RevisionEdit re = (RevisionEdit) object;
           return (re.getTextFront() != null || re.getImageFront() != null) && (re.getTextBack() != null || re.getImageBack() != null);
       } else if (object instanceof RevisionEditInquiryDto) {
           RevisionEditInquiryDto reid = (RevisionEditInquiryDto) object;
           return (reid.getTextFront() != null || reid.getImageFront() != null) && (reid.getTextBack() != null || reid.getImageBack() != null);
       } else {
           throw new IllegalArgumentException("@ContentNotNull only applies to RevisionEdit and RevisionEditInquiryDto");
       }
   }
}
