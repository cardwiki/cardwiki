import {Component, OnInit} from '@angular/core';
import {CardService} from "../../../services/card.service";
import {ActivatedRoute} from "@angular/router";
import {CardContent} from "../../../dtos/cardContent";
import { Location } from '@angular/common';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-card-edit',
  templateUrl: './card-edit.component.html',
  styleUrls: ['./card-edit.component.css']
})
export class CardEditComponent implements OnInit {
  
  private card = new CardContent(null, '', '');
  private deckId: number;
  private cardId: number;

  constructor(private cardService: CardService, private route: ActivatedRoute, private location: Location, private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.deckId = Number(this.route.snapshot.paramMap.get('deckId'));
    this.cardId = Number(this.route.snapshot.paramMap.get('cardId'));
    this.fetchCardContent();
  }

  fetchCardContent(): void {
    console.log('CardEditComponent.fetchCardContent', this.deckId, this.cardId);
    this.cardService.fetchCard(this.deckId, this.cardId)
      .subscribe(cardDetails => {
        console.log('fetched card', cardDetails);
        this.card = cardDetails;
      })
  }

  cardSubmit(): void {
    console.log('CardEditComponent.onSubmit', this.deckId, this.cardId, this.card);
    this.cardService.editCard(this.deckId, this.cardId, this.card)
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
