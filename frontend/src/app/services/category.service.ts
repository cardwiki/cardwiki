import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {CategoryDetails} from '../dtos/categoryDetails';
import { CategoryUpdate } from '../dtos/categoryUpdate';
import { CategorySimple } from '../dtos/categorySimple';
import { ErrorHandlerService } from './error-handler.service';
import { tap } from 'rxjs/operators';
import { Pageable } from '../dtos/pageable';
import { Page } from '../dtos/page';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private categoryBaseUri: string = this.globals.backendUri + '/categories';

  constructor(private httpClient: HttpClient, private globals: Globals, private errorHandler: ErrorHandlerService) {
  }

  /**
   * Search for categories by name
   * 
   * @param name name to search for
   * @param pageable config for pagination
   */
  searchByName(name: string, pageable: Pageable) {
    console.log('search categories: ' + name);
    const params = {
      name,
      ...pageable.toObject(),
    }
    return this.httpClient.get<Page<CategorySimple>>(this.categoryBaseUri, { params })
      .pipe(tap(null, this.errorHandler.handleError('Could not search for Categories')));
  }

  /**
   * Loads a specific category from the backend
   * @param id of category to load
   */
  getCategoryById(id: number): Observable<CategoryDetails> {
    console.log('Load details for category with id ' + id);
    return this.httpClient.get<CategoryDetails>(this.categoryBaseUri + '/' + id)
      .pipe(tap(null, this.errorHandler.handleError('Could not fetch Category')))
  }

  /**
   * Persists category to the backend
   * @param category to persist
   */
  createCategory(category: CategoryUpdate): Observable<CategoryDetails> {
    console.log('Create category with name ' + category.name);
    return this.httpClient.post<CategoryDetails>(this.categoryBaseUri, category)
      .pipe(tap(null, this.errorHandler.handleError('Could not create Category')))
  }

  /**
   * Edits category in the backend
   * @param id id of the category to update
   * @param category Dto containing the data to update category with
   */
  editCategory(id: number, category: CategoryUpdate): Observable<CategoryDetails> {
    console.log('Edit category with id ' + id);
    return this.httpClient.put<CategoryDetails>(this.categoryBaseUri + '/' + id, category)
      .pipe(tap(null, this.errorHandler.handleError('Could not update Category')))
  }

  /**
   * Deletes a category.
   *
   * @param id of the category to delete.
   */
  deleteCategory(id: number) {
    console.log('Delete category with id ' + id);
    return this.httpClient.delete<void>(this.categoryBaseUri + '/' + id)
      .pipe(tap(null, this.errorHandler.handleError('Could not delete Category')));
  }
}
