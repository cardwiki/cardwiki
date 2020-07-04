import { Injectable } from '@angular/core';
import { NotificationService } from './notification.service';
import { HttpErrorResponse } from '@angular/common/http';
import { throwError, of, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {

  constructor(private notificationService: NotificationService) { }

  private readonly httpErrorMessages: { [status: string]: (err: HttpErrorResponse) => string } = {
    0: () => 'Could not connect to server',
    400: err => this.getValidationMessage(err),
    401: () => 'Not authenticated. Please log in and try again',
    403: () => 'Invalid authorization. Please try to log out and log in if you should have access to this resource',
    404: () => 'Could not find this resource',
    409: err => this.getValidationMessage(err),
    422: err => this.getValidationMessage(err),
    500: () => 'An internal server error occured',
    503: () => 'Service currently unavailable. Please try again later',
  };

  /**
   * Logs error and shows message to user
   *
   * @param operation - name of the operation that failed
   * @throws error
   */
  public handleError(operation: string) {
    return (error: any): never => {
      this.log(operation, error);
      this.notifyUser(operation, error);

      throw error;
    };
  }

  /**
   * Catch http errors with specific status and resolve with the provided value
   *
   * @param status single or multiple status which should be caught
   * @param value value which should be resolved for further processing
   * @returns error handler which can be used inside catchError
   */
  public catchStatus<T>(status: number | number[], value: T): (error: any) => Observable<T> {
    const statuses = [status].flat();

    return (error: HttpErrorResponse): Observable<T> => {
      if (statuses.includes(error?.status)) {
        return of(value);
      }
      return throwError(error);
    };
  }

  private log(operation: string, error: any): void {
    console.error(operation, error);
  }

  /**
   * Notifies user about error
   * Message is derived from operation name and error data
   *
   * @param operation - name of the operation that failed
   * @param error error to show
   */
  private notifyUser(operation: string, error: any): void {
    const msg = this.getUserMessage(operation, error);
    this.notificationService.error(msg);
  }

  private getUserMessage(operation: string, error: any): string {
    let errMessage: string;

    if (!error || typeof error !== 'object' || !(error instanceof HttpErrorResponse)) {
      errMessage = 'Unknwon Error' + (error ? ': ' + String(error) : '');
    } else if (error.error instanceof ErrorEvent) {
      errMessage = `Client error: ${error.error.message}`;
 } else {
      errMessage = this.getUserMessageForHttpError(error);
 }

    return `${operation}: \n${errMessage}`;
  }

  private getUserMessageForHttpError(httpError: HttpErrorResponse): string {
    if (httpError.status in this.httpErrorMessages) {
      return this.httpErrorMessages[httpError.status](httpError);
    }
    return `Unexpected error: ${httpError.message}`;
  }

  /**
   * Get a user friendly description of validation errors
   *
   * @param httpError error containing validation errors
   */
  private getValidationMessage(httpError: HttpErrorResponse) {
    // Fallback message
    let description = String(httpError.error);

    // Validation error scheme specified in backend
    if (this.isValidationError(httpError.error)) {
      const validationError = httpError.error as ValidationError;
      description = validationError.validation
        .map(err => Object.entries(err).map(([fieldName, description]) => `${fieldName}: ${description}`).join('\n'))
        .join('\n');
    }
    return `Invalid Data\n${description}`;
  }

  /**
   * Check if error is a ValidationError
   * @param httpError
   */
  private isValidationError(error: any) {
    return error && typeof error === 'object' &&
      Array.isArray(error.validation) &&
      error.validation.every((err: any) => typeof err === 'object');
  }
}

interface ValidationError {
  validation: { [fieldName: string]: string }[];
}
