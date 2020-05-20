import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Category} from '../../../dtos/category';
import {CategoryService} from '../../../services/category.service';

@Component({
  selector: 'app-category-details',
  templateUrl: './category-details.component.html',
  styleUrls: ['./category-details.component.css']
})
export class CategoryDetailsComponent implements OnInit {
  category: Category = new Category('');
  messages: { header: string, success: string, error: string };
  error: boolean = false;
  errorMessage: string;

  constructor(private route: ActivatedRoute, private categoryService: CategoryService) {
    this.route.params.subscribe(async (params) => this.doSearch(params['id']));
  }

  doSearch(id: number) {
    this.categoryService.getCategoryById(id)
      .subscribe((category) => {
          this.category = category;
          this.category.id = id;
        },
        (error) => {
          if (error.status === 400 || error.status === 404) {
            this.error = true;
            this.errorMessage = 'Page not found.';
          } else {
            console.log(error);
            this.error = true;
            this.errorMessage = this.categoryService.handleError(error);
          }
        });
  }

  ngOnInit(): void {
    this.messages = { header: 'Subcategories', success: 'Success', error: 'Error' };
  }

}
