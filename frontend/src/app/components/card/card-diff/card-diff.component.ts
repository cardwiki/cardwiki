import { Component, OnInit } from '@angular/core';
import {Globals} from "../../../global/globals";
import {CardService} from "../../../services/card.service";
import {ActivatedRoute} from "@angular/router";
import {Location} from "@angular/common";
import {diff_match_patch} from "diff-match-patch";
import {RevisionDetailed} from "../../../dtos/revisionDetailed";
import {CardContent} from "../../../dtos/cardContent";

@Component({
  selector: 'app-card-diff',
  templateUrl: './card-diff.component.html',
  styleUrls: ['./card-diff.component.css']
})
export class CardDiffComponent implements OnInit {

  public cardRevisionOld: RevisionDetailed;
  public cardRevisionNew: RevisionDetailed;
  private cardId: number;
  private revisionIdOld: number;
  private revisionIdNew: number;
  public cardDiff: CardContent;

  constructor(private globals: Globals,private cardService: CardService, private route: ActivatedRoute, private location: Location) { }

  ngOnInit(): void {
    this.cardId = Number(this.route.snapshot.paramMap.get('cardId'));
    this.route.queryParams.subscribe(params => {
      this.revisionIdOld = params['revision'];
      this.revisionIdNew = params['diff'];
    });
    this.fetchRevision();
  }

  fetchRevision(): void {
    console.log('CardRevisionComponent.fetchRevision', this.cardRevisionOld, this.cardRevisionOld);
    this.cardService.fetchRevisionsById(this.revisionIdNew ? [this.revisionIdOld, this.revisionIdNew] : [this.revisionIdOld])
      .subscribe(cardRevisions => {
        console.log('fetched revisions', cardRevisions);
        this.cardRevisionOld = cardRevisions.get(this.revisionIdOld)
        this.cardRevisionNew = cardRevisions.get(this.revisionIdNew)
        if (this.cardRevisionOld.cardId !== this.cardId) this.location.back()
        if (this.revisionIdNew) {
          if (this.cardRevisionNew.cardId !== this.cardId) this.location.back()
          else {
            const dmp = new diff_match_patch();
            this.cardDiff = new CardContent(
              dmp.diff_prettyHtml(dmp.diff_main(this.cardRevisionOld.textFront, this.cardRevisionNew.textFront)),
              dmp.diff_prettyHtml(dmp.diff_main(this.cardRevisionOld.textBack, this.cardRevisionNew.textBack)),
              null, null
            )


          }
        }
      })
  }

}
