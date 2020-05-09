package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class ApplicationUser {

    @Id
    @Column(nullable = false, unique = true)
    private String oauthId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private boolean admin;

    @Column(nullable = false)
    private boolean enabled;

    public ApplicationUser() {
    }

    public ApplicationUser(String oauthId, String username, boolean admin, boolean enabled) {
        this.oauthId = oauthId;
        this.username = username;
        this.admin = admin;
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getOauthId() {
        return oauthId;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled(){
        return enabled;
    }
}
