import { Component, OnInit } from '@angular/core';
import { CardService } from '../../../services/card.service';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { NotificationService } from 'src/app/services/notification.service';
import { CardUpdate } from 'src/app/dtos/cardUpdate';
import { TitleService } from 'src/app/services/title.service';

@Component({
  selector: 'app-card-edit',
  templateUrl: './card-edit.component.html',
  styleUrls: ['./card-edit.component.css'],
})
export class CardEditComponent implements OnInit {
  public card: CardUpdate;
  private cardId: number;

  constructor(
    private cardService: CardService,
    private route: ActivatedRoute,
    private location: Location,
    private notificationService: NotificationService,
    private titleService: TitleService
  ) {}

  ngOnInit(): void {
    this.titleService.setTitle('Edit Card');
    this.card = new CardUpdate(null, null, null, null);
    this.cardId = Number(this.route.snapshot.paramMap.get('cardId'));
    this.fetchCardContent();
  }

  fetchCardContent(): void {
    console.log('CardEditComponent.fetchCardContent', this.cardId);
    this.cardService.fetchCard(this.cardId).subscribe((cardUpdate) => {
      console.log('fetched card', cardUpdate);
      this.card = cardUpdate;
    });
  }

  cardSubmit(): void {
    console.log('CardEditComponent.onSubmit', this.cardId, this.card);

    this.cardService.editCard(this.cardId, this.card).subscribe((card) => {
      console.log('edited card', card);
      this.notificationService.success('Updated Card');
      this.location.back();
    });
  }

  cancel(): void {
    this.location.back();
  }
}
