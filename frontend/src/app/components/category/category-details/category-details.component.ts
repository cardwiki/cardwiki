import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CategoryDetails } from '../../../dtos/categoryDetails';
import { CategoryService } from '../../../services/category.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
  selector: 'app-category-details',
  templateUrl: './category-details.component.html',
  styleUrls: ['./category-details.component.css'],
})
export class CategoryDetailsComponent {
  category: CategoryDetails;
  messages = { header: 'Subcategories', success: 'Success', error: 'Error' };

  constructor(
    private route: ActivatedRoute,
    private categoryService: CategoryService,
    private titleService: TitleService
  ) {
    this.route.params.subscribe((params) => {
      this.category = null;
      this.doSearch(params['id']);
    });
  }

  doSearch(id: number) {
    this.categoryService.getCategoryById(id).subscribe((category) => {
      this.category = category;
      this.titleService.setTitle(category.name, null);
    });
  }
}
