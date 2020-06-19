import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import { CardDetails } from '../dtos/cardDetails';
import {CardSimple} from '../dtos/cardSimple';
import { ErrorHandlerService } from './error-handler.service';
import { tap } from 'rxjs/operators';
import { CardUpdate } from '../dtos/cardUpdate';
import { CardContent } from '../dtos/cardContent';
import {CardUpdateDto} from '../dtos/cardUpdateDto';

@Injectable({
  providedIn: 'root'
})
export class CardService {

  private deckBaseUri = this.globals.backendUri + '/decks'
  private cardBaseUri = this.globals.backendUri + '/cards'

  constructor(private httpClient: HttpClient, private globals: Globals, private errorHandler: ErrorHandlerService) {
  }

  /**
   * Persists card to the backend
   * @param card to persist
   */
  createCard(deckId: number, card: CardUpdateDto): Observable<CardDetails> {
    console.log('create card', deckId, card);
    return this.httpClient.post<CardDetails>(`${this.deckBaseUri}/${deckId}/cards`, card)
      .pipe(tap(null, this.errorHandler.handleError('Could not create Card')))
  }

  /**
   * Gets all cards for a specific deck
   * @param deckId of the deck for which cards to get
   */
  getCardsByDeckId(deckId: number): Observable<CardSimple[]> {
    console.log('get cards for deck with id ' + deckId);
    return this.httpClient.get<CardSimple[]>(`${this.deckBaseUri}/${deckId}/cards`)
      .pipe(tap(null, this.errorHandler.handleError('Could not fetch Cards')))
  }

  /**
   * Removes a card from its deck
   * @param cardId of the card to remove
   * @param message optional description why the card will be removed
   */
  removeCard(cardId: number, message?: string): Observable<CardContent> {
    console.log(`remove card with id ${cardId}`);
    const params = message ? { message } : {}
    return this.httpClient.delete<CardContent>(`${this.cardBaseUri}/${cardId}`, { params })
      .pipe(tap(null, this.errorHandler.handleError('Could not remove Card')))
  }

  editCard(cardId: number, card: CardUpdateDto): Observable<CardDetails> {
    console.log(`edit card with id ${cardId}: ${card}`);
    return this.httpClient.patch<CardDetails>(`${this.cardBaseUri}/${cardId}`, card)
      .pipe(tap(null, this.errorHandler.handleError('Could not edit Card')))
  }

  fetchCard(cardId: number): Observable<CardUpdate> {
    console.log(`fetch card with id ${cardId}`);
    return this.httpClient.get<CardUpdate>(`${this.cardBaseUri}/${cardId}`)
      .pipe(tap(null, this.errorHandler.handleError('Could not fetch Card')))
  }
}
