import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SubscriptionLike } from 'rxjs';
import { Location } from '@angular/common';

@Component({
  selector: 'app-confirm-modal',
  templateUrl: './confirm-modal.component.html',
  styleUrls: ['./confirm-modal.component.css'],
})
export class ConfirmModalComponent implements OnInit, OnDestroy {
  @Input() title: string;
  @Input() message: string;
  @Input() action: string;

  private navigationSubscription: SubscriptionLike;

  constructor(public activeModal: NgbActiveModal, private location: Location) {}

  ngOnInit(): void {
    this.navigationSubscription = this.location.subscribe(() =>
      this.activeModal.dismiss()
    );
  }

  onSubmit(): void {
    this.activeModal.close();
  }

  ngOnDestroy(): void {
    this.navigationSubscription.unsubscribe();
  }
}
