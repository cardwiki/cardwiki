import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CategoryService } from '../../../services/category.service';
import { CategoryDetails } from '../../../dtos/categoryDetails';

@Component({
  selector: 'app-category-update',
  templateUrl: './category-update.component.html',
  styleUrls: ['./category-update.component.css']
})
export class CategoryUpdateComponent {
  editCategoryMode: string = 'Update';
  category: CategoryDetails;
  messages = { header: 'Update category', success: 'Category successfully updated', error: 'Error updating category'};

  constructor(private route: ActivatedRoute, private categoryService: CategoryService) {
    this.route.params.subscribe(params => {
      this.category = null;
      this.doSearch(params.id)
    });
  }

  doSearch(id: number) {
    this.categoryService.getCategoryById(id).subscribe(
      category => this.category = category
    );
  }
}
