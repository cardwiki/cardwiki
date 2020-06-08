import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CategoryDetails} from '../../../dtos/categoryDetails';
import {CategoryService} from '../../../services/category.service';

@Component({
  selector: 'app-category-details',
  templateUrl: './category-details.component.html',
  styleUrls: ['./category-details.component.css']
})
export class CategoryDetailsComponent implements OnInit {
  result: { category: CategoryDetails, error: boolean, errorMessage: string };
  messages: { header: string, success: string, error: string };

  constructor(private route: ActivatedRoute, private categoryService: CategoryService) {
    this.route.params.subscribe((params) => this.doSearch(params['id']));
  }

  doSearch(id: number) {
    this.result = this.categoryService.doSearch(id);
  }

  /**
   * Hides the error screen
   */
  vanishError() {
    this.result.error = false;
  }

  ngOnInit(): void {
    this.messages = { header: 'Subcategories', success: 'Success', error: 'Error' };
  }

}
