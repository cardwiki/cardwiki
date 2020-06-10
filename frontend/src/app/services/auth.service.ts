import {Injectable} from '@angular/core';
import {Observable, BehaviorSubject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tap, distinctUntilChanged} from 'rxjs/operators';
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

  private currentUserRoleSubject$: BehaviorSubject<UserRole[]>
  public currentUserRoles$: Observable<UserRole[]>

  private baseUri: string = this.globals.backendUri + '/auth';

  constructor(private httpClient: HttpClient, private globals: Globals, private errorHandler: ErrorHandlerService) {
    this.currentUserRoleSubject$ = new BehaviorSubject(this.getStoredUserRoles())
    this.currentUserRoles$ = this.currentUserRoleSubject$.asObservable()
      .pipe(distinctUntilChanged((a, b) => a.length === b.length && a.every(role => b.includes(role))))

    this.currentUserRoles$.subscribe(roles => console.log('user roles changed to', roles))
  }

  getAuthProviders(): Observable<OAuth2ProviderDto[]> {
    return this.httpClient.get<OAuth2ProviderDto[]>(this.baseUri + '/providers')
      .pipe(tap(null, this.errorHandler.handleError('Could not fetch Login Providers')))
  }

  whoAmI(): Observable<WhoAmI> {
    return this.httpClient.get<WhoAmI>(this.baseUri + '/whoami')
      .pipe(
        tap(null, this.errorHandler.handleError('Could not fetch user info')),
        tap(whoAmI => this.updateWhoAmI(whoAmI))
      )
  }

  getProviderUrl(provider: string): string {
    return this.baseUri + '/providers/' + provider;
  }

  register(username: string): Observable<UserRegistration> {
    return this.httpClient.post<UserRegistration>(this.globals.backendUri + '/users', {username: username, description: ''})
      .pipe(
        tap(null, this.errorHandler.handleError('Could not register')),
        tap(res => this.updateWhoAmI({ ...this.getStoredAuth().whoAmI, hasAccount: true }))
      )
  }

  getCurrentUserRoles(): UserRole[] {
    return this.currentUserRoleSubject$.value;
  }

  logoutUser(): void {
    this.updateWhoAmI(undefined)
    this.updateToken(undefined)
  }

  /**
   * Update stored token and update user roles
   * @param token 
   */
  updateToken(token: string): void {
    this.storeAuth({
      ...this.getStoredAuth(),
      token,
    })
    this.refreshUserRoles()
  }

  getToken(): string {
    return this.getStoredAuth().token
  }

  /**
   * Update stored whoAmI and udpate user roles
   * @param whoAmI 
   */
  private updateWhoAmI(whoAmI: WhoAmI): void {
    this.storeAuth({
      ...this.getStoredAuth(),
      whoAmI,
    })
    this.refreshUserRoles()
  }

  /**
   * Update user roles based on stored auth data
   */
  private refreshUserRoles(): void {
    this.currentUserRoleSubject$.next(this.getStoredUserRoles())
  }

  /**
   * Get stored user roles if they are not expired yet
   */
  private getStoredUserRoles(): UserRole[] {
    const auth = this.getStoredAuth()
    // Not logged in or expired
    if (!auth.whoAmI || !auth.token || !auth.whoAmI.hasAccount)
      return ['ANONYMOUS']
    if (this.getTokenExpirationDate(auth.token).valueOf() < new Date().valueOf())
      return ['ANONYMOUS']

    return auth.whoAmI.admin ?
      ['USER', 'ADMIN'] : ['USER']
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

  /**
   * Read-only copy of current auth data
   */
  private getStoredAuth(): AuthStore {
    const serialized = localStorage.getItem('auth')
    const authData = serialized ? JSON.parse(serialized) : {}
    Object.freeze(authData) // Prevent trying to update this object instead of updating it in localStorage
    return authData
  }

  /**
   * Store auth data in localStorage
   * @param auth auth data to be stored
   */
  private storeAuth(auth: AuthStore) {
    const serialized = JSON.stringify(auth)
    localStorage.setItem('auth', serialized)
  }
}

// No enum so it's easy for direct use in html templates
export type UserRole = 'ANONYMOUS' | 'USER' | 'ADMIN'

interface AuthStore {
  whoAmI?: WhoAmI,
  token?: string,
}