package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class WhoAmIDto {
    private String id;
    private boolean hasAccount;
    private boolean isAdmin;

    public WhoAmIDto() {
    }

    public String getId() {
        return id;
    }

    public boolean isHasAccount() {
        return hasAccount;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHasAccount(boolean hasAccount) {
        this.hasAccount = hasAccount;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
