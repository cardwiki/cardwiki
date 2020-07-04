import { Component, OnInit } from '@angular/core';
import { FavoriteService } from 'src/app/services/favorite.service';
import { DeckSimple } from 'src/app/dtos/deckSimple';
import { Pageable } from 'src/app/dtos/pageable';
import { Page } from 'src/app/dtos/page';
import { NotificationService } from 'src/app/services/notification.service';
import { TitleService } from 'src/app/services/title.service';
import {DeckProgressDetails} from '../../dtos/deckProgressDetails';
import {DeckService} from '../../services/deck.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  favorites: Page<DeckSimple>;
  learned: Page<DeckProgressDetails>;

  readonly favoritesPageSize = 10;
  readonly learnedPageSize = 10;

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

  removeFavorite(deckId: number) {
    this.favoriteService.removeFavorite(deckId)
      .subscribe(() => {
        this.notificationService.success('Removed deck from Favorites');
        this.favorites.content = this.favorites.content.filter(f => f.id !== deckId);
      });
  }

  /**
   * @param page page number 1-indexed
   */
  loadFavoritePage(page: number) {
    const pageable = new Pageable(page - 1, this.favoritesPageSize);
    this.favoriteService.getFavorites(pageable)
      .subscribe(favorites => this.favorites = favorites);
  }

  /**
   * @param page page number 1-indexed
   */
  loadLearnedPage(page: number) {
    const pageable = new Pageable(page - 1, this.learnedPageSize);
    this.deckService.getLearnedDecks(pageable)
      .subscribe(decks => this.learned = decks);
  }

  deleteProgress(event: any, progress: DeckProgressDetails): void {
    event.stopPropagation();
    event.preventDefault();
    if (confirm(`Do you want to permanently delete your progress for deck '${progress.deckName}'`)) {
      this.deckService.deleteProgress(progress.deckId).subscribe(_ => {
        this.learned.content = this.learned.content.filter(p => p !== progress);
      });
    }
  }
}
