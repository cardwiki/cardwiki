import { Component, Input, OnInit } from '@angular/core';
import { CardUpdate } from '../../../dtos/cardUpdate';
import { RevisionDetailed } from '../../../dtos/revisionDetailed';

@Component({
  selector: 'app-card-revision',
  templateUrl: './card-revision.component.html',
  styleUrls: ['./card-revision.component.css'],
})
export class CardRevisionComponent implements OnInit {
  @Input() cardRevision: RevisionDetailed;
  public card: CardUpdate;

  constructor() {}

  ngOnInit(): void {
    console.log(this.cardRevision);
    this.card = new CardUpdate(
      this.cardRevision.textFront,
      this.cardRevision.textBack,
      this.cardRevision.imageFront,
      this.cardRevision.imageBack,
      this.cardRevision.message
    );
    console.log(this.card);
  }
}
