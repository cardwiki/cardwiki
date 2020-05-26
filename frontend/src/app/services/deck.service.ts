import { Injectable } from '@angular/core';

import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Deck} from '../dtos/deck';
import {DeckSimple} from '../dtos/deck-simple';
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
  getDeckById(id: number): Observable<Deck> {
    console.log('Load Deck with id ' + id);
    return this.httpClient.get<Deck>(this.deckBaseUri + '/' + id);
  }

  /**
   * Updates deck in the backend
   * @param deck update
   */
  updateDeck(deck: DeckUpdate): Observable<Deck> {
    console.log('Update Deck with id ' + deck.id);
    return this.httpClient.patch<Deck>(this.deckBaseUri + '/' + deck.id, deck);
  }

  /**
   * Creates a new card deck.
   *
   * @param deck the name of the card deck.
   */
  create(deck: DeckSimple): Observable<Deck> {
    console.log('Create card deck: ' + deck.name);
    return this.httpClient.post<Deck>(this.deckBaseUri, deck);
  }

  /**
   * Load all card decks containing {@code name} from
   * the backend.
   *
   * @param name to search for
   * @param offset of the page.
   * @param limit of results returned.
   */
  searchByName(name: string, offset: number, limit: number): Observable<Deck[]> {
    console.log('search card decks: ' + name);
    const params = new HttpParams({
      fromObject: {
        name,
        offset: offset.toString(10),
        limit: limit.toString(10)
      }
    });
    return this.httpClient.get<Deck[]>(this.deckBaseUri, { params });
  }
}
