import { Component, OnInit } from '@angular/core';
import {Deck} from '../../../dtos/deck';
import {DeckService} from '../../../services/deck.service';
import {ActivatedRoute} from '@angular/router';
import {CardService} from '../../../services/card.service';
import {CardDetails} from '../../../dtos/cardDetails';

@Component({
  selector: 'app-deck-view',
  templateUrl: './deck-view.component.html',
  styleUrls: ['./deck-view.component.css']
})
export class DeckViewComponent implements OnInit {

  deck: Deck;
  cards: CardDetails[];

  constructor(private deckService: DeckService, private cardService: CardService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    const id = +this.route.snapshot.paramMap.get('id');
    this.deckService.getDeckById(id).subscribe(deck => {
      this.deck = deck;
      this.cardService.getCardsByDeckId(id).subscribe(cards => this.cards = cards);
    });
  }
}
