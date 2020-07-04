import { Component, OnInit, OnDestroy } from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {DeckService} from '../../../services/deck.service';
import {FormBuilder, Validators, FormGroup} from '@angular/forms';
import {DeckSimple} from '../../../dtos/deckSimple';
import { Location } from '@angular/common';
import { SubscriptionLike } from 'rxjs';

@Component({
  selector: 'app-deck-create',
  templateUrl: './deck-create-modal.component.html',
  styleUrls: ['./deck-create-modal.component.css']
})
export class DeckCreateModalComponent implements OnInit, OnDestroy {
  deckForm: FormGroup;
  private locationSubscription: SubscriptionLike;

  constructor(
    public activeModal: NgbActiveModal,
    private deckService: DeckService,
    private formBuilder: FormBuilder,
    private location: Location,
  ) {
    this.deckForm = this.formBuilder.group({
       name: ['', [Validators.required, Validators.pattern(/\S+/)]]
    });
  }

  ngOnInit(): void {
    this.locationSubscription = this.location
      .subscribe(() => this.activeModal.dismiss());
  }

  createDeck(): void {
    this.activeModal.close(
      this.deckService.create(new DeckSimple(null, this.deckForm.value.name))
    );
  }

  ngOnDestroy(): void {
    this.locationSubscription.unsubscribe();
  }
}
