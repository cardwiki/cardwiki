import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";
import {AuthService} from "./auth.service";
import {Router} from "@angular/router";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {NotificationService} from "./notification.service";
import {ClipboardService} from "./clipboard.service";
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

  private getState(): string {
    return localStorage.getItem(this.THEME_KEY)

  }

  private setState(theme: string) {
    localStorage.setItem(this.THEME_KEY, this.DARK_THEME_VALUE);

  }

  public setThemeOnStart() {
    if (this.isDarkThemeSelected()) {
      this.setDarkTheme();
    } else {
      this.setLightTheme();
    }
    setTimeout(() => {
      document.body.classList.add('animate-colors-transition');
    }, 500)
  }

  public toggle() {
    if (this.darkThemeSelected) {
      this.setLightTheme();
    } else {
      this.setDarkTheme();
    }
  }

  private isDarkThemeSelected(): boolean {
    this.darkThemeSelected = this.getState() === this.DARK_THEME_VALUE;
    return this.darkThemeSelected;
  }

  private setLightTheme() {
    localStorage.setItem(this.THEME_KEY, this.LIGHT_THEME_VALUE);
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
