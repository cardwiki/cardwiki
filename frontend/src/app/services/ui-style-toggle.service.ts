import { Injectable } from '@angular/core';
import * as $ from 'jquery';

export enum ThemeMode {
  LIGHT,
  DARK,
}

@Injectable({
  providedIn: 'root',
})
export class UiStyleToggleService {
  private readonly THEME_KEY = 'THEME';
  private readonly DARK_THEME_VALUE = 'DARK';
  private readonly LIGHT_THEME_VALUE = 'LIGHT';
  private readonly DARK_THEME_CLASS_NAME = 'theme-dark';

  private getState(): string {
    return localStorage.getItem(this.THEME_KEY);
  }

  private setState(theme: string) {
    localStorage.setItem(this.THEME_KEY, theme);
  }

  public setThemeOnStart(): void {
    $('html').addClass('animate-colors-transition');
    if (this.isDarkThemeSelected()) {
      this.setDarkTheme();
    } else {
      this.setLightTheme();
    }
  }

  public toggle(): void {
    if (this.isDarkThemeSelected()) {
      this.setLightTheme();
    } else {
      this.setDarkTheme();
    }
  }

  private isDarkThemeSelected(): boolean {
    return this.getState() === this.DARK_THEME_VALUE;
  }

  private setLightTheme() {
    this.setState(this.LIGHT_THEME_VALUE);
    $('html').removeClass(this.DARK_THEME_CLASS_NAME);
  }

  private setDarkTheme() {
    this.setState(this.DARK_THEME_VALUE);
    $('html').addClass(this.DARK_THEME_CLASS_NAME);
  }
}
