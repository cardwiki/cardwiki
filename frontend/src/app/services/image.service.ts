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
  * @param data image data
  * @param type image type
  **/
  upload(data: ArrayBuffer, type: string): Observable<string> {
    console.log('Upload image');
    return this.httpClient.post(this.imageBaseUri, data, {headers: {'content-type': type}, responseType: 'text'});
  }

}
