import { Component, OnInit } from '@angular/core';
import {DeckService} from '../../../services/deck.service';
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';
import {DeckUpdate} from '../../../dtos/deckUpdate';
import {Category} from '../../../dtos/category';
import {CategoryService} from '../../../services/category.service';

@Component({
  selector: 'app-deck-edit',
  templateUrl: './deck-edit.component.html',
  styleUrls: ['./deck-edit.component.css']
})
export class DeckEditComponent implements OnInit {

  deck: DeckUpdate;
  categories: Category[];
  selectedCategory;

  constructor(
    private deckService: DeckService,
    private categoryService: CategoryService,
    private route: ActivatedRoute,
    private location: Location
  ) {}

  ngOnInit(): void {
    const id = +this.route.snapshot.paramMap.get('id');
    this.deckService.getDeckById(id).subscribe(deck => {
      this.deck = deck;
      this.categoryService.getCategories().subscribe(categories => this.categories = categories);
    });
  }

  save(): void {
    this.deckService.updateDeck(this.deck).subscribe(
      () => this.location.back()
    );
  }

  cancel(): void {
    this.location.back()
  }

  addCategory(): void {
    if (this.selectedCategory && !this.deck.categories.includes(this.selectedCategory)) {
      this.deck.categories.push(this.selectedCategory);
    }
  }

  removeCategory(event, category): void {
    const index = this.deck.categories.indexOf(category, 0);
    if (index > -1) {
      this.deck.categories.splice(index, 1);
    }
  }
}
