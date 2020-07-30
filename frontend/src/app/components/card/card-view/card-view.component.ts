import { Component, OnInit, Input } from '@angular/core';
import { Globals } from '../../../global/globals';
import { CardUpdate } from '../../../dtos/cardUpdate';

@Component({
  selector: 'app-card-view',
  templateUrl: './card-view.component.html',
  styleUrls: ['./card-view.component.css'],
})
export class CardViewComponent implements OnInit {
  @Input() card: CardUpdate;
  @Input() side: 'front' | 'back';

  constructor(public globals: Globals) {}

  ngOnInit(): void {
    console.log(this.card);
    console.log(this.side);
  }
}
