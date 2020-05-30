import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {CategoryDetails} from '../../../dtos/categoryDetails';
import { CategorySimple } from 'src/app/dtos/categorySimple';

@Component({
  selector: 'app-category-create',
  templateUrl: './category-create.component.html',
  styleUrls: ['./category-create.component.css']
})
export class CategoryCreateComponent implements OnInit {
  editCategoryMode: string = 'Create';
  messages: { header: string, success: string, error: string };
  default: CategoryDetails = new CategoryDetails;

  constructor(router: Router) {
    router.events.subscribe(e => {
      const navigation = router.getCurrentNavigation();
      this.default.parent = new CategorySimple(null, navigation.extractedUrl.queryParams.parent);
    });
  }

  ngOnInit(): void {
    this.messages = { header: 'Create category', success: 'Category successfully created', error: 'Error creating category' };
  }

}
