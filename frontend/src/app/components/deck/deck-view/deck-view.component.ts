import {Component, OnInit} from '@angular/core';
import {DeckDetails} from '../../../dtos/deckDetails';
import {DeckService} from '../../../services/deck.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CardService} from '../../../services/card.service';
import {CardSimple} from '../../../dtos/cardSimple';
import {NotificationService} from 'src/app/services/notification.service';
import {DeckForkModalComponent} from '../deck-fork-modal/deck-fork-modal.component';
import {Observable, Subject} from 'rxjs';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from '../../../services/auth.service';
import {Globals} from '../../../global/globals';
import {FavoriteService} from 'src/app/services/favorite.service';
import { CardRemoveModalComponent } from '../card-remove-modal/card-remove-modal.component';
import { CommentService } from 'src/app/services/comment.service';
import { Pageable } from 'src/app/dtos/pageable';
import { Page } from 'src/app/dtos/page';
import { CommentSimple } from 'src/app/dtos/commentSimple';

@Component({
  selector: 'app-deck-view',
  templateUrl: './deck-view.component.html',
  styleUrls: ['./deck-view.component.css']
})
export class DeckViewComponent implements OnInit {

  deck: DeckDetails;
  cards: CardSimple[];
  isFavorite$: Subject<boolean>

  displayComments = false
  comments: CommentSimple[]
  commentsPage: Page<CommentSimple>
  readonly commentsPageSize = 10

  constructor(private deckService: DeckService, private cardService: CardService, public globals: Globals,
              private favoriteService: FavoriteService, private commentService: CommentService,
              private route: ActivatedRoute, private router: Router, private modalService: NgbModal,
              private authService: AuthService, private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.loadDeck(Number(params.get('id')));
    });
    this.isFavorite$ = new Subject()
    this.displayComments = false
    this.commentsPage = null
    this.comments = []
  }

  loadDeck(id: number) {
    // TODO: Use forkJoin to only update page when everything loaded (to prevent flickering)
    this.deckService.getDeckById(id).subscribe(deck => {
      this.deck = deck;
      this.cardService.getCardsByDeckId(id).subscribe(cards => this.cards = cards);
      this.loadMoreComments()
    });
    if (this.authService.isLoggedIn())
      this.favoriteService.hasFavorite(id).subscribe(isFavorite => this.isFavorite$.next(isFavorite))
  }

  loadMoreComments() {
    const nextPageNumber = this.commentsPage ? this.commentsPage.pageable.pageNumber + 1 : 0
    this.commentService.findByDeckId(this.deck.id, new Pageable(nextPageNumber, this.commentsPageSize))
      .subscribe(page => {
        this.commentsPage = page
        this.comments.push(...page.content)
      })
  }

  toggleComments() {
    this.displayComments = !this.displayComments
  }

  addComment(message: string) {
    console.log('addComment', message)
    this.commentService.addCommentToDeck(this.deck.id, message)
      .subscribe(comment => {
        this.notificationService.success('Comment saved')
        this.comments.unshift(comment)
        this.commentsPage.totalElements += 1
      })
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
