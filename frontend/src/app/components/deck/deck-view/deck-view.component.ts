import { Component, OnInit } from '@angular/core';
import {DeckDetails} from '../../../dtos/deckDetails';
import {DeckService} from '../../../services/deck.service';
import {ActivatedRoute} from '@angular/router';
import {CardService} from '../../../services/card.service';
import {CardSimple} from '../../../dtos/cardSimple';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-deck-view',
  templateUrl: './deck-view.component.html',
  styleUrls: ['./deck-view.component.css']
})
export class DeckViewComponent implements OnInit {

  deck: DeckDetails;
  cards: CardSimple[];

  constructor(private deckService: DeckService, private cardService: CardService, private route: ActivatedRoute, private notificationService: NotificationService) { }

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
    this.cardService.removeCardFromDeck(this.deck.id, card.id).subscribe(() => {
      this.cards = this.cards.filter(c => c !== card)
      this.notificationService.success('Deleted Card')
    });
  }
}
