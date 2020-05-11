import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CategoryService } from '../../../services/category.service';
import { Category } from '../../../dtos/category';

@Component({
  selector: 'app-category-update',
  templateUrl: './category-update.component.html',
  styleUrls: ['./category-update.component.css']
})
export class CategoryUpdateComponent implements OnInit {
  editCategoryMode: string = 'Update';
  category: Category = null;
  error: boolean = false;
  errorMessage: string;

  constructor(private route: ActivatedRoute, private categoryService: CategoryService) {
    this.route.params.subscribe( async (params) => await this.doSearch(params['id']));
  }

  doSearch(id: number) {
    this.categoryService.getCategoryById(id)
      .subscribe((category) => {
          this.category = category;
        },
        (error) => {
          if (error.status === 400 || error.status === 404) {
            this.error = true;
            this.errorMessage = 'Page not found.';
          } else {
            console.log(this.error);
            this.error = true;
        //    this.errorMessage = this.categoryService.handleError(error);
            this.errorMessage = 'default error message';
          }
        });
  }

  ngOnInit(): void {
  }

}
