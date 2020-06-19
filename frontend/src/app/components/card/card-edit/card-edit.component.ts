import {Component, OnInit} from '@angular/core';
import {CardService} from '../../../services/card.service';
import {ActivatedRoute} from '@angular/router';
import { Location } from '@angular/common';
import { NotificationService } from 'src/app/services/notification.service';
import { CardUpdate } from 'src/app/dtos/cardUpdate';
import {CardUpdateDto} from '../../../dtos/cardUpdateDto';

@Component({
  selector: 'app-card-edit',
  templateUrl: './card-edit.component.html',
  styleUrls: ['./card-edit.component.css']
})
export class CardEditComponent implements OnInit {

  public card: CardUpdate;
  private cardId: number;

  constructor(private cardService: CardService, private route: ActivatedRoute, private location: Location,
              private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.card = new CardUpdate(null, null, null, null);
    this.cardId = Number(this.route.snapshot.paramMap.get('cardId'));
    this.fetchCardContent();
  }

  fetchCardContent(): void {
    console.log('CardEditComponent.fetchCardContent', this.cardId);
    this.cardService.fetchCard(this.cardId)
      .subscribe(cardUpdate => {
        console.log('fetched card', cardUpdate);
        this.card = cardUpdate;
      })
  }

  cardSubmit(): void {
    console.log('CardEditComponent.onSubmit', this.cardId, this.card);
    const cardUpdateDto = new CardUpdateDto(this.card.textFront, this.card.textBack, null, null, this.card.message);
    if (this.card.imageFront) {
      cardUpdateDto.imageFrontFilename = this.card.imageFront.filename;
    }
    if (this.card.imageBack) {
      cardUpdateDto.imageBackFilename = this.card.imageBack.filename;
    }
    this.cardService.editCard(this.cardId, cardUpdateDto)
      .subscribe(cardDetails => {
        console.log('edited card', cardDetails);
        this.notificationService.success('Updated Card')
        this.location.back()
      })
  }

  cancel(): void {
    this.location.back();
  }

}
