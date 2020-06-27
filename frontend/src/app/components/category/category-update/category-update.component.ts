import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CategoryService } from '../../../services/category.service';
import { CategoryDetails } from '../../../dtos/categoryDetails';
import { TitleService } from 'src/app/services/title.service';

@Component({
  selector: 'app-category-update',
  templateUrl: './category-update.component.html',
  styleUrls: ['./category-update.component.css']
})
export class CategoryUpdateComponent {
  editCategoryMode = 'Update' as const;
  category: CategoryDetails;
  title = 'Update category';

  constructor(private route: ActivatedRoute, private categoryService: CategoryService, private titleService: TitleService) {
    this.route.params.subscribe(params => {
      this.category = null;
      this.doSearch(params.id)
    });
  }

  doSearch(id: number) {
    this.categoryService.getCategoryById(id).subscribe(
      category => {
        this.category = category;
        this.titleService.setTitle('Edit ' + category.name, null);
      }
    );
  }
}
