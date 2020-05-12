import { Component, OnInit, Input } from '@angular/core';
import { CardContent } from 'src/app/dtos/cardContent';

@Component({
  selector: 'app-card-view',
  templateUrl: './card-view.component.html',
  styleUrls: ['./card-view.component.css']
})
export class CardViewComponent implements OnInit {

  @Input() card: CardContent
  @Input() side: 'front' | 'back'

  constructor() { }

  ngOnInit(): void {
  }

}
