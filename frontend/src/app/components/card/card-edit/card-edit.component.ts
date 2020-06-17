import {Component, OnInit} from '@angular/core';
import {CardService} from "../../../services/card.service";
import {ActivatedRoute} from "@angular/router";
import { Location } from '@angular/common';
import { NotificationService } from 'src/app/services/notification.service';
import { CardUpdate } from 'src/app/dtos/cardUpdate';

@Component({
  selector: 'app-card-edit',
  templateUrl: './card-edit.component.html',
  styleUrls: ['./card-edit.component.css']
})
export class CardEditComponent implements OnInit {
  
  public card: CardUpdate;
  private cardId: number;

  constructor(private cardService: CardService, private route: ActivatedRoute, private location: Location, private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.card = new CardUpdate('', '');
    this.cardId = Number(this.route.snapshot.paramMap.get('cardId'));
    this.fetchCardContent();
  }

  fetchCardContent(): void {
    console.log('CardEditComponent.fetchCardContent', this.cardId);
    this.cardService.fetchCard(this.cardId)
      .subscribe(cardDetails => {
        console.log('fetched card', cardDetails);
        this.card.textFront = cardDetails.textFront;
        this.card.textBack = cardDetails.textBack;
      })
  }

  cardSubmit(): void {
    console.log('CardEditComponent.onSubmit', this.cardId, this.card);
    this.cardService.editCard(this.cardId, this.card)
      .subscribe(cardDetails => {
        console.log('edited card', cardDetails);
        this.notificationService.success('Updated Card')
        this.location.back()
      })
  }

  cancel(): void {
    this.location.back()
  }

}
