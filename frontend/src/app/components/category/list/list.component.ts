import {Component, Input, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {

  @Input() list: { name, id }[];
  @Input() specs: { listSize: number, pageSize: number, page: number };
  @Input() path: string;
  @Input() messages: { header: string, success: string, error: string };
  filteredList: object[];
  loading: boolean = true;
  error: boolean = false;
  errorMessage: string = null;
  searchField: FormControl = new FormControl();

  constructor() {
    this.loading = false;
  }

  onChanges() {
    this.searchField.valueChanges.subscribe((value) => {
      this.filteredList = [];
      this.list.forEach((item) => {
        if (item.name.toLowerCase().includes(value.toLowerCase())) {
          this.filteredList.push(item);
        }
      });
      this.specs = { listSize: this.filteredList.length, pageSize: this.specs.pageSize, page: 1 } ;
    });
  }

  trackChange(index: number, item: any) {
    return item.id;
  }


  ngOnInit() {
    this.onChanges();
  }

  vanishError () {
    this.error = false;
  }

}
