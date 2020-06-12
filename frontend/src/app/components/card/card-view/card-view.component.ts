import { Component, OnInit, Input } from '@angular/core';
import { CardContent } from 'src/app/dtos/cardContent';
import {Globals} from '../../../global/globals';

@Component({
  selector: 'app-card-view',
  templateUrl: './card-view.component.html',
  styleUrls: ['./card-view.component.css']
})
export class CardViewComponent implements OnInit {

  @Input() card: CardContent
  @Input() side: 'front' | 'back'

  constructor(public globals: Globals) { }

  ngOnInit(): void {
    console.log(this.card);
    console.log(this.side);
  }

}
