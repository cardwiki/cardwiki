package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("delete")
public class RevisionDelete extends Revision {
}
