import { Component, OnInit } from '@angular/core';
import {RevisionType} from "../../../dtos/revisionSimple";
import {Page} from "../../../dtos/page";
import {RevisionDetailed} from "../../../dtos/revisionDetailed";
import {Globals} from "../../../global/globals";
import {ActivatedRoute} from "@angular/router";
import {Pageable} from "../../../dtos/pageable";
import {DeckService} from "../../../services/deck.service";

@Component({
  selector: 'app-deck-history',
  templateUrl: './deck-history.component.html',
  styleUrls: ['./deck-history.component.css']
})
export class DeckHistoryComponent implements OnInit {

  readonly revisionTypeToString: { [key in RevisionType]: string } = {
    [RevisionType.CREATE] : 'created',
    [RevisionType.EDIT] : 'edited',
    [RevisionType.DELETE] : 'deleted',
  }

  readonly REVISIONTEXT_TRUNCATE: number = 30;
  readonly REVISION_PAGINATION_LIMIT: number = 20;

  public revisionPage: Page<RevisionDetailed>;
  public revisions: RevisionDetailed[];
  public deckId: number;

  constructor(public globals: Globals, private route: ActivatedRoute, private deckService: DeckService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.revisionPage = null;
      this.revisions = [];
      this.deckId = Number(params.get('id'));
      this.loadRevisions();
    });
  }

  loadRevisions(): void {
    const nextPageNumber = this.revisionPage ? this.revisionPage.pageable.pageNumber + 1 : 0;
    this.deckService.fetchRevisions(this.deckId, new Pageable(nextPageNumber, this.REVISION_PAGINATION_LIMIT))
      .subscribe(revisionPage => {
        this.revisionPage = revisionPage
        this.revisions.push(...revisionPage.content)
      })
  }

}
