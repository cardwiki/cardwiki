import {Component} from '@angular/core';

import {NotificationService, Toast} from '../../services/notification.service';


@Component({
  selector: 'app-toasts-container',
  templateUrl: './toasts-container.component.html',
  styleUrls: ['./toasts-container.component.css'],
  host: {'[class.ngb-toasts]': 'true'}
})
export class ToastsContainerComponent {
  constructor(private notificationService: NotificationService) {}

  get toasts() {
    return this.notificationService.toasts
  }

  remove(toast: Toast) {
    this.notificationService.remove(toast)
  }
}
