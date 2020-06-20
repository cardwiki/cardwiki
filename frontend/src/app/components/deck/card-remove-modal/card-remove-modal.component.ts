import { Component, OnInit } from '@angular/core';
import { CardSimple } from 'src/app/dtos/cardSimple';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CardService } from 'src/app/services/card.service';
import { Globals } from 'src/app/global/globals';

@Component({
  selector: 'app-card-remove-modal',
  templateUrl: './card-remove-modal.component.html',
  styleUrls: ['./card-remove-modal.component.css']
})
export class CardRemoveModalComponent implements OnInit {
  card: CardSimple
  message: string = null

  constructor(
    public activeModal: NgbActiveModal,
    public globals: Globals,
    private cardService: CardService,
  ) { }

  ngOnInit(): void {
  }

  onSubmit() {
    this.activeModal.close(
      this.cardService.removeCard(this.card.id, this.message)
    )
  }

}
