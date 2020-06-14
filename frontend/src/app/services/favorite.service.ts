import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { ErrorHandlerService } from './error-handler.service';
import { DeckSimple } from '../dtos/deckSimple';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { Page } from '../dtos/page';
import { Pageable } from '../dtos/pageable';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class FavoriteService {

  constructor(private httpClient: HttpClient, private globals: Globals, private errorHandler: ErrorHandlerService, private authService: AuthService) {
  }

  /**
   * Add a deck to the favorites of the logged in user
   * 
   * @param deckId deck which should be added as favorite
   */
  addFavorite(deckId: number): Observable<DeckSimple> {
    return this.httpClient.post<DeckSimple>(this.getFavoriteUri(), { deckId })
      .pipe(tap(null, this.errorHandler.handleError('Could not add favorite')))
  }

  /**
   * Get page of favorites of the logged in user
   * 
   * @param pageable 
   */
  getFavorites(pageable: Pageable): Observable<Page<DeckSimple>> {
    const params = pageable.toHttpParams()

    return this.httpClient.get<Page<DeckSimple>>(this.getFavoriteUri(), { params })
      .pipe(tap(null, this.errorHandler.handleError('Could not get favorites')))
  }

  /**
   * Remove deck from the favorites of the logged in user
   * 
   * @param deckId 
   */
  removeFavorite(deckId: number): Observable<void> {
    return this.httpClient.delete<void>(this.getFavoriteUri(deckId))
      .pipe(tap(null, this.errorHandler.handleError('Could not add favorite')))
  }

  private getFavoriteUri(deckId?: number): string {
    const userId = this.authService.getUserId()
    if (typeof userId !== 'number') {
      console.error('Invalid userId', userId)
      throw new Error('Invalid userId for getFavoriteUri')
    }

    return `${this.globals.backendUri}/users/${userId}/favorites` + (deckId ? `/${deckId}` : '')
  }
}
