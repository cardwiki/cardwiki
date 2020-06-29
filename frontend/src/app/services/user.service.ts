import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {DeckSimple} from "../dtos/deckSimple";
import {RevisionDetailed} from "../dtos/revisionDetailed";
import {UserProfile} from "../dtos/userProfile";
import {tap} from "rxjs/operators";
import {ErrorHandlerService} from "./error-handler.service";
import { Pageable } from '../dtos/pageable';
import { Page } from '../dtos/page';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userBaseUri: string = this.globals.backendUri + '/users';

  constructor(private httpClient: HttpClient, private globals: Globals, private errorHandler: ErrorHandlerService) { }

  /**
   * Fetch users containing {@code username} in their username
   *
   * @param username to search for
   * @param pageable config for pagination
   */
  searchUsers(username: string, pageable: Pageable): Observable<Page<UserProfile>> {
    console.log('search for users with username: ' + username);
    const params = {
      username,
      ...pageable.toObject(),
    }
    return this.httpClient.get<Page<UserProfile>>(this.userBaseUri, {params})
      .pipe(tap(null, this.errorHandler.handleError('Could not find Users')));
  }

  /**
   * Load profile of user with username {@code username} from
   * the backend.
   *
   * @param username of user to load profile for
   */
  getProfile(username: string): Observable<UserProfile> {
    console.log('load profile for user: ' + username);
    return this.httpClient.get<UserProfile>(`${this.userBaseUri}/byname/${username}`)
      .pipe(tap(null, this.errorHandler.handleError('Could not load User Profile')));
  }

  /**
   * Fetch page of decks created by user with {@code userId}
   *
   * @param userId to query decks for
   * @param pageable config for pagination
   */
  getDecks(userId: number, pageable: Pageable): Observable<Page<DeckSimple>> {
    console.log('load card decks for user: ' + userId);
    return this.httpClient.get<Page<DeckSimple>>(`${this.userBaseUri}/${userId}/decks`, { params: pageable.toHttpParams() })
      .pipe(tap(null, this.errorHandler.handleError('Could not load User Decks')));
  }

  /**
   * Fetch page of revisions created by user with {@code userId}
   *
   * @param userId to query revisions for
   * @param pageable config for pagination
   */
  getRevisions(userId: number, pageable: Pageable): Observable<Page<RevisionDetailed>> {
    console.log('load card revisions for user: ' + userId);
    return this.httpClient.get<Page<RevisionDetailed>>(`${this.userBaseUri}/${userId}/revisions`, { params: pageable.toHttpParams() })
      .pipe(tap(null, this.errorHandler.handleError('Could not load User Revisions')));
  }

  /**
   * Edit description for user with userid {@code userid}
   *
   * @param userid to query revisions for
   * @param description to save
   */
  editDescription(userid: number, description: string): Observable<UserProfile> {
    console.log('Save description: ' + description);
    return this.httpClient.patch<UserProfile>(`${this.userBaseUri}/${userid}`, {description: description})
      .pipe(tap(null, this.errorHandler.handleError('Could not edit User description')));
  }

  /**
   * Enable or disable the account with userid {@code userid}.
   *
   * @param userid of the user to update.
   * @param enabled the 'enabled' status to set.
   * @param reason why this user is disabled
   */
  editEnabledStatus(userid: number, enabled: boolean, reason?: string): Observable<UserProfile> {
    const msg = enabled ? 'enable user' : 'disable user';
    console.log(`${msg} ${userid}`);
    return this.httpClient.patch<UserProfile>(`${this.userBaseUri}/${userid}`, {enabled: enabled, reason: reason})
      .pipe(tap(null, this.errorHandler.handleError('Could not ' + msg)));
  }

  /**
   * Makes a user an admin or removes your own admin rights.
   *
   * @param userid of the user to update.
   * @param admin the 'admin' status to set.
   */
  editAdminStatus(userid: number, admin: boolean): Observable<UserProfile> {
    const msg = admin ? 'promote user' : 'demote user';
    console.log(`${msg} ${userid}`);
    return this.httpClient.patch<UserProfile>(`${this.userBaseUri}/${userid}`, {admin: admin})
      .pipe(tap(null, this.errorHandler.handleError('Could not ' + msg)));
  }

  /**
   * Deletes a user.
   *
   * @param userid of the user to delete.
   * @param reason why the user is deleted.
   */
  delete(userid: number, reason: string): Observable<void> {
    console.log('delete user ' + userid);
    const params = new HttpParams({
      fromObject: {
        reason: reason
      }
    });
    return this.httpClient.delete<void>(`${this.userBaseUri}/${userid}`, {params: params})
      .pipe(tap(null, this.errorHandler.handleError('Could not delete user ' + userid)));
  }

  setTheme(userid: number, theme: string): Observable<UserProfile> {
    console.log('set theme to', theme, 'for user', userid);
    return this.httpClient.patch<UserProfile>(`${this.userBaseUri}/${userid}`, {theme: theme})
      .pipe(tap(null, this.errorHandler.handleError('Could not set User theme')));
  }

  export(userId: number): Observable<Blob> {
    console.log('export user data', userId);
    return this.httpClient.get(`${this.userBaseUri}/${userId}/export`, { responseType: 'blob' })
      .pipe(tap(null, this.errorHandler.handleError('Could not fetch user data')));
  }
}
