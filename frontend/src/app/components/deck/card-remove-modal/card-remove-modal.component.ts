import { Component, OnInit, OnDestroy } from '@angular/core';
import { CardSimple } from 'src/app/dtos/cardSimple';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CardService } from 'src/app/services/card.service';
import { Globals } from 'src/app/global/globals';
import { Location } from '@angular/common';
import { SubscriptionLike } from 'rxjs';

@Component({
  selector: 'app-card-remove-modal',
  templateUrl: './card-remove-modal.component.html',
  styleUrls: ['./card-remove-modal.component.css'],
})
export class CardRemoveModalComponent implements OnInit, OnDestroy {
  card: CardSimple;
  message: string = null;

  private locationSubscription: SubscriptionLike;

  constructor(
    public activeModal: NgbActiveModal,
    public globals: Globals,
    private cardService: CardService,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.locationSubscription = this.location.subscribe(() =>
      this.activeModal.dismiss()
    );
  }

  onSubmit(): void {
    this.activeModal.close(
      this.cardService.removeCard(this.card.id, this.message)
    );
  }

  ngOnDestroy(): void {
    this.locationSubscription.unsubscribe();
  }
}
