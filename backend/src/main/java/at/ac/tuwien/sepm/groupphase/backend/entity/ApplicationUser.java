package at.ac.tuwien.sepm.groupphase.backend.entity;

//TODO: replace this class with a correct ApplicationUser Entity implementation
public class ApplicationUser {

    private String email;
    private String password;
    private Boolean admin;

    public ApplicationUser() {
    }

    public ApplicationUser(String email, String password, Boolean admin) {
        this.email = email;
        this.password = password;
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
