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
    this.route.params.subscribe(async (params) => await this.doSearch(params['id']));
  }

  async doSearch(id: number) {
    try {
      this.category = await this.categoryService.doSearch(id);
      console.log(this.category);
    } catch (error) {
      this.error = true;
      this.errorMessage = error;
    }
  }

  ngOnInit(): void {
    this.messages = { header: 'Subcategories', success: 'Success', error: 'Error' };
  }

}
