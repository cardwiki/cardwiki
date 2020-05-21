import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import { CardContent } from '../dtos/cardContent';
import { CardDetails } from '../dtos/cardDetails';
import {CardSimple} from '../dtos/cardSimple';

@Injectable({
  providedIn: 'root'
})
export class CardService {

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Persists card to the backend
   * @param card to persist
   */
  createCard(deckId: number, card: CardContent): Observable<CardDetails> {
    console.log('create card', deckId, card);
    return this.httpClient.post<CardDetails>(this.getCardUri(deckId), card);
  }

  private getCardUri(deckId: number, cardId?: number) {
    return typeof cardId !== 'undefined' ?
      `${this.globals.backendUri}/decks/${deckId}/cards/${cardId}`
      : `${this.globals.backendUri}/decks/${deckId}/cards`;
  }

  /**
   * Gets all cards for a specific deck
   * @param deckId of the deck for which cards to get
   */
  getCardsByDeckId(deckId: number): Observable<CardSimple[]> {
    console.log('get cards for deck with id ' + deckId);
    return this.httpClient.get<CardSimple[]>(this.getCardUri(deckId));
  }

  /**
   * Removes a card from its deck
   * @param deckId of the card's deck
   * @param cardId of the card to remove
   */
  removeCardFromDeck(deckId: number, cardId: number): Observable<CardSimple> {
    console.log(`remove card with id ${cardId} from deck ${deckId}`);
    return this.httpClient.delete<CardSimple>(this.getCardUri(deckId, cardId));
  }

  editCard(deckId: number, cardId: number, card: CardContent): Observable<CardDetails> {
    console.log(`edit card with id ${cardId} from deck ${deckId}: ${card}`);
    return this.httpClient.patch<CardDetails>(this.getCardUri(deckId, cardId), card);
  }

  fetchCard(deckId: number, cardId: number): Observable<CardDetails> {
    console.log(`fetch card with id ${cardId} from deck ${deckId}`);
    return this.httpClient.get<CardDetails>(this.getCardUri(deckId, cardId));
  }
}
