import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { CardContent } from 'src/app/dtos/cardContent';
import { CardService } from 'src/app/services/card.service';
import { Location } from '@angular/common';

@Component({
  selector: 'app-card-create',
  templateUrl: './card-create.component.html',
  styleUrls: ['./card-create.component.css']
})
export class CardCreateComponent implements OnInit {

  private deckId: number
  public card = new CardContent(null, null, null, null, null)

  constructor(private cardService: CardService, private route: ActivatedRoute, private location: Location) { }

  ngOnInit(): void {
    this.deckId = Number(this.route.snapshot.paramMap.get('id'))
  }

  cardSubmit(): void {
    console.log('CardCreateComponent.onSubmit', this.deckId, this.card)
    this.cardService.createCard(this.deckId, this.card)
      .subscribe(
        cardDetails => {
          console.log('created card', cardDetails)
          this.location.back()
        },
        error => {
          console.error('error creating card', error)
          alert('Error while creating card')
        }
      )
  }

  cancel(): void {
    this.location.back()
  }
}
