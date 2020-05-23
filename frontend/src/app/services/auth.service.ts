import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs/operators';
import * as jwt_decode from 'jwt-decode';
import {Globals} from '../global/globals';
import { OAuthProviders } from '../dtos/oauthProviders';
import { WhoAmI } from '../dtos/whoAmI';
import { UserRegistration } from '../dtos/userRegistration';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUri: string = this.globals.backendUri + '/auth';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getAuthProviders() {
    return this.httpClient.get<OAuthProviders>(this.baseUri + '/providers');
  }

  whoAmI() {
    return this.httpClient.get<WhoAmI>(this.baseUri + '/whoami')
    .pipe(tap(res => {
       localStorage.setItem('hasAccount', String(res.hasAccount));
    }));
  }

  getProviderUrl(provider){
    return this.baseUri + '/providers/' + provider;
  }

  register(username){
    return this.httpClient.post<UserRegistration>(this.globals.backendUri + '/users', {username: username, description: ''})
      .pipe(tap(res => {
        localStorage.setItem('hasAccount', 'true');
      }));
  }

  getToken() {
    return localStorage.getItem('authToken');
  }

  /**
   * Check if a valid JWT token is saved in the localStorage
   */
  isLoggedIn() {
    return !!this.getToken()
      && (this.getTokenExpirationDate(this.getToken()).valueOf() > new Date().valueOf())
      && localStorage.getItem('hasAccount') == 'true';
  }

  logoutUser() {
    localStorage.removeItem('authToken');
  }

  /**
   * Returns the user role based on the current token
   */
  getUserRole() {
    //if (this.getToken() != null) {
    //  const decoded: any = jwt_decode(this.getToken());
    //  const authInfo: string[] = decoded.rol;
    //  if (authInfo.includes('ROLE_ADMIN')) {
    //    return 'ADMIN';
    //  } else if (authInfo.includes('ROLE_USER')) {
    //    return 'USER';
    //  }
    //}
    return 'UNDEFINED';
  }

  setToken(token: string) {
    localStorage.setItem('authToken', token);
  }

  private getTokenExpirationDate(token: string): Date {

    const decoded: any = jwt_decode(token);
    if (decoded.exp === undefined) {
      return null;
    }

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }
}
