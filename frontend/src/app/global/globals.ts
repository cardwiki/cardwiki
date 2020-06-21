import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Globals {
  readonly backendUri: string = 'http://localhost:8080/api/v1';
  readonly backendBaseUri: string = 'http://localhost:8080';
  readonly maxTextSize: number = 1000;
  readonly maxMessageSize = 150;
  readonly maxCommentSize = 500;
}
