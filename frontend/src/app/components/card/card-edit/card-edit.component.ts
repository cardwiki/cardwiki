import {Component, OnInit} from '@angular/core';
import {CardService} from "../../../services/card.service";
import {ActivatedRoute} from "@angular/router";
import {CardContent} from "../../../dtos/cardContent";
import { Location } from '@angular/common';

@Component({
  selector: 'app-card-edit',
  templateUrl: './card-edit.component.html',
  styleUrls: ['./card-edit.component.css']
})
export class CardEditComponent implements OnInit {
  
  public card = new CardContent(null, '', '');
  private deckId: number;
  private cardId: number;

  constructor(private cardService: CardService, private route: ActivatedRoute, private location: Location) { }

  ngOnInit(): void {
    this.deckId = Number(this.route.snapshot.paramMap.get('deckId'));
    this.cardId = Number(this.route.snapshot.paramMap.get('cardId'));
    this.fetchCardContent();
  }

  fetchCardContent(): void {
    console.log('CardEditComponent.fetchCardContent', this.deckId, this.cardId);
    this.cardService.fetchCard(this.deckId, this.cardId)
      .subscribe(
        cardDetails => {
          console.log('fetched card', cardDetails);
          this.card = cardDetails;
        },
      error => {
          console.error('error fetching card', error);
        alert('Error while fetching card');
      }
      )
  }

  cardSubmit(): void {
    console.log('CardEditComponent.onSubmit', this.deckId, this.cardId, this.card);
    this.cardService.editCard(this.deckId, this.cardId, this.card)
      .subscribe(
        cardDetails => {
          console.log('edited card', cardDetails);
          this.location.back()
        },
        error => {
          console.error('error editing card', error);
          alert('Error while editing card')
        }
      )
  }

  cancel(): void {
    this.location.back()
  }

}
