import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {CategoryDetails} from '../dtos/categoryDetails';
import { CategoryUpdate } from '../dtos/categoryUpdate';
import { CategorySimple } from '../dtos/categorySimple';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private categoryBaseUri: string = this.globals.backendUri + '/categories';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

// async doSearch(id: number): Promise<any> {
//    return new Promise((resolve, reject) => {
//     this.getCategoryById(id).subscribe((category) => {
//       console.log(category);
//           return resolve(category);
//         },
//         (error) => {
//           if (error.status === 400 || error.status === 404) {
//             reject('Page not found.');
//           }
//           reject(this.handleError(error));
//         });
//   });
//  }

  /**
   * fetches the details of a Category
   * @param id of the Category to find
   * @return object containing the Category found or the error thrown
   */
  doSearch(id: number, filter: string = null) {
    const result = { category: null, error: false, errorMessage: '' };
    this.getCategoryById(id)
      .subscribe((category) => {
          category.id = id;
          result.category = category;
        },
        (error) => {
          console.log(error);
          result.error = true;
          result.errorMessage = this.handleError(error);
        });
    return result;
  }

  /**
   * Handles errors returned by the endpoint
   * @param error returned
   * @return string containing the error message
   */
  handleError(error): string {
      if (error.status === 500 || error.status === 0) {
        return 'Something went wrong while processing your request.'  ;
      }
      if (error.status === 400 || error.status === 404 || error.status === 409) {
        return typeof(error.error) === 'string' ? error.error : error.error.message;
      }
      if (error.status === 401 || error.status === 403) {
        return 'Not authorized: ' + error.status;
      }
      if (error.status === 503) {
        return 'Service unavailable: ' + error.status;
      }
      if (error.error.message && error.error.includes('Validation errors')) {
        const message = error.error.split('[')[1].split(']')[0];
        const messages = message.split(',');
        let result = '';
        for (let i = 0; i < messages.length; i++) {
          result += messages[i].substr(messages[i].indexOf(' '));
        }
        return result;
      }
      return error.error.message ? error.error.message : error.message;
    }

  /**
   * Loads all categories from the backend
   */
  getCategories(): Observable<CategorySimple[]> {
    return this.httpClient.get<CategorySimple[]>(this.categoryBaseUri);
  }

  /**
   * Loads a specific category from the backend
   * @param id of category to load
   */
  getCategoryById(id: number): Observable<CategoryDetails> {
    console.log('Load details for category with id ' + id);
    return this.httpClient.get<CategoryDetails>(this.categoryBaseUri + '/' + id);
  }

  /**
   * Persists category to the backend
   * @param category to persist
   */
  createCategory(category: CategoryUpdate): Observable<CategoryDetails> {
    console.log('Create category with name ' + category.name);
    return this.httpClient.post<CategoryDetails>(this.categoryBaseUri, category);
  }

  /**
   * Edits category in the backend
   * @param id id of the category to update
   * @param category Dto containing the data to update category with
   */
  editCategory(id: number, category: CategoryUpdate): Observable<CategoryDetails> {
    console.log('Edit category with id ' + id);
    return this.httpClient.put<CategoryDetails>(this.categoryBaseUri + '/' + id, category);
  }
}
