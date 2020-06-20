import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Globals } from "../../../global/globals";
import { CardUpdate } from 'src/app/dtos/cardUpdate';

@Component({
  selector: 'app-card-form',
  templateUrl: './card-form.component.html',
  styleUrls: ['./card-form.component.css']
})
export class CardFormComponent implements OnInit {

  @Input() card: CardUpdate
  @Output() cardSubmit: EventEmitter<CardUpdate> = new EventEmitter();
  @Output() cancel: EventEmitter<void> = new EventEmitter();
  constructor(public globals: Globals) { }

  ngOnInit(): void {
  }

  onSubmit() {
    this.card.message = this.card.message || null
    console.log('submitting card form', this.card)
    this.cardSubmit.emit(this.card)
  }

  onCancel() {
    console.log('cancelling card form')
    this.cancel.emit()
  }
}
