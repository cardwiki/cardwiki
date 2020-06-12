import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ImageService {

  private imageBaseUri: string = this.globals.backendUri + '/images';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  /** Uploads image to the backend
  * @param data image formdata
  **/
  upload(data: FormData): Observable<string> {
    console.log('Upload image');
    return this.httpClient.post(this.imageBaseUri, data, {responseType: 'text'});
  }

}
