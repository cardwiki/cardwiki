import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { CardService } from 'src/app/services/card.service';
import { Location } from '@angular/common';
import { NotificationService } from 'src/app/services/notification.service';
import { CardUpdate } from 'src/app/dtos/cardUpdate';
import { TitleService } from 'src/app/services/title.service';

@Component({
  selector: 'app-card-create',
  templateUrl: './card-create.component.html',
  styleUrls: ['./card-create.component.css']
})
export class CardCreateComponent implements OnInit {

  private deckId: number;
  public card: CardUpdate;

  constructor(private cardService: CardService, private route: ActivatedRoute, private location: Location,
              private notificationService: NotificationService, private titleService: TitleService) { }

  ngOnInit(): void {
    this.titleService.setTitle('Create Card', 'Create a new Card');
    this.card = new CardUpdate(null, null, null, null);
    this.deckId = Number(this.route.snapshot.paramMap.get('id'));
  }

  cardSubmit(): void {
    console.log('CardCreateComponent.onSubmit', this.deckId, this.card);

    this.cardService.createCard(this.deckId, this.card)
      .subscribe(
        card => {
          console.log('created card', card);
          this.notificationService.success('Created new Card');
          this.location.back();
        });
  }

  cancel(): void {
    this.location.back();
  }
}
