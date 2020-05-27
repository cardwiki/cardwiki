import {Component, HostListener, OnInit} from '@angular/core';
import {CardSimple} from '../../../dtos/cardSimple';
import {DeckService} from '../../../services/deck.service';
import {ActivatedRoute} from '@angular/router';
import {DeckSimple} from '../../../dtos/deckSimple';
import {LearnService} from '../../../services/learn.service';
import {LearnAttempt} from '../../../dtos/learnAttempt';

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

  constructor(private deckService: DeckService, private learnService: LearnService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.loadDeck(Number(params.get('id')));
      this.getNextCard(Number(params.get('id')));
    });
  }

  @HostListener('document:keypress', ['$event'])
  handleKeyBoardEvent(event: KeyboardEvent) {
    if (event.key === ' ') {
      this.onFlip();
    } else if (this.flipped) {
      if (event.key === '1') {
        this.onNext('AGAIN');
      }
      if (event.key === '2') {
        this.onNext('GOOD');
      }
      if (event.key === '3') {
        this.onNext('EASY') ;
      }
    }
  }

  loadDeck(id: number) {
    this.deckService.getDeckById(id).subscribe(deck => {
      console.log(deck);
      this.deck = deck;
    },
      error => {
      console.log(error);
    });
  }

  getNextCard(deckId: number) {
   this.learnService.getNextCard(deckId)
      .subscribe(cardDetails => {
        console.log('list: ', cardDetails);
        if (!(cardDetails instanceof Array) || cardDetails.length < 1) {
          this.card = new CardSimple(
            0,
            'There are no more cards to learn at the moment.',
            'There are no more cards to learn at the moment.'
          );
        } else {
          this.card = new CardSimple(cardDetails[0].id, cardDetails[0].textFront, cardDetails[0].textBack);
        }
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
    console.log('Attempting to submit status: ' + status);
    await this.triggerNext(status);
    this.getNextCard(this.deck.id);
  }

  async triggerNext(status: string) {
    console.log('Submitted status: ' + status);
    if (this.card.id > 0) {
      await this.learnService.sendAttemptStatus(new LearnAttempt(this.card.id, status))
        .subscribe(() => {
            console.log('Status ' + status + ' successfully submitted.');
          },
          error => {
            console.log(error);
          });
    }
  }
}
