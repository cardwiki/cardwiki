import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { distinctUntilChanged } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  public readonly toasts$: Observable<Toast[]>;

  private toasts: Toast[];
  private readonly toastsSubject: BehaviorSubject<Toast[]>;

  constructor() {
    this.toasts = [];
    this.toastsSubject = new BehaviorSubject([]);
    this.toasts$ = this.toastsSubject.pipe(distinctUntilChanged());
  }

  /**
   * Log and display info for user
   * @param msg message for the user
   */
  info(msg: string): void {
    console.info('notification', msg);
    this.show(msg, {
      classname: 'bg-info text-light',
    });
  }

  /**
   * Log and display success for user
   * @param msg message for the user
   */
  success(msg: string): void {
    console.log('notification', msg);
    this.show(msg, {
      classname: 'bg-success text-light',
      delay: 3000,
    });
  }

  /**
   * Log and display warning for user
   * @param msg message for the user
   */
  warning(msg: string): void {
    console.warn('notification', msg);
    this.show(msg, {
      classname: 'bg-warning',
    });
  }

  /**
   * Log and display error for user
   * @param msg message for the user
   */
  error(msg: string): void {
    console.error('notification', msg);
    this.show(msg, {
      classname: 'bg-danger text-light',
    });
  }

  /**
   * Remove a toast
   *
   * @param toast toast to be removed
   */
  remove(toast: Toast): void {
    this.toasts = this.toasts.filter(t => t !== toast);
    this.updateObservable();
  }

  /**
   * Show a toast
   *
   * @param message message for the user
   * @param [options] options for the toast behavior and design
   */
  private show(message: string, options: ToastOptions = {}) {
    const defaultOptions = {
      autohide: 'delay' in options,
      classname: '',
    };
    options = { ...defaultOptions, ...options };
    this.toasts.push({ message, options });
    this.updateObservable();
  }

  private updateObservable() {
    this.toastsSubject.next([...this.toasts]);
  }
}

export interface Toast {
  message: string;
  options: ToastOptions;
}

export interface ToastOptions {
  autohide?: boolean;
  delay?: number;
  classname?: string;
}
