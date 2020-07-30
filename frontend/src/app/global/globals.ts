import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class Globals {
  readonly backendUri: string = `${environment.backendBaseUri}/api/v1`;
  readonly backendBaseUri: string = environment.backendBaseUri;
  readonly maxTextSize: number = 1000;
  readonly maxMessageSize = 150;
  readonly maxCommentSize = 500;
}
