import {Component, OnInit, ViewChild} from '@angular/core';
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
import { Pageable } from 'src/app/dtos/pageable';
import { Page } from 'src/app/dtos/page';
import { CommentService } from 'src/app/services/comment.service';
import { CommentSimple } from 'src/app/dtos/commentSimple';
import { CommentFormComponent } from '../../comment/comment-form/comment-form.component';
import { CardsImportModalComponent} from '../cards-import-modal/cards-import-modal.component';
import {ClipboardService} from '../../../services/clipboard.service';
import {ClipboardPasteModalComponent} from '../../clipboard/clipboard-paste-modal/clipboard-paste-modal.component';
import {CardUpdate} from '../../../dtos/cardUpdate';
import { TitleService } from 'src/app/services/title.service';
import { DeckProgress } from 'src/app/dtos/deckProgress';

@Component({
  selector: 'app-deck-view',
  templateUrl: './deck-view.component.html',
  styleUrls: ['./deck-view.component.css']
})
export class DeckViewComponent implements OnInit {

  readonly limit = 50;

  deck: DeckDetails;
  isFavorite$: Subject<boolean>;
  clipboardSize: number;
  loadingError: string;
  progress: DeckProgress;


  page: Page<CardSimple>;
  cards: CardSimple[];
  loadingCards: boolean;

  displayComments = false;
  comments: CommentSimple[];
  commentsPage: Page<CommentSimple>;
  readonly commentsPageSize = 10;

  @ViewChild('commentForm') private commentForm: CommentFormComponent;

  constructor(private deckService: DeckService, private cardService: CardService, public globals: Globals,
              private favoriteService: FavoriteService, private commentService: CommentService,
              private route: ActivatedRoute, private router: Router, private modalService: NgbModal,
              private authService: AuthService, private notificationService: NotificationService,
              private clipboardService: ClipboardService, private titleService: TitleService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.deck = this.page = null;
      this.loadingError = null;
      this.cards = [];
      this.isFavorite$ = new Subject();
      this.displayComments = false;
      this.commentsPage = null;
      this.comments = [];
      this.loadDeck(Number(params.get('id')));
    });
    this.clipboardService.clipboard$.subscribe(clipboard => this.clipboardSize = clipboard.length);
  }

  loadDeck(id: number) {
    // TODO: Use forkJoin to only update page when everything loaded (to prevent flickering)
    this.deckService.getDeckById(id).subscribe(deck => {
      this.titleService.setTitle(deck.name, null);
      this.deck = deck;
      this.cards = [];
      this.loadMoreCards();
      this.loadMoreComments();
      if (this.authService.isLoggedIn()) {
        this.favoriteService.hasFavorite(id).subscribe(isFavorite => this.isFavorite$.next(isFavorite));
        this.deckService.fetchProgress(id).subscribe(progress => this.progress = progress);
      }
    }, err => {
      this.loadingError = err && err.status === 404 ?
        'This deck does not exist. Maybe it has been deleted or the link is broken'
        : 'Could not load this deck.';
    });
  }

  loadMoreCards() {
    const nextPageNumber = this.page ? this.page.pageable.pageNumber + 1 : 0;
    this.loadingCards = true;
    this.cardService.getCardsByDeckId(this.deck.id, new Pageable(nextPageNumber, this.limit))
      .subscribe(page => {
        this.page = page;
        this.cards.push(...page.content);
      }).add(() => this.loadingCards = false);
  }

  loadMoreComments() {
    const nextPageNumber = this.commentsPage ? this.commentsPage.pageable.pageNumber + 1 : 0;
    this.commentService.findByDeckId(this.deck.id, new Pageable(nextPageNumber, this.commentsPageSize))
      .subscribe(page => {
        this.commentsPage = page;
        this.comments.push(...page.content);
      });
  }

  toggleComments() {
    this.displayComments = !this.displayComments;
  }

  addComment(message: string) {
    console.log('addComment', message);
    this.commentService.addCommentToDeck(this.deck.id, message)
      .subscribe(comment => {
        this.notificationService.success('Comment saved');
        this.comments.unshift(comment);
        this.commentsPage.totalElements += 1;
        this.commentForm.reset();
      });
  }

  openCardRemoveModal(card: CardSimple) {
    const modalRef = this.modalService.open(CardRemoveModalComponent);
    modalRef.componentInstance.card = card;

    modalRef.result.then(
      (res: Observable<void>) => res.subscribe(
        () => {
          this.notificationService.success('Deleted Card');
          this.cards = this.cards.filter(c => c !== card);
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

  deleteCard(card: CardSimple) {
    if (confirm('Are you sure you want to permanently delete this card?')) {
      this.cardService.deleteCard(card.id).subscribe(_ => {
        this.cards = this.cards.filter(c => c !== card);
      });
    }
  }

  saveToFavorites() {
    this.favoriteService.addFavorite(this.deck.id)
      .subscribe(() => this.isFavorite$.next(true));
  }

  removeFromFavorites() {
    this.favoriteService.removeFavorite(this.deck.id)
      .subscribe(() => this.isFavorite$.next(false));
  }

  exportCsv() {
    this.deckService.exportDeckAsCsv(this.deck.id).subscribe((answer: any) => {
      const file = document.createElement('a');
      const blob = new Blob([answer], {type: 'text/csv'});
      const objectUrl = URL.createObjectURL(blob);
      file.href = objectUrl;
      file.download = this.deck.name + '.csv';
      file.click();
      URL.revokeObjectURL(objectUrl);
    });
  }

  openImportModal(deck: DeckDetails) {
    const modalRef = this.modalService.open(CardsImportModalComponent, { size: 'lg' });
    modalRef.componentInstance.deck = this.deck;

    modalRef.result.then(
      (res: Observable<DeckDetails>) => res.subscribe(
        (deckResult: DeckDetails) => this.router.navigate(['decks', deckResult])
      )
    ).catch(() => {});
  }

  copyToClipboard(card: CardSimple) {
    this.clipboardService.copy(card, this.deck.name);
    this.notificationService.success('Copied to Clipboard');
  }

  pasteFromClipboard(cards: CardUpdate[]) {
    this.clipboardService.paste(this.deck.id, cards).subscribe(pastedCards => {
      this.cards.push(...pastedCards);
      this.notificationService.success('Cards pasted from Clipboard');
    });
  }

  openClipboardPasteModal() {
    const modalRef = this.modalService.open(ClipboardPasteModalComponent, { size: 'lg' });
    modalRef.result.then(
      (res: CardUpdate[]) => this.pasteFromClipboard(res)
    ).catch(() => {});
  }

}
