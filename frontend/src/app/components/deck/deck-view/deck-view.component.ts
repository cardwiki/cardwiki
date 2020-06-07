import {Component, Injector, OnInit} from '@angular/core';
import {DeckDetails} from '../../../dtos/deckDetails';
import {DeckService} from '../../../services/deck.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CardService} from '../../../services/card.service';
import {CardSimple} from '../../../dtos/cardSimple';
import {DeckForkModalComponent} from '../deck-fork-modal/deck-fork-modal.component';
import {Observable} from 'rxjs';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-deck-view',
  templateUrl: './deck-view.component.html',
  styleUrls: ['./deck-view.component.css']
})
export class DeckViewComponent implements OnInit {

  deck: DeckDetails;
  cards: CardSimple[];

  constructor(private deckService: DeckService, private cardService: CardService, private route: ActivatedRoute,
              private router: Router, private modalService: NgbModal) { }

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
      const index: number = this.cards.indexOf(card);
      if (index !== -1) {
        this.cards.splice(index, 1);
      }
    });
  }

  openForkModal() {
    const modalRef = this.modalService.open(DeckForkModalComponent);
    modalRef.componentInstance.deck = this.deck;

    modalRef.result.then(
      (res: Observable<DeckDetails>) => res.subscribe(
        (deck: DeckDetails) => this.router.navigate(['decks', deck.id])
      )
    ).catch(() => {});
  }
}
