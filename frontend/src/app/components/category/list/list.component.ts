import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {

  @Input() list: { name, id }[];
  @Input() specs: { listSize: number, pageSize: number, page: number };
  @Input() path: string;

  constructor() {
  }

  trackChange(index: number, item: any) {
    return item.id;
  }

  ngOnInit() {
  }
}
