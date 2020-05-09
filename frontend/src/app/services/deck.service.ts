import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Deck} from '../dtos/deck';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DeckService {

  private deckBaseUri: string = this.globals.backendUri + '/decks';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads a specific deck from the backend
   * @param id of deck to load
   */
  getDeckById(id: number): Observable<Deck> {
    console.log('Load Deck with id ' + id);
    return this.httpClient.get<Deck>(this.deckBaseUri + '/' + id);
  }

  /**
   * Persists deck to the backend
   * @param deck to persist
   */
  createDeck(deck: Deck): Observable<Deck> {
    console.log('Create deck with name ' + deck.name);
    return this.httpClient.post<Deck>(this.deckBaseUri, deck);
  }

  /**
   * Updates deck in the backend
   * @param deck to update
   */
  updateDeck(deck: Deck): Observable<Deck> {
    console.log('Update Deck with id ' + deck.id);
    return this.httpClient.put<Deck>(this.deckBaseUri + '/' + deck.id, deck);
  }
}
