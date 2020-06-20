import { Injectable } from '@angular/core';
import { CommentSimple } from '../dtos/commentSimple';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { ErrorHandlerService } from './error-handler.service';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Page } from '../dtos/page';
import { Pageable } from '../dtos/pageable';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  private deckBaseUri = this.globals.backendUri + '/decks'
  private commentBaseUri = this.globals.backendUri + '/comments'

  constructor(private httpClient: HttpClient, private globals: Globals, private errorHandler: ErrorHandlerService, private authService: AuthService) {
  }

  /**
   * Add a comment to a deck
   * 
   * @param deckId deck where the comment should be added
   * @param message comment message to be added
   * @return created comment
   */
  addCommentToDeck(deckId: number, message: string): Observable<CommentSimple> {
    const dto = { message }
    return this.httpClient.post<CommentSimple>(`${this.deckBaseUri}/${deckId}/comments`, dto)
      .pipe(tap(null, this.errorHandler.handleError('Could not create comment')))
  }

  /**
   * Edit an existing comment
   * 
   * @param commentId id of the comment to edit
   * @param message new comment message
   * @return updated comment
   */
  editComment(commentId: number, message: string): Observable<CommentSimple> {
    const dto = { message }
    return this.httpClient.put<CommentSimple>(`${this.commentBaseUri}/${commentId}`, dto)
      .pipe(tap(null, this.errorHandler.handleError('Could not edit comment')))
  }

  /**
   * Find page of comments of a deck
   * 
   * @param deckId deck which should be searched for comments
   * @param pageable pagination config
   * @return page of comments sorted by newest first
   */
  findByDeckId(deckId: number, pageable: Pageable): Observable<Page<CommentSimple>> {
    return this.httpClient.get<Page<CommentSimple>>(`${this.deckBaseUri}/${deckId}/comments`, { params: pageable.toHttpParams() })
      .pipe(tap(null, this.errorHandler.handleError('Could not fetch comments')))
  }

  /**
   * Find a single comment
   * 
   * @param commentId id of the comment
   * @return comment
   */
  findOne(commentId: number): Observable<CommentSimple> {
    return this.httpClient.get<CommentSimple>(`${this.commentBaseUri}/${commentId}`)
      .pipe(tap(null, this.errorHandler.handleError('Could not fetch comment')))
  }

  /**
   * Permanently delete a comment
   * 
   * @param commentId id of the comment
   */
  deleteComment(commentId: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.commentBaseUri}/${commentId}`)
      .pipe(tap(null, this.errorHandler.handleError('Could not delete comment')))
  }
}
