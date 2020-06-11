import { Component } from '@angular/core';
import {CategoryDetails} from '../../../dtos/categoryDetails';
import {ActivatedRoute} from '@angular/router';
import {CategoryService} from '../../../services/category.service';
import {combineLatest} from 'rxjs';
import {map} from 'rxjs/operators';
import { CategorySimple } from 'src/app/dtos/categorySimple';

@Component({
  selector: 'app-category-subcategories',
  templateUrl: './category-subcategories.component.html',
  styleUrls: ['./category-subcategories.component.css']
})
export class CategorySubcategoriesComponent {
  category: CategoryDetails;
  filter: string;
  filteredList: CategorySimple[];
  specs: { listSize: number, pageSize: number, page: number };

  constructor(private route: ActivatedRoute, private categoryService: CategoryService) {
    combineLatest([this.route.params, this.route.queryParams])
      .pipe(map(results => ({id: results[0].id, queryParams: results[1]})))
      .subscribe(results => {
        this.category = null
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
        });
  }

  applyFilter(): void {
    if (this.category) {
      const filteredList = this.category.children.filter(
        item => !this.filter ||
        item.name.toLowerCase().includes(this.filter.toLowerCase())
      );
      this.specs = {
        listSize: filteredList.length,
        pageSize: this.specs ? this.specs.pageSize : 20,
        page: this.specs ? this.specs.page : 1
      };
      this.filteredList = filteredList;
    }
  }
}
