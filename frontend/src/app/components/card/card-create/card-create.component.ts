import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { CardService } from 'src/app/services/card.service';
import { Location } from '@angular/common';
import { NotificationService } from 'src/app/services/notification.service';
import { CardUpdate } from 'src/app/dtos/cardUpdate';
import {CardUpdateDto} from '../../../dtos/cardUpdateDto';

@Component({
  selector: 'app-card-create',
  templateUrl: './card-create.component.html',
  styleUrls: ['./card-create.component.css']
})
export class CardCreateComponent implements OnInit {

  private deckId: number;
  public card: CardUpdate;

  constructor(private cardService: CardService, private route: ActivatedRoute, private location: Location, private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.card = new CardUpdate(null, null, null, null);
    this.deckId = Number(this.route.snapshot.paramMap.get('id'));
  }

  cardSubmit(): void {
    console.log('CardCreateComponent.onSubmit', this.deckId, this.card);
    const cardUpdateDto = new CardUpdateDto(this.card.textFront, this.card.textBack, null, null, this.card.message);
    if (this.card.imageFront) {
      cardUpdateDto.imageFrontFilename = this.card.imageFront.filename;
    }
    if (this.card.imageBack) {
      cardUpdateDto.imageBackFilename = this.card.imageBack.filename;
    }

    this.cardService.createCard(this.deckId, cardUpdateDto)
      .subscribe(
        cardDetails => {
          console.log('created card', cardDetails);
          this.notificationService.success('Created new Card');
          this.location.back();
        });
  }

  cancel(): void {
    this.location.back();
  }
}
