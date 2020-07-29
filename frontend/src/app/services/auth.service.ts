import {Injectable} from '@angular/core';
import {Observable, BehaviorSubject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tap, distinctUntilChanged, catchError} from 'rxjs/operators';
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

  public userRoles$: Observable<UserRole[]>;
  public userName$: Observable<string>; // null if not logged in

  private userRoleSubject$: BehaviorSubject<UserRole[]>;
  private userNameSubject$: BehaviorSubject<string>;

  private baseUri: string = this.globals.backendUri + '/auth';

  constructor(private httpClient: HttpClient, private globals: Globals, private errorHandler: ErrorHandlerService) {
    this.userRoleSubject$ = new BehaviorSubject(this.getStoredUserRoles());
    this.userRoles$ = this.userRoleSubject$.asObservable()
      .pipe(distinctUntilChanged((a, b) => a.length === b.length && a.every(role => b.includes(role))));

    this.userNameSubject$ = new BehaviorSubject(this.getStoredUserName());
    this.userName$ = this.userNameSubject$.asObservable()
      .pipe(distinctUntilChanged());

    this.userRoles$.subscribe(roles => console.log('user roles changed to', roles));
    this.userName$.subscribe(username => console.log('username changed to ', username));
  }

  getAuthProviders(): Observable<OAuth2ProviderDto[]> {
    return this.httpClient.get<OAuth2ProviderDto[]>(this.baseUri + '/providers')
      .pipe(catchError(this.errorHandler.handleError('Could not fetch Login Providers')));
  }

  whoAmI(): Observable<WhoAmI> {
    return this.httpClient.get<WhoAmI>(this.baseUri + '/whoami')
      .pipe(
        catchError(this.errorHandler.handleError('Could not fetch user info')),
        tap(whoAmI => this.updateWhoAmI(whoAmI))
      );
  }

  getProviderUrl(provider: string): string {
    return this.baseUri + '/providers/' + provider;
  }

  register(username: string): Observable<UserRegistration> {
    return this.httpClient.post<UserRegistration>(this.globals.backendUri + '/users', {username: username, description: ''})
      .pipe(
        catchError(this.errorHandler.handleError('Could not register')),
        tap(res => this.updateWhoAmI({ ...this.getStoredAuth().whoAmI,
                                      hasAccount: true, id: res.id, username: res.username, admin: res.admin }))
      );
  }

  isLoggedIn(): boolean {
    return this.getUserRoles().includes('USER');
  }

  getUserRoles(): UserRole[] {
    return this.userRoleSubject$.value;
  }

  getUserName(): string {
    return this.userNameSubject$.value;
  }

  logoutUser(): void {
    this.resetStoredAuth();
    this.refreshUserRoles();
  }

  /**
   * Update stored JWT token and update user roles
   */
  updateToken(token: string): void {
    this.storeAuth({
      ...this.getStoredAuth(),
      token,
    });
    this.refreshUserRoles();
  }

  getToken(): string {
    return this.getStoredAuth().token;
  }

  getUserId(): number | undefined {
    return this.getStoredAuth().whoAmI?.id;
  }

  updateRedirectUrl(url: string): void {
    this.storeAuth({
      ...this.getStoredAuth(),
      redirectUrl: url,
    });
  }

  clearRedirectUrl(): void {
    this.updateRedirectUrl(undefined);
  }

  getRedirectUrl(): string {
    return this.getStoredAuth().redirectUrl;
  }

  /**
   * Update stored whoAmI and udpate user roles
   */
  private updateWhoAmI(whoAmI: WhoAmI): void {
    this.storeAuth({
      ...this.getStoredAuth(),
      whoAmI,
    });
    this.refreshUserRoles();
  }

  /**
   * Update user roles based on stored auth data
   */
  private refreshUserRoles(): void {
    this.userRoleSubject$.next(this.getStoredUserRoles());
    this.userNameSubject$.next(this.getStoredUserName());
  }

  /**
   * Get stored user roles if they are not expired yet
   */
  private getStoredUserRoles(): UserRole[] {
    const auth = this.getStoredAuth();

    // Token expired
    if (auth.token && this.getTokenExpirationDate(auth.token).valueOf() < new Date().valueOf()) {
      // Remove invalid token so anonymous requests succeed
      this.resetStoredAuth();
      return ['ANONYMOUS'];
    }

    // Not logged in
    if (!auth.whoAmI?.hasAccount || !auth.token) {
      return ['ANONYMOUS'];
    }

    return auth.whoAmI.admin ?
      ['USER', 'ADMIN'] : ['USER'];
  }

  private getStoredUserName(): string {
    return this.getStoredAuth().whoAmI?.username || null;
  }

  private getTokenExpirationDate(token: string): Date {

    const decoded = jwt_decode<JwtTokenContent>(token);
    if (decoded.exp === undefined) {
      return null;
    }

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }

  /**
   * Read-only copy of current auth data
   */
  private getStoredAuth(): AuthStore {
    const serialized = localStorage.getItem('auth');
    const authData = serialized ? JSON.parse(serialized) : {};
    Object.freeze(authData); // Prevent trying to update this object instead of updating it in localStorage
    return authData;
  }

  /**
   * Store auth data in localStorage
   * @param auth auth data to be stored
   */
  private storeAuth(auth: AuthStore) {
    const serialized = JSON.stringify(auth);
    localStorage.setItem('auth', serialized);
  }

  private resetStoredAuth() {
    this.storeAuth({});
  }
}

// No enum so it's easy for direct use in html templates
export type UserRole = 'ANONYMOUS' | 'USER' | 'ADMIN';

interface AuthStore {
  whoAmI?: WhoAmI;
  token?: string;
  redirectUrl?: string;
}

// TODO: check what properties exist
interface JwtTokenContent {
  exp?: number;
}