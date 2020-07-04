import { Component, OnInit, OnDestroy } from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {DeckService} from '../../../services/deck.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DeckSimple} from '../../../dtos/deckSimple';
import {DeckDetails} from '../../../dtos/deckDetails';
import { SubscriptionLike } from 'rxjs';
import { Location } from '@angular/common';

@Component({
  selector: 'app-deck-create',
  templateUrl: './deck-fork-modal.component.html',
  styleUrls: ['./deck-fork-modal.component.css']
})
export class DeckForkModalComponent implements OnInit, OnDestroy {
  deckForm: FormGroup;
  deck: DeckDetails;
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

  forkDeck(): void {
    this.activeModal.close(
      this.deckService.copy(this.deck.id, new DeckSimple(null, this.deckForm.value.name))
    );
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
