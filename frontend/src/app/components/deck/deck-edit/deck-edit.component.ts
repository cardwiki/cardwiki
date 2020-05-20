import { Component, OnInit } from '@angular/core';
import {DeckService} from '../../../services/deck.service';
import {ActivatedRoute} from '@angular/router';
import {DeckSimple} from '../../../dtos/deckSimple';
import {Location} from '@angular/common';

@Component({
  selector: 'app-deck-edit',
  templateUrl: './deck-edit.component.html',
  styleUrls: ['./deck-edit.component.css']
})
export class DeckEditComponent implements OnInit {

  deck: DeckSimple;

  constructor(private deckService: DeckService, private route: ActivatedRoute, private location: Location) { }

  ngOnInit(): void {
    const id = +this.route.snapshot.paramMap.get('id');
    this.deckService.getDeckById(id).subscribe(deck => {
      this.deck = deck;
    });
  }

  save(): void {
    this.deckService.updateDeck(this.deck).subscribe(
      () => this.location.back()
    );
  }
}
