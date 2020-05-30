import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CategoryService } from '../../../services/category.service';
import { CategoryDetails } from '../../../dtos/categoryDetails';

@Component({
  selector: 'app-category-update',
  templateUrl: './category-update.component.html',
  styleUrls: ['./category-update.component.css']
})
export class CategoryUpdateComponent implements OnInit {
  editCategoryMode: string = 'Update';
  result: { category: CategoryDetails, error: boolean, errorMessage: string };
  messages: { header: string, success: string, error: string };

  constructor(private route: ActivatedRoute, private categoryService: CategoryService) {
    this.route.params.subscribe( async (params) => await this.doSearch(params['id']));
  }

  doSearch(id: number) {
    this.result = this.categoryService.doSearch(id);
    console.log('result', this.result);
  }

  vanishError() {
    if (this.result) {
      this.result.error = false;
    }
  }

  ngOnInit(): void {
    this.messages = { header: 'Update category', success: 'Category successfully updated', error: 'Error updating category'};
  }

}
