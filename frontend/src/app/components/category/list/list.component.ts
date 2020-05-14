import {Component, Input, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {

  @Input() list: {name, id}[];
  @Input() specs: { listSize: number, pageSize: number, page: number };
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
        if (item.name.includes(value)) {
          this.filteredList.push(item);
        }
      });
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
