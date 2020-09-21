import { Component, Input, OnInit } from '@angular/core';
import { CardService } from '../../../services/card.service';
import { Router } from '@angular/router';
import { diff_match_patch } from 'diff-match-patch';
import { RevisionDetailed } from '../../../dtos/revisionDetailed';
import { CardUpdate } from '../../../dtos/cardUpdate';

@Component({
  selector: 'app-card-diff',
  templateUrl: './card-diff.component.html',
  styleUrls: ['./card-diff.component.css'],
})
export class CardDiffComponent implements OnInit {
  @Input() old: RevisionDetailed;
  @Input() new: RevisionDetailed;

  public cardDiffHtml: { front: string; back: string };

  constructor(private cardService: CardService, private router: Router) {}

  ngOnInit(): void {
    const dmp = new diff_match_patch();
    const diffFront = dmp.diff_main(this.old.textFront, this.new.textFront);
    const diffBack = dmp.diff_main(this.old.textBack, this.new.textBack);
    dmp.diff_cleanupSemantic(diffFront);
    dmp.diff_cleanupSemantic(diffBack);
    this.cardDiffHtml = {
      front: dmp.diff_prettyHtml(diffFront),
      back: dmp.diff_prettyHtml(diffBack),
    };
    console.log(this.cardDiffHtml);
  }

  revert(): void {
    const cardUpdate: CardUpdate = new CardUpdate(
      this.old.textFront,
      this.old.textBack,
      this.old.imageFront,
      this.old.imageBack,
      'Revert to ' + this.old.message
    );

    this.cardService.editCard(this.new.id, cardUpdate).subscribe(() => {
      this.router.navigate([
        'decks',
        this.old.deck.id,
        'cards',
        this.new.id,
        'history',
      ]);
    });
  }
}
