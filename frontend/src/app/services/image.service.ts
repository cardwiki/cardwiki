import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { Image } from '../dtos/image';
import { catchError } from 'rxjs/operators';
import { ErrorHandlerService } from './error-handler.service';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  private imageBaseUri: string = this.globals.backendUri + '/images';

  constructor(
    private httpClient: HttpClient,
    private globals: Globals,
    private errorHandler: ErrorHandlerService
  ) {}

  /** Uploads image to the backend
   * @param data image formdata
   **/
  upload(data: FormData): Observable<Image> {
    console.log('upload image');
    return this.httpClient
      .post<Image>(this.imageBaseUri, data)
      .pipe(
        catchError(this.errorHandler.handleError('Could not upload Image'))
      );
  }
}
