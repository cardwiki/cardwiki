import { Component, OnInit } from '@angular/core';
import {DeckDetails} from '../../../dtos/deckDetails';
import {DeckService} from '../../../services/deck.service';
import {ActivatedRoute} from '@angular/router';
import {CardService} from '../../../services/card.service';
import {CardSimple} from '../../../dtos/cardSimple';

@Component({
  selector: 'app-deck-view',
  templateUrl: './deck-view.component.html',
  styleUrls: ['./deck-view.component.css']
})
export class DeckViewComponent implements OnInit {

  deck: DeckDetails;
  cards: CardSimple[];

  constructor(private deckService: DeckService, private cardService: CardService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.loadDeck(Number(params.get('id')));
    });
  }

  loadDeck(id: number) {
    this.deckService.getDeckById(id).subscribe(deck => {
      this.deck = deck;
      this.cardService.getCardsByDeckId(id).subscribe(cards => this.cards = cards);
    });
  }

  removeCard(card: CardSimple) {
    if (confirm('Are you sure you want to delete this card?'))
      this.cardService.removeCardFromDeck(this.deck.id, card.id).subscribe(() => {
        const index: number = this.cards.indexOf(card);
        if (index !== -1) {
          this.cards.splice(index, 1);
        }
      });
  }
}
