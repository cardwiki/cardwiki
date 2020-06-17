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
           RevisionEdit revisionEdit = (RevisionEdit) object;
           return (revisionEdit.getTextFront() != null || revisionEdit.getImageFront() != null)
               && (revisionEdit.getTextBack() != null || revisionEdit.getImageBack() != null);
       } else if (object instanceof RevisionEditInquiryDto) {
           RevisionEditInquiryDto revisionEditInquiryDto = (RevisionEditInquiryDto) object;
           return (revisionEditInquiryDto.getTextFront() != null || revisionEditInquiryDto.getImageFront() != null)
               && (revisionEditInquiryDto.getTextBack() != null || revisionEditInquiryDto.getImageBack() != null);
       } else {
           throw new IllegalArgumentException("@ContentNotNull only applies to RevisionEdit and RevisionEditInquiryDto");
       }
   }
}
