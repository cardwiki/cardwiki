import { Component, OnInit } from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {DeckService} from '../../../services/deck.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DeckSimple} from '../../../dtos/deckSimple';
import {DeckDetails} from '../../../dtos/deckDetails';

@Component({
  selector: 'app-deck-create',
  templateUrl: './deck-fork-modal.component.html',
  styleUrls: ['./deck-fork-modal.component.css']
})
export class DeckForkModalComponent implements OnInit {
  deckForm: FormGroup;
  deck: DeckDetails;

  constructor(
    public activeModal: NgbActiveModal,
    private deckService: DeckService,
    private formBuilder: FormBuilder,
  ) {
    this.deckForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.pattern(/\S+/)]]
    });
  }

  ngOnInit(): void {
  }

  forkDeck(): void {
    this.activeModal.close(
      this.deckService.copy(this.deck.id, new DeckSimple(null, this.deckForm.value.name))
    );
  }
}
