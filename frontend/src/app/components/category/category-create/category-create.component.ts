import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CategoryDetails } from '../../../dtos/categoryDetails';
import { TitleService } from 'src/app/services/title.service';
import { CategoryService } from 'src/app/services/category.service';

@Component({
  selector: 'app-category-create',
  templateUrl: './category-create.component.html',
  styleUrls: ['./category-create.component.css'],
})
export class CategoryCreateComponent implements OnInit {
  editCategoryMode = 'Create' as const;
  title = 'Create category';
  default = new CategoryDetails();
  ready = false;

  constructor(
    route: ActivatedRoute,
    private titleService: TitleService,
    categoryService: CategoryService
  ) {
    route.queryParams.subscribe((params) => {
      const parentId = Number(params.parentId);
      if (isNaN(parentId)) {
        this.ready = true;
      } else {
        // Create as subcategory of specified parent
        categoryService.getCategoryById(parentId).subscribe((category) => {
          this.default.parent = category;
          this.ready = true;
          console.log(this.default);
        });
      }
    });
  }

  ngOnInit(): void {
    this.titleService.setTitle('Create Category', null);
  }
}
