import {Component, Injector, OnInit} from '@angular/core';
import {DeckDetails} from '../../../dtos/deckDetails';
import {DeckService} from '../../../services/deck.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CardService} from '../../../services/card.service';
import {CardSimple} from '../../../dtos/cardSimple';
import {NotificationService} from 'src/app/services/notification.service';
import {DeckForkModalComponent} from '../deck-fork-modal/deck-fork-modal.component';
import {Observable} from 'rxjs';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from '../../../services/auth.service';
import {Globals} from '../../../global/globals';

@Component({
  selector: 'app-deck-view',
  templateUrl: './deck-view.component.html',
  styleUrls: ['./deck-view.component.css']
})
export class DeckViewComponent implements OnInit {

  deck: DeckDetails;
  cards: CardSimple[];

  constructor(private deckService: DeckService, private cardService: CardService, private route: ActivatedRoute,
              private router: Router, private modalService: NgbModal, public authService: AuthService,
              private notificationService: NotificationService, public globals: Globals) { }

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
    if (confirm('Are you sure you want to delete this card?')) {
      this.cardService.removeCardFromDeck(this.deck.id, card.id).subscribe(() => {
        this.cards = this.cards.filter(c => c !== card)
        this.notificationService.success('Deleted Card')
      });
    }
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
