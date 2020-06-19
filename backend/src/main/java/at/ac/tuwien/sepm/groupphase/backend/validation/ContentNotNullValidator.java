package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RevisionEditDto;
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
       } else if (object instanceof RevisionEditDto) {
           RevisionEditDto revisionInputDto = (RevisionEditDto) object;
           return (revisionInputDto.getTextFront() != null || revisionInputDto.getImageFront() != null)
               && (revisionInputDto.getTextBack() != null || revisionInputDto.getImageBack() != null);
       } else {
           throw new IllegalArgumentException("@ContentNotNull only applies to RevisionEdit and RevisionInputDto");
       }
   }
}
