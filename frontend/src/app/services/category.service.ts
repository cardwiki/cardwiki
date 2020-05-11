import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Category} from '../dtos/category';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private categoryBaseUri: string = this.globals.backendUri + '/categories';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Handles errors returned by the endpoint
   * @param error returned
   * @return string containing the error message
   */
  handleError(error): string {
      if (error.status === 500 || error.status === 0) {
        if (error.message.includes('ConstraintViolationException')) {
          return 'Invalid input. Category may already exist.';
        }
        return 'Something went wrong while processing your request.'  ;
      }
      if (error.status === 401 || error.status === 403) {
        return 'Not authorized: ' + error.status;
      }
      if (error.status === 503) {
        return 'Service unavailable: ' + error.status;
      }
      const message = error.error.split('[')[1].split(']')[0];
      const messages = message.split(',');
      let result = '';
      for (let i = 0; i < messages.length; i++) {
        result += messages[i].substr(messages[i].indexOf(' '));
      }
      return result;
    }

  /**
   * Loads all categories from the backend
   */
  getCategories(): Observable<Category[]> {
    return this.httpClient.get<Category[]>(this.categoryBaseUri);
  }

  /**
   * Loads a specific category from the backend
   * @param id of category to load
   */
  getCategoryById(id: number): Observable<Category> {
    console.log('Load category details for ' + id);
    return this.httpClient.get<Category>(this.categoryBaseUri + '/' + id);
  }


  /**
   * Persists category to the backend
   * @param category to persist
   */
  createCategory(category: Category): Observable<Category> {
    console.log('Create category with name ' + category.name);
    return this.httpClient.post<Category>(this.categoryBaseUri, category);
  }

  /**
   * Edits category in the backend
   * @param category Dto containing the data to update category with
   */
  editCategory(category: Category): Observable<Category> {
    console.log('Edit category with id ' + category.id);
    return this.httpClient.patch<Category>(this.categoryBaseUri, category);
  }
}
