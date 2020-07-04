import { Component, OnInit } from '@angular/core';
import {DeckService} from '../../../services/deck.service';
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';
import {DeckUpdate} from '../../../dtos/deckUpdate';
import { CategorySimple } from 'src/app/dtos/categorySimple';
import { NotificationService } from 'src/app/services/notification.service';
import { TitleService } from 'src/app/services/title.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CategoryPickerModalComponent } from '../../category/category-picker-modal/category-picker-modal.component';

@Component({
  selector: 'app-deck-edit',
  templateUrl: './deck-edit.component.html',
  styleUrls: ['./deck-edit.component.css']
})
export class DeckEditComponent implements OnInit {

  deckId: number;
  deck: DeckUpdate;

  constructor(
    private deckService: DeckService,
    private modalService: NgbModal,
    private route: ActivatedRoute,
    private location: Location,
    private notificationService: NotificationService,
    private titleService: TitleService,
  ) {}

  ngOnInit(): void {
    this.deckId = +this.route.snapshot.paramMap.get('id');
    this.deckService.getDeckById(this.deckId).subscribe(deck => {
      this.titleService.setTitle(`Edit ${deck.name}`, null);
      this.deck = new DeckUpdate(deck.name, deck.categories);
    });
  }

  save(): void {
    this.deckService.updateDeck(this.deckId, this.deck).subscribe(
      () => {
        this.notificationService.success('Updated Deck');
        this.location.back();
      }
    );
  }

  cancel(): void {
    this.location.back();
  }

  openCategoryPicker(): void {
    const categoryPickerModal = this.modalService.open(CategoryPickerModalComponent);
    categoryPickerModal.componentInstance.title = 'Select category';
    categoryPickerModal.result
      .then((category: CategorySimple) => this.addCategory(category))
      .catch(err => console.log('Category picker cancelled', err));
  }

  addCategory(category: CategorySimple) {
    if (this.deck.categories.some(c => c.id === category.id)) {
      this.notificationService.warning(`Category ${category.name} has already been added`);
    } else {
      this.deck.categories.push(category);
    }
  }

  removeCategory(category: CategorySimple): void {
    this.deck.categories = this.deck.categories.filter(c => c !== category);
  }
}
