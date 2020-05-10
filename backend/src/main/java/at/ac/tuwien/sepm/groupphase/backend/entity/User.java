package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String oAuthId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private boolean admin;

    @Column(nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private Set<Card> cards = new HashSet<>();

    public User() {
    }

    public User(String oAuthId, String username, boolean admin, boolean enabled) {
        this.oAuthId = oAuthId;
        this.username = username;
        this.admin = admin;
        this.enabled = enabled;
    }

    public void dismissCard(Card card) {
        if (!cards.remove(card))
            throw new NoSuchElementException("Tried to dismiss card which is not yet associated with user");
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

    public String setOAuthId() {
        return oAuthId;
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

    public void setOAuthId(String oauthId) {
        this.oAuthId = oauthId;
    }

    public Set<Card> getCards() {
        return cards;
    }

    public void setCards(Set<Card> cards) {
        this.cards = cards;
    }

    @Override
    public String toString() {
        return "ApplicationUser{" +
            "oAuthId='" + oAuthId + '\'' +
            ", username='" + username + '\'' +
            ", admin=" + admin +
            ", enabled=" + enabled +
            '}';
    }
}
