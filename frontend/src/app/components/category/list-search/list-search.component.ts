import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { CategorySimple } from 'src/app/dtos/categorySimple';

@Component({
  selector: 'app-list-search',
  templateUrl: './list-search.component.html',
  styleUrls: ['./list-search.component.css'],
})
export class ListSearchComponent implements OnInit {
  @Input() id: number;
  @Input() list: CategorySimple[];
  @Input() specs: { listSize: number; pageSize: number; page: number };
  @Input() path: string;
  @Input() messages: { success: string; error: string };
  filteredList: CategorySimple[];
  searchForm: FormGroup;

  @ViewChild('searchFormField') searchChild: ElementRef;

  constructor(private formBuilder: FormBuilder, private router: Router) {
    this.searchForm = this.formBuilder.group({
      search: [''],
    });
  }

  ngOnInit(): void {
    this.onChanges();
  }

  onChanges(): void {
    if (this.searchForm) {
      this.searchForm.controls.search.valueChanges.subscribe((value) => {
        this.filteredList = this.list.filter(
          (item) =>
            !value || item.name.toLowerCase().includes(value.toLowerCase())
        );
        this.specs = {
          listSize: this.filteredList.length,
          pageSize: this.specs.pageSize,
          page: 1,
        };
      });
    }
  }

  /**
   * applies the search filter and navigates to the result page
   */
  applySearch(): void {
    const path = this.path === 'categories' ? 'subcategories' : this.path;
    this.router
      .navigate(['/categories/' + this.id + '/' + path], {
        queryParams: { filter: this.searchForm.value.search || '' },
      })
      .then(() => console.log('x'));
  }

  keyDownFunction(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.searchChild.nativeElement.blur();
      if (this.id) {
        this.applySearch();
      }
    }
  }

  onReset(): void {
    this.searchForm.reset();
  }
}
