import {Component, OnInit} from '@angular/core';
import {CategoryService} from '../../../services/category.service';
import { CategorySimple } from 'src/app/dtos/categorySimple';
import { TitleService } from 'src/app/services/title.service';

@Component({
  selector: 'app-category-list',
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css']
})
export class CategoryListComponent implements OnInit {
  categories: CategorySimple[];
  specs: { listSize: number, pageSize: number, page: number }
  path: string = 'categories';

  constructor(private categoryService: CategoryService, private titleService: TitleService) { }

  ngOnInit(): void {
    this.titleService.setTitle('Categories')
    this.fetchCategories();
  }

  fetchCategories() {
    this.categoryService.getCategories().subscribe(
      (categories) => {
        console.log('Getting categories.');
        this.categories = categories;
        if (categories) {
          this.specs = { listSize: categories.length, pageSize: 20, page: 1};
        }
      });
  }
}
