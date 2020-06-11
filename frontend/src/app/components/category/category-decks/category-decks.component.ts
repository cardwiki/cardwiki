import { Component } from '@angular/core';
import {CategoryDetails} from '../../../dtos/categoryDetails';
import {ActivatedRoute} from '@angular/router';
import {CategoryService} from '../../../services/category.service';
import {DeckSimple} from '../../../dtos/deckSimple';
import {combineLatest} from 'rxjs';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-category-decks',
  templateUrl: './category-decks.component.html',
  styleUrls: ['./category-decks.component.css']
})
export class CategoryDecksComponent {
  category: CategoryDetails;
  filter: string;
  filteredList: DeckSimple[];
  specs: { listSize: number, pageSize: number, page: number };

  constructor(private route: ActivatedRoute, private categoryService: CategoryService) {
    combineLatest([this.route.params, this.route.queryParams])
      .pipe(map(results => ({id: results[0].id, queryParams: results[1]})))
      .subscribe(results => {
        this.filter = results.queryParams.filter;
        this.doSearch(results.id);
      });
  }

  doSearch(id: number) {
    this.categoryService.getCategoryById(id)
      .subscribe((category) => {
          category.id = id;
          this.category = category;
          this.applyFilter();
        });
  }

  applyFilter(): void {
    if (this.category) {
      const filteredList = this.category.decks.filter(
        item => !this.filter ||
        item.name.toLowerCase().includes(this.filter.toLowerCase())
      );
      this.specs = {
        listSize: filteredList.length,
        pageSize: this.specs ? this.specs.pageSize : 20,
        page: this.specs ? this.specs.page : 1
      };
      this.filteredList = filteredList;
    }
  }
}
