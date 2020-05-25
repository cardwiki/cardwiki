import {Component, HostListener, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {Deck} from "../../dtos/deck";
import {CardSimple} from "../../dtos/cardSimple";
import {DeckService} from "../../services/deck.service";
import {CardService} from "../../services/card.service";

@Component({
  selector: 'app-deck-preview',
  templateUrl: './deck-preview.component.html',
  styleUrls: ['./deck-preview.component.css']
})

export class DeckPreviewComponent implements OnInit {

  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    if (event.key === 'ArrowRight') this.nextCard();
    else if (event.key === 'ArrowLeft') this.previousCard();
  }

  deck: Deck;
  cards: CardSimple[];
  currentcard: CardSimple;
  flipped: boolean = false;

  constructor(private deckService: DeckService, private cardService: CardService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.loadDeck(Number(params.get('id')));
    });
  }

  loadDeck(id: number) {
    this.deckService.getDeckById(id).subscribe(deck => {
      this.deck = deck;
      this.cardService.getCardsByDeckId(id).subscribe(cards => {
        console.log("Loaded cards: " + cards[0].textFront);
        this.cards = cards;
        this.currentcard = cards[0];
      });
    });
  }

  flip(to: boolean): void {
    //this.flipped = !this.flipped; //doesn't work??
    this.flipped = to;
  }

  nextCard(): void {
    this.flipped = false;
    const nextCardId = this.cards.indexOf(this.currentcard) + 1;
    this.currentcard = this.cards[nextCardId < this.cards.length ? nextCardId : 0];
  }

  previousCard(): void {
    this.flipped = false;
    const previousCardId = this.cards.indexOf(this.currentcard) - 1;
    this.currentcard = this.cards[previousCardId >= 0 ? previousCardId : this.cards.length-1];
  }

}
