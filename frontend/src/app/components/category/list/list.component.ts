import { Component, Input } from '@angular/core';
import { CategorySimple } from 'src/app/dtos/categorySimple';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css'],
})
export class ListComponent {
  @Input() list: CategorySimple[]; // Note: Currently also used with DeckSimple
  @Input() specs: { listSize: number; pageSize: number; page: number };
  @Input() path: string;

  trackChange(_index: number, item: CategorySimple): number {
    return item.id;
  }
}
