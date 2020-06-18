package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("create")
public class RevisionCreate extends RevisionEdit {
}
