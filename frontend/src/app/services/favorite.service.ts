import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { ErrorHandlerService } from './error-handler.service';
import { DeckSimple } from '../dtos/deckSimple';
import { map, catchError } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { Page } from '../dtos/page';
import { Pageable } from '../dtos/pageable';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class FavoriteService {
  constructor(
    private httpClient: HttpClient,
    private globals: Globals,
    private errorHandler: ErrorHandlerService,
    private authService: AuthService
  ) {}

  /**
   * Add a deck to the favorites of the logged in user
   *
   * @param deckId deck which should be added as favorite
   */
  addFavorite(deckId: number): Observable<DeckSimple> {
    return this.httpClient
      .put<DeckSimple>(this.getFavoriteUri(deckId), {})
      .pipe(
        catchError(this.errorHandler.handleError('Could not add favorite'))
      );
  }

  /**
   * Get page of favorites of the logged in user
   */
  getFavorites(pageable: Pageable): Observable<Page<DeckSimple>> {
    const params = pageable.toHttpParams();

    return this.httpClient
      .get<Page<DeckSimple>>(this.getFavoriteUri(), { params })
      .pipe(
        catchError(this.errorHandler.handleError('Could not get favorites'))
      );
  }

  /**
   * Check if a deck is in the favorites of the logged in user
   *
   * @param deckId deck which should be checked
   */
  hasFavorite(deckId: number): Observable<boolean> {
    // return false if the status is 404, else true for a successful response
    return this.httpClient.get<void>(this.getFavoriteUri(deckId)).pipe(
      catchError(this.errorHandler.catchStatus(404, false)),
      catchError(
        this.errorHandler.handleError('Could not check if deck is a favorite')
      ),
      map((val) => val !== false)
    );
  }

  /**
   * Remove deck from the favorites of the logged in user
   */
  removeFavorite(deckId: number): Observable<void> {
    return this.httpClient
      .delete<void>(this.getFavoriteUri(deckId))
      .pipe(
        catchError(this.errorHandler.handleError('Could not add favorite'))
      );
  }

  private getFavoriteUri(deckId?: number): string {
    const userId = this.authService.getUserId();
    if (typeof userId !== 'number') {
      console.error('Invalid userId', userId);
      throw new Error('Invalid userId for getFavoriteUri');
    }

    return (
      `${this.globals.backendUri}/users/${userId}/favorites` +
      (deckId ? `/${deckId}` : '')
    );
  }
}
