import { Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DeckDetails } from '../../../dtos/deckDetails';
import { CardSimple } from '../../../dtos/cardSimple';
import { DeckService } from '../../../services/deck.service';
import { CardService } from '../../../services/card.service';
import { Pageable } from 'src/app/dtos/pageable';
import { Page } from 'src/app/dtos/page';
import { Globals } from '../../../global/globals';
import { TitleService } from 'src/app/services/title.service';

@Component({
  selector: 'app-deck-preview',
  templateUrl: './deck-preview.component.html',
  styleUrls: ['./deck-preview.component.css'],
})
export class DeckPreviewComponent implements OnInit {
  constructor(
    private deckService: DeckService,
    private cardService: CardService,
    private route: ActivatedRoute,
    public globals: Globals,
    private titleService: TitleService
  ) {}

  readonly fetchSize = 25;
  readonly minPrefetched = 10; // fetch next cards when (remaining cards <= minPrefetched)

  deck: DeckDetails;
  page: Page<CardSimple>;
  cards: CardSimple[];
  currentCardIndex = 0;
  currentcard: CardSimple;
  flipped = false;

  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent): void {
    const actions: { [key: string]: () => void } = {
      ArrowRight: () => this.nextCard(),
      ArrowLeft: () => this.previousCard(),
      ArrowUp: () => this.flipTo('front'),
      ArrowDown: () => this.flipTo('back'),
      ' ': () => this.flip(),
    };
    if (event.key in actions) {
      actions[event.key]();
    }
  }

  ngOnInit(): void {
    this.deck = this.page = this.currentcard = null;
    this.cards = [];
    this.route.paramMap.subscribe((params) => {
      this.loadDeck(Number(params.get('id')));
    });
  }

  loadDeck(id: number): void {
    this.deckService.getDeckById(id).subscribe((deck) => {
      this.titleService.setTitle(`View ${deck.name}`, null);
      this.deck = deck;
      this.loadNextCards();
    });
  }

  loadNextCards(): void {
    const nextPageNumber = this.page ? this.page.pageable.pageNumber + 1 : 0;
    this.cardService
      .getCardsByDeckId(
        this.deck.id,
        new Pageable(nextPageNumber, this.fetchSize)
      )
      .subscribe((page) => {
        console.log('Fetched next cards', page);
        this.page = page;
        this.cards.push(...page.content);
        if (this.currentcard === null) {
          this.gotoCard(0);
        }
      });
  }

  flipTo(to: 'front' | 'back'): void {
    this.flipped = to === 'back';
  }

  flip(): void {
    this.flipped = !this.flipped;
  }

  nextCard(): void {
    this.gotoCard(this.currentCardIndex + 1);
  }

  previousCard(): void {
    this.gotoCard(this.currentCardIndex - 1);
  }

  gotoCard(index: number): void {
    this.flipped = false;
    this.currentcard = this.cards[index % this.cards.length];
    this.currentCardIndex = index;

    if (this.shouldPreLoadNextCards()) {
      this.loadNextCards();
    }
  }

  shouldPreLoadNextCards(): boolean {
    return (
      !this.page.last &&
      this.cards.length - this.currentCardIndex <= this.minPrefetched
    );
  }
}
