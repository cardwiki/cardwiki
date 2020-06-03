import {Component} from '@angular/core';

import {NotificationService, Toast} from '../../services/notification.service';
import { Observable } from 'rxjs';


@Component({
  selector: 'app-toasts-container',
  templateUrl: './toasts-container.component.html',
  styleUrls: ['./toasts-container.component.css'],
  host: {'[class.ngb-toasts]': 'true'}
})
export class ToastsContainerComponent {
  public toasts$: Observable<Toast[]>

  constructor(private notificationService: NotificationService) {
    this.toasts$ = notificationService.toasts$
  }

  remove(toast: Toast) {
    this.notificationService.remove(toast)
  }
}
