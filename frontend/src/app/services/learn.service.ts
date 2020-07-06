import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {LearnAttempt} from '../dtos/learnAttempt';
import { CardSimple } from '../dtos/cardSimple';
import { Pageable } from '../dtos/pageable';

@Injectable({
  providedIn: 'root'
})
export class LearnService {

  private learnBaseUri: string = this.globals.backendUri + '/learn';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads the next cards to learn for a specific deck from the backend
   * @param deckId id of deck to learn
   */
  getNextCards(deckId: number, pageable: Pageable): Observable<CardSimple[]> {
    console.log('Get next cards of Deck with id ' + deckId);
    const params = {
      deckId: String(deckId),
      ...pageable.toObject(),
    };
    return this.httpClient.get<CardSimple[]>(this.learnBaseUri + '/next', { params });
  }

  /**
   * Sends the result of the last learn attempt to the backend
   * @param learnAttempt object containing the card id and the status to be submitted
   */
  sendAttemptStatus(learnAttempt: LearnAttempt) {
    console.log('Sending status for most recent attempt for Card with id ' + learnAttempt.cardId);
    return this.httpClient.post<LearnAttempt>(this.learnBaseUri + '/attempt', learnAttempt);
  }
}
