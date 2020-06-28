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
   * @param deckName of the deck where the card is copied from
   */
  copy(card: CardSimple, deckName: string) {
    console.log('Copy to clipboard');
    this.cardService.fetchCard(card.id).subscribe(cardUpdate => {
      cardUpdate.message = `Copied from ${deckName}`;
      const clipboard = this.clipboard$.value;
      clipboard.push(cardUpdate);
      this.set(clipboard);
    });
  }

  /**
   * Pastes cards from the clipboard to a specific deck
   * @param deckId of the deck to which cards are pasted
   * @param cards to paste
   */
  paste(deckId: number, cards: CardUpdate[]): Observable<CardSimple[]> {
    console.log('Paste from clipboard');
    this.set(this.clipboard$.value.filter(c => !cards.includes(c)));
    return forkJoin(cards.map(card => this.cardService.createCard(deckId, card)));
  }

  /**
   * Removes a card from the clipboard
   * @param card to remove
   */
  remove(card: CardUpdate) {
    console.log('Remove from clipboard');
    this.set(this.clipboard$.value.filter(c => c !== card));
  }

  /**
   * Updates clipboard and localstorage
   * @param clipboard new clipboard state
   */
  set(clipboard: CardUpdate[]) {
    this.clipboard$.next(clipboard);
    localStorage.setItem('clipboard', JSON.stringify(clipboard));
  }

}
