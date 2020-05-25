import {Component, HostListener, OnInit} from '@angular/core';
import {CardSimple} from '../../../dtos/cardSimple';
import {DeckService} from '../../../services/deck.service';
import {ActivatedRoute} from '@angular/router';
import {DeckSimple} from '../../../dtos/deckSimple';
import {CardService} from '../../../services/card.service';

@Component({
  selector: 'app-learn-deck',
  templateUrl: './learn-deck.component.html',
  styleUrls: ['./learn-deck.component.css']
})
export class LearnDeckComponent implements OnInit {

  deck: DeckSimple;
  card: CardSimple;
  backside: boolean;
  flipped: boolean;

  constructor(private deckService: DeckService, private cardService: CardService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.loadDeck(Number(params.get('id')));
      this.getNextCard(Number(params.get('id')), 2);
    });
  }

  @HostListener('document:keypress', ['$event'])
  handleKeyBoardEvent(event: KeyboardEvent) {
    if (event.key === ' ') {
      console.log('space');
      this.onFlip();
    } else if (this.flipped) {
      if (event.key === '1') {
        this.onNext('Again');
      }
      if (event.key === '2') {
        this.onNext('Good');
      }
      if (event.key === '3') {
        this.onNext('Easy') ;
      }
    }
  }

  loadDeck(id: number) {
    this.deckService.getDeckById(id).subscribe(deck => {
      this.deck = deck;
    },
      error => {
      console.log(error);
    });
  }

  getNextCard(deckId: number, cardId: number) {
    this.cardService.fetchCard(deckId, cardId)
      .subscribe(cardDetails => {
        this.card = new CardSimple(cardDetails.id, cardDetails.textFront, cardDetails.textBack);
      },
        error => {
        console.log(error);
      });
  }

  onFlip() {
    this.flipped = true;
    this.backside = !this.backside;
  }

  async onNext(status: string) {
    this.flipped = false;
    this.backside = false;
    console.log('Send status: ' + status);
    await this.triggerNext(status);
    this.getNextCard(this.deck.id, 2);

  }

  async triggerNext(status: string) {
    // send status
    console.log('sending status');
    }
}
