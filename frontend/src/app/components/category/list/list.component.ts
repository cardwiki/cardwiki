import {Component, Input, OnInit} from '@angular/core';
import { CategorySimple } from 'src/app/dtos/categorySimple';
import {CategoryService} from '../../../services/category.service';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {

  @Input() list: CategorySimple[]; // Note: Currently also used with DeckSimple
  @Input() specs: { listSize: number, pageSize: number, page: number };
  @Input() path: string;

  constructor(private categoryService: CategoryService) {
  }

  trackChange(index: number, item: any) {
    return item.id;
  }

  ngOnInit() {}

  deleteCategory(category: CategorySimple) {
    if (confirm(`Are you sure you want to permanently delete category '${category.name}'`)) {
      this.categoryService.deleteCategory(category.id).subscribe(_ => {
        this.list = this.list.filter(c => c !== category);
      });
    }
  }
}
