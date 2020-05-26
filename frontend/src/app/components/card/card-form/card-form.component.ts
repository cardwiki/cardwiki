import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CardContent } from 'src/app/dtos/cardContent';
import { Globals } from "../../../global/globals";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-card-form',
  templateUrl: './card-form.component.html',
  styleUrls: ['./card-form.component.css']
})
export class CardFormComponent implements OnInit {

  @Input() card: CardContent
  @Output() cardSubmit: EventEmitter<CardContent> = new EventEmitter();
  @Output() cancel: EventEmitter<void> = new EventEmitter();
  constructor(private globals: Globals) { }

  ngOnInit(): void {
  }

  onSubmit() {
    console.log('submitting card form', this.card)
    this.cardSubmit.emit(this.card)
  }

  onCancel() {
    console.log('cancelling card form')
    this.cancel.emit()
  }
}
