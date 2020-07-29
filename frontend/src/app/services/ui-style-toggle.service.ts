import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {AuthService} from './auth.service';
import {UserService} from './user.service';
import * as $ from 'jquery';

export enum ThemeMode {
  LIGHT, DARK
}

@Injectable({
  providedIn: 'root'
})
export class UiStyleToggleService {

  private readonly THEME_KEY = 'THEME';
  private readonly DARK_THEME_VALUE = 'DARK';
  private readonly LIGHT_THEME_VALUE = 'LIGHT';
  private readonly DARK_THEME_CLASS_NAME = 'theme-dark';

  private darkThemeSelected = true;
  public theme$ = new BehaviorSubject<ThemeMode>(ThemeMode.LIGHT);
  private username: string;

  constructor(private authService: AuthService, private userService: UserService) {
  }

  private async getState(): Promise<string> {
    if (this.authService.isLoggedIn()) {
      return await this.userService.getProfile(this.username).toPromise().then(profile => {
        console.log(profile);
        return profile.theme;
      });
    } else {
      return localStorage.getItem(this.THEME_KEY);
    }
  }

  private setState(theme: string) {
    localStorage.setItem(this.THEME_KEY, theme);
    if (this.authService.isLoggedIn()) {
      const userid = this.authService.getUserId();
      this.userService.setTheme(userid, theme).subscribe(response => {
        console.log(response);
      });
    }
  }

  public setThemeOnStart(): void {
    if (this.authService.isLoggedIn()) {
      this.username = this.authService.getUserName();
    }
    this.isDarkThemeSelected().then(result => {
      if (result) {
        this.setDarkTheme();
      } else {
        this.setLightTheme();
      }
      setTimeout(() => {
        $('html').addClass('animate-colors-transition');
      }, 500);
    });
  }

  public toggle(): void {
    if (this.darkThemeSelected) {
      this.setLightTheme();
    } else {
      this.setDarkTheme();
    }
  }

  private isDarkThemeSelected(): Promise<boolean> {
    return this.getState().then(theme => {
      this.darkThemeSelected = theme === this.DARK_THEME_VALUE;
      return this.darkThemeSelected;
    });
  }

  private setLightTheme() {
    this.setState(this.LIGHT_THEME_VALUE);
    $('html').removeClass(this.DARK_THEME_CLASS_NAME);
    this.darkThemeSelected = false;
    this.theme$.next(ThemeMode.LIGHT);
  }

  private setDarkTheme() {
    this.setState(this.DARK_THEME_VALUE);
    $('html').addClass(this.DARK_THEME_CLASS_NAME);
    this.darkThemeSelected = true;
    this.theme$.next(ThemeMode.DARK);
  }

}
