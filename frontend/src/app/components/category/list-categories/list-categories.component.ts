import {Component, OnInit} from '@angular/core';
import {Category} from '../../../dtos/category';
import {CategoryService} from '../../../services/category.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-list-categories',
  templateUrl: './list-categories.component.html',
  styleUrls: ['./list-categories.component.css']
})
export class ListCategoriesComponent implements OnInit {
  categories: Category[];
  specs: { listSize: number, pageSize: number, page: number }
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
          this.specs = { listSize: categories.length, pageSize: 12, page: 1};
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
