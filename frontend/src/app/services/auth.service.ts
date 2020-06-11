import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs/operators';
import * as jwt_decode from 'jwt-decode';
import {Globals} from '../global/globals';
import { OAuth2ProviderDto } from '../dtos/oAuth2Provider';
import { WhoAmI } from '../dtos/whoAmI';
import { UserRegistration } from '../dtos/userRegistration';
import { ErrorHandlerService } from './error-handler.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUri: string = this.globals.backendUri + '/auth';

  constructor(private httpClient: HttpClient, private globals: Globals, private errorHandler: ErrorHandlerService) {
  }

  getAuthProviders(): Observable<OAuth2ProviderDto[]> {
    return this.httpClient.get<OAuth2ProviderDto[]>(this.baseUri + '/providers')
      .pipe(tap(null, this.errorHandler.handleError('Could not fetch Login Providers')))
  }

  whoAmI(): Observable<WhoAmI> {
    return this.httpClient.get<WhoAmI>(this.baseUri + '/whoami')
      .pipe(
        tap(null, this.errorHandler.handleError('Could not fetch user info')),
        tap(res => localStorage.setItem('hasAccount', String(res.hasAccount)))
      )
  }

  getProviderUrl(provider: string): string {
    return this.baseUri + '/providers/' + provider;
  }

  register(username: string): Observable<UserRegistration> {
    return this.httpClient.post<UserRegistration>(this.globals.backendUri + '/users', {username: username, description: ''})
      .pipe(
        tap(null, this.errorHandler.handleError('Could not register')),
        tap(res => localStorage.setItem('hasAccount', 'true'))
      )
  }

  getToken(): string {
    return localStorage.getItem('authToken');
  }

  /**
   * Check if a valid JWT token is saved in the localStorage
   */
  isLoggedIn(): boolean {
    return !!this.getToken()
      && (this.getTokenExpirationDate(this.getToken()).valueOf() > new Date().valueOf())
      && localStorage.getItem('hasAccount') == 'true';
  }

  logoutUser(): void {
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

  setToken(token: string): void {
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
