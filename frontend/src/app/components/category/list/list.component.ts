import {Component, Input, OnInit} from '@angular/core';
import { CategorySimple } from 'src/app/dtos/categorySimple';
import {CategoryService} from '../../../services/category.service';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {

  @Input() list: CategorySimple[];
  @Input() specs: { listSize: number, pageSize: number, page: number };
  @Input() path: string;

  constructor(private categoryService: CategoryService) {
  }

  trackChange(index: number, item: any) {
    return item.id;
  }

  ngOnInit() {}

  deleteCategory(category: CategorySimple) {
    this.categoryService.deleteCategory(category.id).subscribe(_ => {
      this.list = this.list.filter(c => c !== category);
    });
  }
}
