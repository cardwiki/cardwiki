import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import { CardContent } from '../dtos/cardContent';
import { CardDetails } from '../dtos/cardDetails';

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
      : `${this.globals.backendUri}/decks/${deckId}/cards`
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
