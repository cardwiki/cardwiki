import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-category-create',
  templateUrl: './category-create.component.html',
  styleUrls: ['./category-create.component.css']
})
export class CategoryCreateComponent implements OnInit {
  editCategoryMode: string = 'Create';
  constructor() { }

  ngOnInit(): void {
  }

}
