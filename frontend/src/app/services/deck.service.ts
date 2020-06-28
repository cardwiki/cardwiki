import { Injectable } from '@angular/core';

import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {DeckDetails} from '../dtos/deckDetails';
import {DeckSimple} from '../dtos/deckSimple';
import {DeckUpdate} from '../dtos/deckUpdate';
import { ErrorHandlerService } from './error-handler.service';
import { tap } from 'rxjs/operators';
import { Page } from '../dtos/page';
import { Pageable } from '../dtos/pageable';
import {RevisionDetailed} from '../dtos/revisionDetailed';

@Injectable({
  providedIn: 'root'
})
export class DeckService {

  private deckBaseUri: string = this.globals.backendUri + '/decks';

  constructor(private httpClient: HttpClient, private globals: Globals, private errorHandler: ErrorHandlerService) {
  }

  /**
   * Loads a deck from the backend
   * @param id of deck to load
   */
  getDeckById(id: number): Observable<DeckDetails> {
    console.log('Load Deck with id ' + id);
    return this.httpClient.get<DeckDetails>(this.deckBaseUri + '/' + id)
      .pipe(tap(null, this.errorHandler.handleError('Could not fetch Deck')));
  }

  /**
   * Gets csv-data for a specific deck from the backend.
   * @param id of deck to get.
   */
  exportDeckAsCsv(id: number) {
    console.log('Load Deck with id ' + id);
    const headers = new HttpHeaders()
      .append('Accept', 'text/csv')
      .append('Content-Type', 'text/html');

    return this.httpClient.get(this.deckBaseUri + '/' + id,  { headers, responseType: 'text' })
      .pipe(tap(null, this.errorHandler.handleError('Could not fetch Deck')));
  }

  /**
   * Updates deck in the backend
   * @param deckId id of the deck to update
   * @param deck update
   */
  updateDeck(deckId: number, deck: DeckUpdate): Observable<DeckDetails> {
    console.log('Update Deck with id ' + deckId);
    return this.httpClient.patch<DeckDetails>(this.deckBaseUri + '/' + deckId, deck)
      .pipe(tap(null, this.errorHandler.handleError('Could not update Deck')));
  }

  /**
   * Creates a new card deck.
   *
   * @param deck the name of the card deck.
   */
  create(deck: DeckSimple): Observable<DeckDetails> {
    console.log('Create card deck: ' + deck.name);
    return this.httpClient.post<DeckDetails>(this.deckBaseUri, deck)
      .pipe(tap(null, this.errorHandler.handleError('Could not create Deck')));
  }

  /**
   * Fetch page of decks containing {@code name}
   *
   * @param name to search for
   * @param pageable config for pagination
   */
  searchByName(name: string, pageable: Pageable): Observable<Page<DeckDetails>> {
    console.log('search card decks: ' + name);
    const params = {
      name,
      ...pageable.toObject(),
    };
    return this.httpClient.get<Page<DeckDetails>>(this.deckBaseUri, { params })
      .pipe(tap(null, this.errorHandler.handleError('Could not search for Decks')));
  }

  /**
   * Creates a new card deck.
   *
   * @param deck the name of the card deck.
   */
  copy(deckId: number, deck: DeckSimple): Observable<DeckDetails> {
    console.log('Copy card deck with id ' + deckId);
    return this.httpClient.post<DeckDetails>(this.deckBaseUri + '/' + deckId + '/copy', deck)
      .pipe(tap(null, this.errorHandler.handleError('Could not fork Deck')));
  }

  delete(deckId: number): Observable<void> {
    console.log('Delete deck ' + deckId);
    return this.httpClient.delete<void>(this.deckBaseUri + '/' + deckId)
      .pipe(tap(null, this.errorHandler.handleError('Could not delete deck ' + deckId)));
  }


  import(deckId: number, data: FormData): Observable<DeckDetails> {
    console.log('import cards to deck ' + deckId);
    return this.httpClient.post<DeckDetails>(this.deckBaseUri + '/' + deckId + '/cards', data)
      .pipe(tap(null, this.errorHandler.handleError('Could not import cards to deck ' + deckId)));
  }
  /**
   * Fetches card revisions for cards in a deck
   *
   * @param deckId of the deck to fetch revisions of
   * @param pageable config for pagination
   */
  fetchRevisions(deckId: number, pageable: Pageable): Observable<Page<RevisionDetailed>> {
    console.log(`fetch revisions for cards in deck with id ${deckId}`);
    return this.httpClient.get<Page<RevisionDetailed>>(this.deckBaseUri + '/' + deckId + '/revisions', { params: pageable.toHttpParams() })
      .pipe(tap(null, this.errorHandler.handleError('Could not fetch Revisions')));
  }
}
