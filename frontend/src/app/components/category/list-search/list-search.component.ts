import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
  selector: 'app-list-search',
  templateUrl: './list-search.component.html',
  styleUrls: ['./list-search.component.css']
})
export class ListSearchComponent implements OnInit {

  @Input() id: number;
  @Input() list: { name, id }[];
  @Input() specs: { listSize: number, pageSize: number, page: number };
  @Input() path: string;
  @Input() messages: { header: string, success: string, error: string };
  filteredList: object[];
  error: boolean = false;
  errorMessage: string = null;
  searchForm: FormGroup;

  @ViewChild('searchFormField') searchChild: ElementRef;

  constructor(private formBuilder: FormBuilder, private router: Router) {
    this.searchForm = this.formBuilder.group({
      search: ['']
    });
  }

  onChanges() {
    if (this.searchForm) {
      this.searchForm.controls.search.valueChanges.subscribe((value) => {
        this.filteredList = [];
        this.list.forEach((item) => {
          if (!value) {
            this.filteredList = this.list;
          }
          if (value && item.name.toLowerCase().includes(value.toLowerCase())) {
            this.filteredList.push(item);
          }
        });
        this.specs = {listSize: this.filteredList.length, pageSize: this.specs.pageSize, page: 1};
      });
    }
  }

  /**
   * applies the search filter and navigates to the result page
   */
  applySearch() {
    const path = this.path === 'categories' ? 'subcategories' : this.path;
    this.router.navigate(['/categories/' + this.id + '/' + path],
      { queryParams: { filter: this.searchForm.value.search || ''}})
      .then(() => (console.log('x')));
  }

  keyDownFunction(event) {
    if (event.key === 'Enter') {
      this.searchChild.nativeElement.blur();
      if (this.id) {
        this.applySearch();
      }
    }
  }

  onReset() {
    this.searchForm.reset();
  }

  ngOnInit() {
    this.onChanges();
  }

  vanishError () {
    this.error = false;
  }
}