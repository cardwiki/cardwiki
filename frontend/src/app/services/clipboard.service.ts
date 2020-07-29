import {Injectable} from '@angular/core';
import {BehaviorSubject, forkJoin, Observable} from 'rxjs';
import {CardUpdate} from '../dtos/cardUpdate';
import {CardService} from './card.service';
import {CardSimple} from '../dtos/cardSimple';

@Injectable({
  providedIn: 'root'
})
export class ClipboardService {

  private clipboardSubject$: BehaviorSubject<CardUpdate[]>;
  public clipboard$: Observable<CardUpdate[]>;

  constructor(private cardService: CardService) {
    this.clipboardSubject$ = new BehaviorSubject(JSON.parse(localStorage.getItem('clipboard') ?? '[]'));
    this.clipboard$ = this.clipboardSubject$.asObservable();
  }

  /**
   * Copies card to the clipboard
   * @param card to copy
   * @param deckName of the deck where the card is copied from
   */
  copy(card: CardSimple, deckName: string): void {
    console.log('Copy to clipboard');
    this.cardService.fetchCard(card.id).subscribe(cardUpdate => {
      cardUpdate.message = `Copied from ${deckName}`;
      const clipboard = this.clipboardSubject$.value;
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
    this.set(this.clipboardSubject$.value.filter(c => !cards.includes(c)));
    return forkJoin(cards.map(card => this.cardService.createCard(deckId, card)));
  }

  /**
   * Removes a card from the clipboard
   * @param card to remove
   */
  remove(card: CardUpdate): void {
    console.log('Remove from clipboard');
    this.set(this.clipboardSubject$.value.filter(c => c !== card));
  }

  /**
   * Updates clipboard and localstorage
   * @param clipboard new clipboard state
   */
  set(clipboard: CardUpdate[]): void {
    this.clipboardSubject$.next(clipboard);
    localStorage.setItem('clipboard', JSON.stringify(clipboard));
  }

  /**
   * Clears clipboard and localstorage
   */
  clear(): void {
    this.clipboardSubject$.next([]);
    localStorage.removeItem('clipboard');
  }
}
