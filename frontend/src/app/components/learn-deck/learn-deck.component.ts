import {Component, HostListener, OnInit} from '@angular/core';
import {CardSimple} from '../../dtos/cardSimple';
import {DeckService} from '../../services/deck.service';
import {ActivatedRoute} from '@angular/router';
import {DeckSimple} from '../../dtos/deckSimple';
import {LearnService} from '../../services/learn.service';
import {LearnAttempt, AttemptStatus } from '../../dtos/learnAttempt';
import {Globals} from '../../global/globals';
import { TitleService } from 'src/app/services/title.service';

@Component({
  selector: 'app-learn-deck',
  templateUrl: './learn-deck.component.html',
  styleUrls: ['./learn-deck.component.css']
})
export class LearnDeckComponent implements OnInit {

  deck: DeckSimple;
  card: CardSimple;
  flipped: boolean;

  constructor(private deckService: DeckService, private learnService: LearnService, private route: ActivatedRoute, public globals: Globals,
              private titleService: TitleService) { }

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
        this.onAgain();
      }
      if (event.key === '2') {
        this.onGood();
      }
      if (event.key === '3') {
        this.onEasy();
      }
    }
  }

  loadDeck(id: number) {
    this.deckService.getDeckById(id).subscribe(deck => {
      this.titleService.setTitle(`Learn ${deck.name}`, null);
      console.log(deck);
      this.deck = deck;
    },
      error => {
      console.log(error);
    });
  }

  getNextCard(deckId: number) {
   this.learnService.getNextCards(deckId)
      .subscribe(cards => {
        console.log('next cards list: ', cards);
        this.card = cards.length ? cards[0] : null
      },
        error => {
        console.log(error);
      });
  }

  onFlip() {
    this.flipped = true;
  }

  async onNext(status: AttemptStatus) {
    this.flipped = false;
    console.log('Attempting to submit status: ' + status);
    await this.triggerNext(status);
    this.getNextCard(this.deck.id);
  }

  onAgain() {
    this.onNext(AttemptStatus.AGAIN)
  }

  onGood() {
    this.onNext(AttemptStatus.GOOD)
  }

  onEasy() {
    this.onNext(AttemptStatus.EASY)
  }

  async triggerNext(status: AttemptStatus) {
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
