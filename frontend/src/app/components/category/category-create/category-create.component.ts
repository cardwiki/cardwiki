import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-category-create',
  templateUrl: './category-create.component.html',
  styleUrls: ['./category-create.component.css']
})
export class CategoryCreateComponent implements OnInit {
  editCategoryMode: string = 'Create';
  messages: { header: string, success: string, error: string };
  constructor() { }

  ngOnInit(): void {
    this.messages = { header: 'Create category', success: 'Category successfully created', error: 'Error creating category' };
  }

}
