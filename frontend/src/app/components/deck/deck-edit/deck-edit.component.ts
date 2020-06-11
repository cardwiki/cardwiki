import { Component, OnInit } from '@angular/core';
import {DeckService} from '../../../services/deck.service';
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';
import {DeckUpdate} from '../../../dtos/deckUpdate';
import {CategoryService} from '../../../services/category.service';
import { CategorySimple } from 'src/app/dtos/categorySimple';

@Component({
  selector: 'app-deck-edit',
  templateUrl: './deck-edit.component.html',
  styleUrls: ['./deck-edit.component.css']
})
export class DeckEditComponent implements OnInit {

  deckId: number;
  deck: DeckUpdate;
  categories: CategorySimple[];
  selectedCategory: CategorySimple;

  constructor(
    private deckService: DeckService,
    private categoryService: CategoryService,
    private route: ActivatedRoute,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.deckId = +this.route.snapshot.paramMap.get('id');
    this.deckService.getDeckById(this.deckId).subscribe(deck => {
      this.deck = new DeckUpdate(deck.name, deck.categories);
      this.categoryService.getCategories().subscribe(categories => this.categories = categories);
    });
  }

  save(): void {
    this.deckService.updateDeck(this.deckId, this.deck).subscribe(
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

  removeCategory(category: CategorySimple): void {
    const index = this.deck.categories.indexOf(category, 0);
    if (index > -1) {
      this.deck.categories.splice(index, 1);
    }
  }
}
