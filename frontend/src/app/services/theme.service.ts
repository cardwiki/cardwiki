import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  private readonly THEME_KEY = 'THEME';
  private readonly THEME_CLASS_DARK = 'theme-dark';

  /**
   * Initially load UI Theme
   */
  public init(): void {
    this.applyConfiguredTheme();
  }

  /**
   * Update the UI theme
   *
   * @param config updated configuration
   */
  public changeThemeConfig(config: UiThemeConfig): void {
    document.documentElement.classList.add('animate-colors-transition');
    this.setConfig(config);
    this.applyConfiguredTheme();
  }

  public getConfig(): UiThemeConfig {
    return (localStorage.getItem(this.THEME_KEY) as UiThemeConfig) ?? 'AUTO';
  }

  private setConfig(config: UiThemeConfig): void {
    localStorage.setItem(this.THEME_KEY, config);
  }

  private applyConfiguredTheme(): void {
    const theme = this.getConfiguredTheme();
    if (theme === 'LIGHT') {
      document.documentElement.classList.remove(this.THEME_CLASS_DARK);
    } else {
      document.documentElement.classList.add(this.THEME_CLASS_DARK);
    }
  }

  private getConfiguredTheme(): UiTheme {
    const config = this.getConfig();
    if (config === 'AUTO') {
      return this.getPreferredColorTheme();
    } else {
      return config;
    }
  }

  /**
   * Return preferred theme as indicated by the browser
   */
  private getPreferredColorTheme(): UiTheme {
    return window.matchMedia('(prefers-color-scheme: light)').matches
      ? 'LIGHT'
      : 'DARK';
  }
}

type UiTheme = 'LIGHT' | 'DARK';
export type UiThemeConfig = UiTheme | 'AUTO';
