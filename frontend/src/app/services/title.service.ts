import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { distinctUntilChanged } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TitleService {

  title$: Observable<string>;
  header$: Observable<string>; // null if header should be hidden

  private readonly titleSubject$: BehaviorSubject<string>;
  private readonly headerSubject$: BehaviorSubject<string>;
  private readonly titleSuffix = 'CardWiki';

  constructor() {
    this.titleSubject$ = new BehaviorSubject(this.titleSuffix);
    this.title$ = this.titleSubject$.asObservable().pipe(distinctUntilChanged());

    this.headerSubject$ = new BehaviorSubject(null);
    this.header$ = this.headerSubject$.asObservable().pipe(distinctUntilChanged());
  }

  /**
   * Set title and header
   * 
   * @param title title shown in the tab preview. null for default
   * @param header header shown in the page. if null it will be omitted
   */
  setTitle(title: string, header = title) {
    title = title ? `${title} - ${this.titleSuffix}` : this.titleSuffix;
    this.titleSubject$.next(title);
    this.headerSubject$.next(header);
  }
}
