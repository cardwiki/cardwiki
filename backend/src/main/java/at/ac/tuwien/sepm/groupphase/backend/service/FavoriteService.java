package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.exception.AuthenticationRequiredException;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.InsufficientAuthorizationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FavoriteService {
    /**
     * Add a deck as favorite of the user
     *
     * @param userId id of the user to which it should be added
     * @param deckId id of the favorite deck
     * @return created favorite
     * @throws AuthenticationRequiredException if no authenticated user could be found
     * @throws InsufficientAuthorizationException if the authenticated user does not match the {@code userId}
     * @throws DeckNotFoundException if no deck with this id exists
     */
    Deck addFavorite(Long userId, Long deckId);

    /**
     * Get favorites of the user
     *
     * @param userId id of the user to get the favorites from
     * @param pageable paging parameters
     * @return page containing favorites
     * @throws AuthenticationRequiredException if no authenticated user could be found
     * @throws InsufficientAuthorizationException if the authenticated user does not match the {@code userId}
     */
    Page<Deck> getFavorites(Long userId, Pageable pageable);

    /**
     * Remove a deck from the favorites of the user
     *
     * @param userId id of the user from which it should be removed
     * @param deckId id of the deck which should be removed
     * @throws AuthenticationRequiredException if no authenticated user could be found
     * @throws InsufficientAuthorizationException if the authenticated user does not match the {@code userId}
     * @throws DeckNotFoundException if no favorite with this {@code deckId} exists
     */
    void removeFavorite(Long userId, Long deckId);
}
