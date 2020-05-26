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
   * Load profile of user with userid {@code userid} from
   * the backend.
   *
   * @param userid of user to load profile for
   */
  getProfile(userid: string = '@me'): Observable<UserProfile> {
    console.log('load profile for user: ' + userid);
    return this.httpClient.get<UserProfile>(`${this.userBaseUri}/${userid}/profile`);
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
    return this.httpClient.get<DeckSimple[]>(`${this.userBaseUri}/${userid}/decks`, { params });
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
    return this.httpClient.get<RevisionDetailed[]>(`${this.userBaseUri}/${userid}/revisions`, { params });
  }
}
