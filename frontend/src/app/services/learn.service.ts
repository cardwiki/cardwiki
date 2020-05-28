import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {CardDetails} from '../dtos/cardDetails';
import {LearnAttempt} from '../dtos/learnAttempt';

@Injectable({
  providedIn: 'root'
})
export class LearnService {

  private learnBaseUri: string = this.globals.backendUri + '/learn';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads the next card to learn for a specific deck from the backend
   * @param id of deck to learn
   */
  getNextCard(deckId: number): Observable<CardDetails> {
    console.log('Get next Card of Deck with id ' + deckId);
    const params = new HttpParams().append('deckId', String(deckId));
    return this.httpClient.get<CardDetails>(this.learnBaseUri + '/next', { params });
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
