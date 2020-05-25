import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {DeckSimple} from "../dtos/deckSimple";
import {RevisionDetailed} from "../dtos/revisionDetailed";
import {UserProfile} from "../dtos/userProfile";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userBaseUri: string = this.globals.backendUri + '/users';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  /**
   * Load profile of user with username {@code username} from
   * the backend.
   *
   * @param username of user to load profile for
   */
  getProfile(username: string): Observable<UserProfile> {
    console.log('load card decks for user: ' + username);
    return this.httpClient.get<UserProfile>(`${this.userBaseUri}/${username}/profile`);
  }

  /**
   * Load all card decks created by user with username {@code username} from
   * the backend.
   *
   * @param username to query decks for
   * @param offset of the page.
   * @param limit of results returned.
   */
  getDecks(username: string, limit: number, offset: number): Observable<DeckSimple[]> {
    console.log('load card decks for user: ' + username);
    const params = new HttpParams({
      fromObject: {
        offset: offset.toString(10),
        limit: limit.toString(10)
      }
    });
    return this.httpClient.get<DeckSimple[]>(`${this.userBaseUri}/${username}/decks`, { params });
  }

  /**
   * Load all card revisions created by user with username {@code username} from
   * the backend.
   *
   * @param username to query revisions for
   * @param offset of the page.
   * @param limit of results returned.
   */
  getRevisions(username: string, limit: number, offset: number): Observable<RevisionDetailed[]> {
    console.log('load card revisions for user: ' + username);
    const params = new HttpParams({
      fromObject: {
        offset: offset.toString(10),
        limit: limit.toString(10)
      }
    });
    return this.httpClient.get<RevisionDetailed[]>(`${this.userBaseUri}/${username}/revisions`, { params });
  }
}
