import {Component, Injector, OnInit} from '@angular/core';
import {DeckDetails} from '../../../dtos/deckDetails';
import {DeckService} from '../../../services/deck.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CardService} from '../../../services/card.service';
import {CardSimple} from '../../../dtos/cardSimple';
import {NotificationService} from 'src/app/services/notification.service';
import {DeckForkModalComponent} from '../deck-fork-modal/deck-fork-modal.component';
import {Observable, Subject, BehaviorSubject} from 'rxjs';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from '../../../services/auth.service';
import {FavoriteService} from 'src/app/services/favorite.service';
import { CardRemoveModalComponent } from '../card-remove-modal/card-remove-modal.component';
import { Pageable } from 'src/app/dtos/pageable';
import { Page } from 'src/app/dtos/page';

@Component({
  selector: 'app-deck-view',
  templateUrl: './deck-view.component.html',
  styleUrls: ['./deck-view.component.css']
})
export class DeckViewComponent implements OnInit {

  readonly limit = 50;

  deck: DeckDetails;
  page: Page<CardSimple>;
  cards: CardSimple[];
  isFavorite$: Subject<boolean>;
  loading: boolean;

  constructor(private deckService: DeckService, private cardService: CardService, private route: ActivatedRoute, private favoriteService: FavoriteService,
              private router: Router, private modalService: NgbModal, public authService: AuthService, private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.deck = this.page = null
    this.cards = []
    this.route.paramMap.subscribe(params => {
      this.loadDeck(Number(params.get('id')));
    });
    this.isFavorite$ = new Subject()
  }

  loadDeck(id: number) {
    this.deckService.getDeckById(id).subscribe(deck => {
      this.deck = deck;
      this.cards = [];
      this.loadMoreCards();
    });
    if (this.authService.isLoggedIn())
      this.favoriteService.hasFavorite(id).subscribe(isFavorite => this.isFavorite$.next(isFavorite))
  }

  loadMoreCards() {
    const nextPageNumber = this.page ? this.page.pageable.pageNumber + 1 : 0
    this.loading = true
    this.cardService.getCardsByDeckId(this.deck.id, new Pageable(nextPageNumber, this.limit))
      .subscribe(page => {
        this.page = page;
        this.cards.push(...page.content)
      }).add(() => this.loading = false);
  }

  openCardRemoveModal(card: CardSimple) {
    const modalRef = this.modalService.open(CardRemoveModalComponent);
    modalRef.componentInstance.card = card;

    modalRef.result.then(
      (res: Observable<void>) => res.subscribe(
        () => {
          this.notificationService.success('Deleted Card')
          this.cards = this.cards.filter(c => c !== card)
        }   
      )
    ).catch(err => console.error('Did not remove card', err));
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

  saveToFavorites() {
    this.favoriteService.addFavorite(this.deck.id)
      .subscribe(() => this.isFavorite$.next(true))
  }

  removeFromFavorites() {
    this.favoriteService.removeFavorite(this.deck.id)
      .subscribe(() => this.isFavorite$.next(false))
  }
}
