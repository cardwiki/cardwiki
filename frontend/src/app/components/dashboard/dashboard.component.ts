import { Component, OnInit } from '@angular/core';
import { FavoriteService } from 'src/app/services/favorite.service';
import { DeckSimple } from 'src/app/dtos/deckSimple';
import { Pageable } from 'src/app/dtos/pageable';
import { Page } from 'src/app/dtos/page';
import { NotificationService } from 'src/app/services/notification.service';
import { TitleService } from 'src/app/services/title.service';
import {DeckProgressDetails} from '../../dtos/deckProgressDetails';
import {DeckService} from '../../services/deck.service';
import { DeckProgress } from 'src/app/dtos/deckProgress';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  favorites: Page<DeckSimple>;
  progressList: ProgressEntry[];
  progressPage: Page<DeckProgressDetails>;

  readonly favoritesPageSize = 10;
  readonly learnedPageSize = 15;

  constructor(
    private favoriteService: FavoriteService,
    private notificationService: NotificationService,
    private titleService: TitleService,
    private deckService: DeckService
  ) { }

  ngOnInit(): void {
    this.titleService.setTitle('Dashboard', null);
    this.loadFavoritePage(1);
    this.loadLearnedPage(1);
  }

  removeFavorite(deckId: number): void {
    this.favoriteService.removeFavorite(deckId)
      .subscribe(() => {
        this.notificationService.success('Removed deck from Favorites');
        this.favorites.content = this.favorites.content.filter(f => f.id !== deckId);
      });
  }

  /**
   * @param page page number 1-indexed
   */
  loadFavoritePage(page: number): void {
    const pageable = new Pageable(page - 1, this.favoritesPageSize);
    this.favoriteService.getFavorites(pageable)
      .subscribe(favorites => this.favorites = favorites);
  }

  /**
   * @param page page number 1-indexed
   */
  loadLearnedPage(page: number): void {
    const pageable = new Pageable(page - 1, this.learnedPageSize);
    this.deckService.getLearnedDecks(pageable)
      .subscribe(decks => {
        this.progressPage = decks;
        this.progressList = decks.content.flatMap(({ deckId, deckName, normal, reverse }) => {
          return [
            normal ? {deckId, deckName, reverse: false, ...normal } : null,
            reverse ? {deckId, deckName, reverse: true, ...reverse } : null,
          ].filter(p => p !== null);
        });
      });
  }

  deleteProgress(event: Event, progress: ProgressEntry): void {
    event.stopPropagation();
    event.preventDefault();
    if (confirm(`Do you want to permanently delete your progress for deck '${progress.deckName}'${progress.reverse ? ' (reverse)' : ''}`)) {
      this.deckService.deleteProgress(progress.deckId, progress.reverse).subscribe(() => {
        this.loadLearnedPage(1);
      });
    }
  }
}

type ProgressEntry = DeckProgress & { deckId: number, deckName: string, reverse: boolean };
