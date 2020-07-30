import { Component, HostListener, OnInit } from '@angular/core';
import { CardSimple } from '../../dtos/cardSimple';
import { DeckService } from '../../services/deck.service';
import { ActivatedRoute } from '@angular/router';
import { DeckSimple } from '../../dtos/deckSimple';
import { LearnService } from '../../services/learn.service';
import { LearnAttempt, AttemptStatus } from '../../dtos/learnAttempt';
import { Globals } from '../../global/globals';
import { TitleService } from 'src/app/services/title.service';
import { Pageable } from 'src/app/dtos/pageable';

@Component({
  selector: 'app-learn-deck',
  templateUrl: './learn-deck.component.html',
  styleUrls: ['./learn-deck.component.css'],
})
export class LearnDeckComponent implements OnInit {
  deck: DeckSimple;
  card: CardSimple;
  flipped: boolean;
  reverse: boolean;

  constructor(
    private deckService: DeckService,
    private learnService: LearnService,
    private route: ActivatedRoute,
    public globals: Globals,
    private titleService: TitleService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.reverse = 'reverse' in this.route.snapshot.queryParams;
      this.loadDeck(Number(params.get('id')));
      this.getNextCard(Number(params.get('id')));
    });
  }

  @HostListener('document:keydown', ['$event'])
  handleKeyBoardEvent(event: KeyboardEvent) {
    if (event.key === ' ') {
      this.onFlip();
    } else if (this.flipped) {
      const shortcuts: { [key: string]: () => void } = {
        1: () => this.onAgain(),
        2: () => this.onGood(),
        3: () => this.onEasy(),
      };
      if (event.key in shortcuts) {
        shortcuts[event.key]();
      }
    }
  }

  loadDeck(id: number) {
    this.deckService.getDeckById(id).subscribe(
      (deck) => {
        this.titleService.setTitle(`Learn ${deck.name}`, null);
        console.log(deck);
        this.deck = deck;
      },
      (error) => {
        console.log(error);
      }
    );
  }

  getNextCard(deckId: number) {
    // TODO: Fetch more cards with one query
    this.learnService
      .getNextCards(deckId, this.reverse, new Pageable(0, 1))
      .subscribe(
        (cards) => {
          console.log('next cards list: ', cards);
          if (this.reverse) {
            cards = cards.map((c) => this.reverseCard(c));
          }
          this.card = cards.length ? cards[0] : null;
          this.flipped = false;
        },
        (error) => {
          console.error(error);
        }
      );
  }

  /**
   * Exchange front and back side of a card
   *
   * @param card card where front and back will be replaced
   */
  reverseCard(card: CardSimple): CardSimple {
    return new CardSimple(
      card.id,
      card.textBack,
      card.textFront,
      card.imageBackUrl,
      card.imageFrontUrl
    );
  }

  onFlip() {
    this.flipped = true;
  }

  async onNext(status: AttemptStatus) {
    console.log('onNext: ' + status);
    this.learnService
      .sendAttemptStatus(new LearnAttempt(this.card.id, status, this.reverse))
      .subscribe(() => this.getNextCard(this.deck.id));
  }

  onAgain() {
    this.onNext(AttemptStatus.AGAIN);
  }

  onGood() {
    this.onNext(AttemptStatus.GOOD);
  }

  onEasy() {
    this.onNext(AttemptStatus.EASY);
  }
}
