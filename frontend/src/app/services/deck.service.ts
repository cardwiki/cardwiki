import { Injectable } from '@angular/core';

import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {DeckDetails} from '../dtos/deckDetails';
import {DeckSimple} from '../dtos/deckSimple';
import {DeckUpdate} from '../dtos/deckUpdate';

@Injectable({
  providedIn: 'root'
})
export class DeckService {

  private deckBaseUri: string = this.globals.backendUri + '/decks';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads a deck from the backend
   * @param id of deck to load
   */
  getDeckById(id: number): Observable<DeckDetails> {
    console.log('Load Deck with id ' + id);
    return this.httpClient.get<DeckDetails>(this.deckBaseUri + '/' + id);
  }

  /**
   * Updates deck in the backend
   * @param deckId id of the deck to update
   * @param deck update
   */
  updateDeck(deckId: number, deck: DeckUpdate): Observable<DeckDetails> {
    console.log('Update Deck with id ' + deckId);
    return this.httpClient.patch<DeckDetails>(this.deckBaseUri + '/' + deckId, deck);
  }

  /**
   * Creates a new card deck.
   *
   * @param deck the name of the card deck.
   */
  create(deck: DeckSimple): Observable<DeckDetails> {
    console.log('Create card deck: ' + deck.name);
    return this.httpClient.post<DeckDetails>(this.deckBaseUri, deck);
  }

  /**
   * Load all card decks containing {@code name} from
   * the backend.
   *
   * @param name to search for
   * @param offset of the page.
   * @param limit of results returned.
   */
  searchByName(name: string, offset: number, limit: number): Observable<DeckDetails[]> {
    console.log('search card decks: ' + name);
    const params = new HttpParams({
      fromObject: {
        name,
        offset: offset.toString(10),
        limit: limit.toString(10)
      }
    });
    return this.httpClient.get<DeckDetails[]>(this.deckBaseUri, { params });
  }

  /**
   * Creates a new card deck.
   *
   * @param deck the name of the card deck.
   */
  copy(deckId: number, deck: DeckSimple): Observable<DeckDetails> {
    console.log('Copy card deck with id ' + deckId);
    return this.httpClient.post<DeckDetails>(this.deckBaseUri + '/' + deckId + '/copy', deck);
  }
}
