package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean admin;

    public ApplicationUser() {
    }

    public ApplicationUser(Long id) {
        this.id = id;
    }

    public ApplicationUser(Long id, Boolean admin) {
        this.id = id;
        this.admin = admin;
    }


    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
