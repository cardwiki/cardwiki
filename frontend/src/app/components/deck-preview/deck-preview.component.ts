import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-deck-preview',
  templateUrl: './deck-preview.component.html',
  styleUrls: ['./deck-preview.component.css']
})
export class DeckPreviewComponent implements OnInit {

  private deckId: number

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.deckId = Number(this.route.snapshot.paramMap.get('id'))
  }

}
