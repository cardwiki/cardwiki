import { Component, OnInit } from '@angular/core';
import { FavoriteService } from 'src/app/services/favorite.service';
import { DeckSimple } from 'src/app/dtos/deckSimple';
import { Pageable } from 'src/app/dtos/pageable';
import { Page } from 'src/app/dtos/page';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  favorites: Page<DeckSimple>

  readonly favoritesPageSize = 10

  constructor(private favoriteService: FavoriteService, private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.loadFavoritePage(1)
  }

  removeFavorite(deckId: number) {
    this.favoriteService.removeFavorite(deckId)
      .subscribe(() => {
        this.notificationService.success('Removed deck from Favorites')
        this.favorites.content = this.favorites.content.filter(f => f.id !== deckId)
      })
  }

  /** 
   * @param page page number 1-indexed
   */
  loadFavoritePage(page: number) {
    const pageable = new Pageable(page - 1, this.favoritesPageSize)
    this.favoriteService.getFavorites(pageable)
      .subscribe(favorites => this.favorites = favorites)
  }
}
