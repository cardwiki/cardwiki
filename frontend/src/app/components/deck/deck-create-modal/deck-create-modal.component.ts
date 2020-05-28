import { Component, OnInit } from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {DeckService} from '../../../services/deck.service';
import {FormBuilder, Validators} from '@angular/forms';
import {DeckSimple} from '../../../dtos/deckSimple';

@Component({
  selector: 'app-deck-create',
  templateUrl: './deck-create-modal.component.html',
  styleUrls: ['./deck-create-modal.component.css']
})
export class DeckCreateModalComponent implements OnInit {
  deckForm;

  constructor(
    private activeModal: NgbActiveModal,
    private deckService: DeckService,
    private formBuilder: FormBuilder,
  ) {
    this.deckForm = this.formBuilder.group({
       name: ['', [Validators.required, Validators.pattern(/\S+/)]]
    });
  }

  ngOnInit(): void {
  }

  createDeck(): void {
    this.activeModal.close(
      this.deckService.create(new DeckSimple(null, this.deckForm.value.name))
    );
  }
}
