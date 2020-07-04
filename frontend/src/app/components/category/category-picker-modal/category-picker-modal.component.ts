import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CategoryService } from 'src/app/services/category.service';
import { Page } from 'src/app/dtos/page';
import { CategorySimple } from 'src/app/dtos/categorySimple';
import { BehaviorSubject, SubscriptionLike } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Pageable } from 'src/app/dtos/pageable';
import { Location } from '@angular/common';

@Component({
  selector: 'app-category-picker-modal',
  templateUrl: './category-picker-modal.component.html',
  styleUrls: ['./category-picker-modal.component.css']
})
export class CategoryPickerModalComponent implements OnInit, OnDestroy {

  @Input() title: string;

  readonly limit = 8;
  page: Page<CategorySimple>;
  categories: CategorySimple[] = [];
  canSubmit = false;
  countNotShown = 0;

  private searchTerm$: BehaviorSubject<string>;
  private locationSubscription: SubscriptionLike;

  constructor(public activeModal: NgbActiveModal, private categoryService: CategoryService, private location: Location) {
    this.searchTerm$ = new BehaviorSubject('');
    this.searchTerm$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
    ).subscribe(term => this.search(term));
  }

  ngOnInit(): void {
    this.locationSubscription = this.location
      .subscribe(() => this.activeModal.dismiss());
  }

  onInput(event: any): void {
    const target = event.target as HTMLInputElement;
    this.searchTerm$.next(target.value);
  }

  search(name: string) {
    this.categoryService.searchByName(name, new Pageable(0, this.limit))
      .subscribe(page => {
        this.page = page;
        this.categories = page.content;
        this.canSubmit = page.totalElements === 1;
        this.countNotShown = page.totalElements - page.numberOfElements;
      });
  }

  onSelect(category: CategorySimple) {
    this.activeModal.close(category);
  }

  onSubmit() {
    this.onSelect(this.categories[0]);
  }

  ngOnDestroy() {
    this.locationSubscription.unsubscribe();
  }
}
