import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";
import {AuthService} from "./auth.service";
import {UserService} from "./user.service";

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
  private readonly DARK_THEME_CLASS_NAME = 'theme-dark'

  private darkThemeSelected = true;
  public theme$ = new BehaviorSubject<ThemeMode>(ThemeMode.LIGHT)
  private username$: Observable<string>

  constructor(private authService: AuthService, private userService: UserService) {

    this.username$ = authService.userName$;
  }

  private getState(): Promise<string> {
    return this.username$.toPromise().then(username => {
      console.log("theme username", username)
      return this.userService.getProfile(username).toPromise()
    }).then(profile => {
      return profile.theme;
    })
  }

  private setState(theme: string) {
    localStorage.setItem(this.THEME_KEY, theme);
    const userid = this.authService.getUserId()
    if (userid) {
      this.userService.setTheme(userid, theme)
    }
  }

  public setThemeOnStart() {
    this.isDarkThemeSelected().then(result => {
      if (result) {
        this.setDarkTheme();
      } else {
        this.setLightTheme();
      }
      setTimeout(() => {
        document.body.classList.add('animate-colors-transition');
      }, 500)
    })
  }

  public toggle() {
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
    })
  }

  private setLightTheme() {
    this.setState(this.LIGHT_THEME_VALUE);
    document.body.classList.remove(this.DARK_THEME_CLASS_NAME);
    this.darkThemeSelected = false;
    this.theme$.next(ThemeMode.LIGHT);
  }

  private setDarkTheme() {
    this.setState(this.DARK_THEME_VALUE)
    document.body.classList.add(this.DARK_THEME_CLASS_NAME);
    this.darkThemeSelected = true;
    this.theme$.next(ThemeMode.DARK);
  }

}
