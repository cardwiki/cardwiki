import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Category} from '../dtos/category';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private categoryBaseUri: string = this.globals.backendUri + '/category';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all categories from the backend
   */
  getCategory(): Observable<Category[]> {
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
   * @param data to update category with
   */
  editCategory(category: Category): Observable<Category> {
    console.log('Edit category with id ' + category.id);
    return this.httpClient.patch<Category>(this.categoryBaseUri, category);
  }
}
