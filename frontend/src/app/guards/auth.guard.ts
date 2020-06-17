import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {AuthService, UserRole} from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService,
              private router: Router) {}

  canActivate(): boolean {
    if (this.authService.getUserRoles().includes('USER')) {
      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }
}
