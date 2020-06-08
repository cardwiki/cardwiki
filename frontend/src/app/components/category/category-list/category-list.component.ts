import {Component, OnInit} from '@angular/core';
import {CategoryService} from '../../../services/category.service';
import {ActivatedRoute} from '@angular/router';
import { CategorySimple } from 'src/app/dtos/categorySimple';

@Component({
  selector: 'app-category-list',
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css']
})
export class CategoryListComponent implements OnInit {
  categories: CategorySimple[];
  specs: { listSize: number, pageSize: number, page: number }
  path: string = 'categories';
  error: boolean = false;
  errorMessage: string;

  constructor(private route: ActivatedRoute, private categoryService: CategoryService) { }

  ngOnInit(): void {
    this.fetchCategories();
  }

  vanishError() {
    this.error = false;
  }

  fetchCategories() {
    this.categoryService.getCategories().subscribe(
      (categories) => {
        console.log('Getting categories.');
        this.categories = categories;
        if (categories) {
          this.specs = { listSize: categories.length, pageSize: 20, page: 1};
        }
      },
      (error) => {
        console.log('Could not get categories:');
        console.log(error);
        this.error = true;
        this.errorMessage = this.categoryService.handleError(error);
      }
    );
  }
}
