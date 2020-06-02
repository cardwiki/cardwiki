import { Injectable, ApplicationRef } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private _toasts: Toast[] = [];

  constructor(private applicationRef: ApplicationRef) {
  }

  /**
   * Get visible toasts
   */
  get toasts() {
    return [...this._toasts]
  }

  /**
   * Log and display info for user
   * @param msg message for the user
   */
  info(msg: string) {
    console.info('notification', msg)
    this.show(msg, {
      classname: 'bg-info text-light',
    })
  }

  /**
   * Log and display success for user
   * @param msg message for the user
   */
  success(msg: string) {
    console.log('notification', msg)
    this.show(msg, {
      classname: 'bg-success text-light',
      delay: 3000,
    })
  }

  /**
   * Log and display warning for user
   * @param msg message for the user
   */
  warning(msg: string) {
    console.warn('notification', msg)
    this.show(msg, {
      classname: 'bg-warning',
    })
  }

  /**
   * Log and display error for user
   * @param msg message for the user
   */
  error(msg: string) {
    console.error('notification', msg)
    this.show(msg, {
      classname: 'bg-danger text-light',
    });
  }

  /**
   * Remove a toast
   * 
   * @param toast toast to be removed
   */
  remove(toast: Toast) {
    this._toasts = this._toasts.filter(t => t !== toast)
    this.triggerChangeDetection()
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
    }
    options = { ...defaultOptions, ...options }
    this._toasts.push({ message, options })
    this.triggerChangeDetection()
  }

  // workaround for *ngFor not updating in some components (e.g. deck-view)
  private triggerChangeDetection() {
    this.applicationRef.tick()
  }
}

export interface Toast {
  message: string
  options: ToastOptions
}

export interface ToastOptions {
  autohide?: boolean
  delay?: number
  classname?: string
}