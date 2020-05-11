import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { CardContent } from 'src/app/dtos/cardContent';
import { CardService } from 'src/app/services/card.service';

@Component({
  selector: 'app-card-create',
  templateUrl: './card-create.component.html',
  styleUrls: ['./card-create.component.css']
})
export class CardCreateComponent implements OnInit {

  private deckId: number
  private card = new CardContent('Sample Front Text', 'Some \n Multi \n Line Text which is a bit longer')

  constructor(private cardService: CardService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.deckId = Number(this.route.snapshot.paramMap.get('id'))
  }

  cardSubmit(): void {
    console.log('CardCreateComponent.onSubmit', this.deckId, this.card)
    this.cardService.createCard(this.deckId, this.card)
      .subscribe(
        cardDetails => {
          console.log('created card', cardDetails)
          alert('Successfully created card')
        },
        error => {
          console.error('error creating card', error)
          alert('Error while creating card')
        }
      )
  }
}
