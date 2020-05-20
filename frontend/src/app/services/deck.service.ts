import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Deck} from '../dtos/deck';
import {Observable} from 'rxjs';
import {DeckSimple} from '../dtos/deckSimple';

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
   * @param deck to update
   */
  updateDeck(deck: DeckSimple): Observable<DeckSimple> {
    console.log('Update Deck with id ' + deck.id);
    return this.httpClient.put<DeckSimple>(this.deckBaseUri + '/' + deck.id, deck);
  }
}
