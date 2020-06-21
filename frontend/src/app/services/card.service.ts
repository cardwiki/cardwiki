import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {CardSimple} from '../dtos/cardSimple';
import { ErrorHandlerService } from './error-handler.service';
import { tap } from 'rxjs/operators';
import { CardUpdate } from '../dtos/cardUpdate';

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
  createCard(deckId: number, card: CardUpdate): Observable<CardSimple> {
    console.log('create card', deckId, card);
    const dto = this.toCardUpdateDto(card)
    return this.httpClient.post<CardSimple>(`${this.deckBaseUri}/${deckId}/cards`, dto)
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
  removeCard(cardId: number, message?: string): Observable<void> {
    console.log(`remove card with id ${cardId}`);
    const params = message ? { message } : {}
    return this.httpClient.delete<void>(`${this.cardBaseUri}/${cardId}`, { params })
      .pipe(tap(null, this.errorHandler.handleError('Could not remove Card')))
  }

  editCard(cardId: number, card: CardUpdate): Observable<CardSimple> {
    console.log(`edit card with id ${cardId}: ${card}`);
    const dto = this.toCardUpdateDto(card)
    return this.httpClient.patch<CardSimple>(`${this.cardBaseUri}/${cardId}`, dto)
      .pipe(tap(null, this.errorHandler.handleError('Could not edit Card')))
  }

  fetchCard(cardId: number): Observable<CardUpdate> {
    console.log(`fetch card with id ${cardId}`);
    return this.httpClient.get<CardUpdate>(`${this.cardBaseUri}/${cardId}`)
      .pipe(tap(null, this.errorHandler.handleError('Could not fetch Card')))
  }

  /**
   * Permanently deletes a card.
   *
   * @param cardId of the card to delete.
   */
  deleteCard(cardId: number): Observable<void> {
    console.log(`Delete card ${cardId}`);
    return this.httpClient.delete<void>(`${this.cardBaseUri}/${cardId}`)
      .pipe(tap(null, this.errorHandler.handleError('Could not delete Card')));
  }

  private toCardUpdateDto(card: CardUpdate): CardUpdateDto {
    const cardUpdateDto = new CardUpdateDto(card.textFront, card.textBack, null, null, card.message);
    cardUpdateDto.imageFrontFilename = card.imageFront?.filename || null;
    cardUpdateDto.imageBackFilename = card.imageBack?.filename || null;

    return cardUpdateDto
  }
}

class CardUpdateDto implements CardTextContent {
  constructor(
    public textFront: string,
    public textBack: string,
    public imageFrontFilename: string,
    public imageBackFilename: string,
    public message: string = null) {}
}
