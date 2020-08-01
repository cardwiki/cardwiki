package at.ac.tuwien.sepm.groupphase.backend.profiles.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

public class Agent {
    private User user;
    private boolean persist;
    private EntityManager em;

    public Agent(EntityManager em, boolean persist, User user){
        this.persist = persist;
        this.user = user;
        this.em = em;
        if (persist) {
            this.em.persist(user);
            this.em.flush();
        }
    }

    public User getUser(){
        return user;
    }

    public Agent persist(){
        return new Agent(em, true, user);
    }

    public Agent unpersist(){
        return new Agent(em, false, user);
    }

    public static User defaultUser(String username){
        User user = new User();
        user.setUsername(username);
        user.setDescription("some description");
        user.setAuthId(username);
        user.setEnabled(true);
        user.setAdmin(false);
        user.setDeleted(false);
        user.setTheme("LIGHT");
        return user;
    }

    private void beforeReturn(Object o){
        if (persist) {
            this.em.persist(o);
            this.em.flush();
        }
    }

    public Agent makeAdmin() {
        user.setAdmin(true);
        beforeReturn(user);
        return this;
    }

    public Deck createDeck(){
        Deck deck = new Deck();
        deck.setName("some deck");
        deck.setCreatedBy(user);
        deck.setCreatedAt(LocalDateTime.of(2020, 1, 1, 1, 1));
        deck.setUpdatedAt(LocalDateTime.of(2020, 2, 1, 1, 1));
        user.getDecks().add(deck);
        beforeReturn(deck);
        return deck;
    }

    public Deck addCategory(Deck deck, Category category) {
        deck.getCategories().add(category);
        category.getDecks().add(deck);
        beforeReturn(deck);
        return deck;
    }

    public Card createCardIn(Deck deck, RevisionCreate revisionCreate){
        Card card = new Card();
        card.setDeck(deck);
        card.setLatestRevision(revisionCreate);
        revisionCreate.setType(Revision.Type.Values.CREATE); // Not set automatically within transaction
        revisionCreate.setCreatedBy(user);
        revisionCreate.setCard(card);
        card.getRevisions().add(revisionCreate);
        deck.getCards().add(card);
        card.setCreatedAt(LocalDateTime.of(2020, 1, 1, 1, 1));
        user.getRevisions().add(revisionCreate);
        beforeReturn(card);
        return card;
    }

    public Card createCardIn(Deck deck){
        RevisionCreate revisionCreate = new RevisionCreate();
        revisionCreate.setTextFront("front text");
        revisionCreate.setTextBack("back text");
        revisionCreate.setMessage("test message");
        return createCardIn(deck, revisionCreate);
    }

    public Comment createCommentIn(Deck deck){
        return createCommentIn(deck, "What a beautiful deck");
    }

    public Comment createCommentIn(Deck deck, String message) {
        Comment comment = new Comment();
        comment.setMessage(message);
        comment.setDeck(deck);
        comment.setCreatedBy(user);
        deck.setCreatedAt(LocalDateTime.of(2020, 1, 1, 1, 1));
        deck.setUpdatedAt(LocalDateTime.of(2020, 2, 1, 1, 1));
        deck.getComments().add(comment);
        user.getComments().add(comment);
        beforeReturn(comment);
        return comment;
    }

    public RevisionEdit editCard(Card card, RevisionEdit revisionEdit){
        revisionEdit.setType(Revision.Type.Values.EDIT); // Not set automatically within transaction
        card.setLatestRevision(revisionEdit);
        card.getRevisions().add(revisionEdit);
        revisionEdit.setCard(card);
        revisionEdit.setCreatedBy(user);
        user.getRevisions().add(revisionEdit);
        beforeReturn(revisionEdit);
        return revisionEdit;
    }

    public RevisionEdit editCard(Card card){
        RevisionEdit revisionEdit = new RevisionEdit();
        revisionEdit.setTextFront("front text");
        revisionEdit.setTextBack("back text");
        revisionEdit.setMessage("test message");
        return editCard(card, revisionEdit);
    }

    public Category createCategory(String name){
        Category category = new Category();
        category.setName(name);
        category.setCreatedAt(LocalDateTime.of(2020, 1, 1, 1, 1));
        category.setUpdatedAt(LocalDateTime.of(2020, 2, 1, 1, 1));
        category.setCreatedBy(user);
        user.getCategories().add(category);
        beforeReturn(category);
        return category;
    }

    public Category addSubcategory(Category parent, String childName) {
        Category child = createCategory(childName);
        child.setParent(parent);
        parent.getChildren().add(child);
        beforeReturn(child);
        return child;
    }

    public void deleteCard(Card card, RevisionDelete revisionDelete){
        revisionDelete.setCreatedBy(user);
        user.getRevisions().add(revisionDelete);
        card.setLatestRevision(revisionDelete);
        revisionDelete.setCard(card);
        user.getRevisions().add(revisionDelete);
        beforeReturn(revisionDelete);
    }

    public void deleteCard(Card card){
        RevisionDelete revisionDelete = new RevisionDelete();
        revisionDelete.setMessage("test message");
        deleteCard(card, revisionDelete);
    }

    public Deck addFavorite(Deck deck) {
        user.getFavorites().add(deck);
        deck.getFavoredBy().add(user);
        beforeReturn(deck);
        return deck;
    }

    public Progress createProgress(Card card, Progress.Status status, boolean reverse) {
        Progress progress = new Progress();
        progress.setDue(LocalDateTime.now().plusDays(3L));
        progress.setEasinessFactor(5);
        progress.setInterval(2);
        progress.setStatus(status);
        return createProgress(card, progress, reverse);
    }

    public Progress createProgressDue(Card card, boolean reverse) {
        Progress progress = new Progress();
        progress.setDue(LocalDateTime.now().minusMinutes(1L));
        progress.setEasinessFactor(5);
        progress.setInterval(2);
        progress.setStatus(Progress.Status.REVIEWING);
        return createProgress(card, progress, reverse);
    }

    public Progress createProgressNotDue(Card card, boolean reverse) {
        Progress progress = new Progress();
        progress.setDue(LocalDateTime.now().plusMinutes(1L));
        progress.setEasinessFactor(5);
        progress.setInterval(2);
        progress.setStatus(Progress.Status.REVIEWING);
        return createProgress(card, progress, reverse);
    }

    public Progress createProgress(Card card, Progress progress, boolean reverse) {
        progress.setId(new Progress.Id(user, card, reverse));
        user.getProgress().add(progress);
        beforeReturn(progress);
        return progress;
    }

    public Image createImage(String filename) {
        Image image = new Image();
        image.setFilename(filename);
        image.setCreatedBy(user);
        user.getImages().add(image);
        beforeReturn(image);
        return image;
    }
}
