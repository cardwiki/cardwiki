import { Component, OnInit } from '@angular/core';
import {Category} from '../../../dtos/category';
import {ActivatedRoute, Router} from '@angular/router';
import {CategoryService} from '../../../services/category.service';
import {combineLatest} from 'rxjs';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-category-subcategories',
  templateUrl: './category-subcategories.component.html',
  styleUrls: ['./category-subcategories.component.css']
})
export class CategorySubcategoriesComponent implements OnInit {
  category: Category;
  error: boolean;
  errorMessage: string;
  filter: string;
  filteredList: Category[];
  specs: { listSize: number, pageSize: number, page: number };

  constructor(private route: ActivatedRoute, private categoryService: CategoryService, private router: Router) {
    combineLatest([this.route.params, this.route.queryParams])
      .pipe(map(results => ({id: results[0].id, queryParams: results[1]})))
      .subscribe(results => {
        this.filter = results.queryParams.filter;
        this.doSearch(results.id);
      });
  }

  doSearch(id: number) {
    this.categoryService.getCategoryById(id)
      .subscribe((category) => {
          category.id = id;
          this.category = category;
          this.applyFilter();
        },
        (error) => {
          console.log(error);
          this.error = true;
          this.errorMessage = this.categoryService.handleError(error);
        });
  }

  applyFilter(): void {
    if (this.category) {
      let filteredList = [];
      this.category.children.forEach((item) => {
        if (!this.filter) {
          filteredList = this.category.children;
        } else if (item.name.toLowerCase().includes(this.filter.toLowerCase())) {
          filteredList.push(item);
        }
      });
      this.specs = {
        listSize: filteredList.length,
        pageSize: this.specs ? this.specs.pageSize : 20,
        page: this.specs ? this.specs.page : 1
      };
      this.filteredList = filteredList;
    }
  }

  /**
   * Hides the error screen
   */
  vanishError() {
    this.error = false;
  }

  ngOnInit(): void {
  }
}