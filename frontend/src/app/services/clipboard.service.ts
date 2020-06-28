import {Injectable} from '@angular/core';
import {BehaviorSubject, forkJoin, Observable, of} from 'rxjs';
import {CardUpdate} from '../dtos/cardUpdate';
import {CardService} from './card.service';
import {CardSimple} from '../dtos/cardSimple';

@Injectable({
  providedIn: 'root'
})
export class ClipboardService {

  public clipboard$: BehaviorSubject<CardUpdate[]>;

  constructor(private cardService: CardService) {
    this.clipboard$ = new BehaviorSubject(JSON.parse(localStorage.getItem('clipboard') ?? '[]'));
  }

  /**
   * Copies card to the clipboard
   * @param card to copy
   */
  copy(card: CardUpdate) {
    const clipboard = JSON.parse(localStorage.getItem('clipboard') ?? '[]');
    clipboard.push(card);
    this.clipboard$.next(clipboard);
    localStorage.setItem('clipboard', JSON.stringify(clipboard));
  }

  /**
   * Pastes cards from the clipboard to a specific deck
   * @param deckId to which cards cards are pasted
   */
  paste(deckId: number): Observable<CardSimple[]> {
    const clipboard: CardUpdate[] = JSON.parse(localStorage.getItem('clipboard') ?? '[]');
    this.clipboard$.next([]);
    localStorage.removeItem('clipboard');
    return forkJoin(clipboard.map(card => this.cardService.createCard(deckId, card)));
  }

  /**
   * Removes a card from the clipboard
   * @param card to remove
   */
  remove(card: CardUpdate) {
    const clipboard = this.clipboard$.value.filter(c => c !== card);
    this.clipboard$.next(clipboard);
    localStorage.setItem('clipboard', JSON.stringify(clipboard));
  }
}
