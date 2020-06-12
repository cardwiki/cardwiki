import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {DeckSimple} from "../dtos/deckSimple";
import {RevisionDetailed} from "../dtos/revisionDetailed";
import {UserProfile} from "../dtos/userProfile";
import {tap} from "rxjs/operators";
import {ErrorHandlerService} from "./error-handler.service";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userBaseUri: string = this.globals.backendUri + '/users';

  constructor(private httpClient: HttpClient, private globals: Globals, private errorHandler: ErrorHandlerService) { }

  /**
   * Load users containing {@code userid} in their username
   *
   * @param username to search for
   * @param offset of the page.
   * @param limit of results returned.
   */
  searchUsers(username: string, limit: number = 10, offset: number = 0): Observable<UserProfile[]> {
    console.log('search for users with username: ' + username);
    const params = new HttpParams({
      fromObject: {
        username: username,
        offset: offset.toString(10),
        limit: limit.toString(10)
      }
    });
    return this.httpClient.get<UserProfile[]>(this.userBaseUri, {params})
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
   * Load all card decks created by user with userid {@code userid} from
   * the backend.
   *
   * @param userid to query decks for
   * @param offset of the page.
   * @param limit of results returned.
   */
  getDecks(userid: number, limit: number, offset: number): Observable<DeckSimple[]> {
    console.log('load card decks for user: ' + userid);
    const params = new HttpParams({
      fromObject: {
        offset: offset.toString(10),
        limit: limit.toString(10)
      }
    });
    return this.httpClient.get<DeckSimple[]>(`${this.userBaseUri}/${userid}/decks`, { params })
      .pipe(tap(null, this.errorHandler.handleError('Could not load User Decks')));
  }

  /**
   * Load all card revisions created by user with userid {@code userid} from
   * the backend.
   *
   * @param userid to query revisions for
   * @param offset of the page.
   * @param limit of results returned.
   */
  getRevisions(userid: number, limit: number, offset: number): Observable<RevisionDetailed[]> {
    console.log('load card revisions for user: ' + userid);
    const params = new HttpParams({
      fromObject: {
        offset: offset.toString(10),
        limit: limit.toString(10)
      }
    });
    return this.httpClient.get<RevisionDetailed[]>(`${this.userBaseUri}/${userid}/revisions`, { params })
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
}
