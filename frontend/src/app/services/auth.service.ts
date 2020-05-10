import {Injectable} from '@angular/core';
import {AuthRequest} from '../dtos/auth-request';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs/operators';
import * as jwt_decode from 'jwt-decode';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUri: string = this.globals.backendUri + '/auth';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getAuthProviders(){
    return this.httpClient.get(this.baseUri + '/providers');
  }

  whoAmI(){
    return this.httpClient.get(this.baseUri + '/whoami')
    .pipe(tap(res => {
       localStorage.setItem('loggedIn', res.hasAccount);
    }));
  }

  getProviderUrl(provider){
    return this.baseUri + '/providers/' + provider;
  }

  register(id, username){
    return this.httpClient.post(this.globals.backendUri + '/users', {id: id, username: username, description: ''})
      .pipe(tap(res => {
        localStorage.setItem('loggedIn', res.hasAccount);
      }));
  }

  /**
   * Check if a valid JWT token is saved in the localStorage
   */
  isLoggedIn() {
      return localStorage.getItem('loggedIn') == 'true';
  }

  logoutUser() {
    localStorage.removeItem('loggedIn');
    return this.httpClient.post(this.baseUri + '/logout', {});
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
}
