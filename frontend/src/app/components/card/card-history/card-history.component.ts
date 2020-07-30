import { Component, OnInit } from '@angular/core';
import { Page } from '../../../dtos/page';
import { RevisionDetailed } from '../../../dtos/revisionDetailed';
import { Pageable } from '../../../dtos/pageable';
import { ActivatedRoute } from '@angular/router';
import { Globals } from '../../../global/globals';
import { CardService } from '../../../services/card.service';
import { RevisionType } from '../../../dtos/revisionSimple';
import { CardUpdate } from '../../../dtos/cardUpdate';

@Component({
  selector: 'app-card-history',
  templateUrl: './card-history.component.html',
  styleUrls: ['./card-history.component.css'],
})
export class CardHistoryComponent implements OnInit {
  readonly revisionTypeToString: { [key in RevisionType]: string } = {
    [RevisionType.CREATE]: 'created',
    [RevisionType.EDIT]: 'edited',
    [RevisionType.DELETE]: 'deleted',
  };

  readonly REVISIONTEXT_TRUNCATE: number = 30;
  readonly REVISION_PAGINATION_LIMIT: number = 20;

  public revisionPage: Page<RevisionDetailed>;
  public revisions: RevisionDetailed[];
  public deckId: number;
  public cardId: number;

  public selectedRevisionIdOld: number;
  public selectedRevisionIdNew: number;

  constructor(
    public globals: Globals,
    private route: ActivatedRoute,
    private cardService: CardService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.revisionPage = null;
      this.revisions = [];
      this.deckId = Number(params.get('deckId'));
      this.cardId = Number(params.get('cardId'));
      this.loadRevisions();
    });
  }

  loadRevisions(): void {
    const nextPageNumber = this.revisionPage
      ? this.revisionPage.pageable.pageNumber + 1
      : 0;
    this.cardService
      .fetchRevisions(
        this.cardId,
        new Pageable(nextPageNumber, this.REVISION_PAGINATION_LIMIT)
      )
      .subscribe((revisionPage) => {
        this.revisionPage = revisionPage;
        this.revisions.push(...revisionPage.content);
      });
  }

  undo(revisionId: number): void {
    const newRevision: RevisionDetailed = this.revisions.find(
      (x) => x.id === revisionId
    );
    const card: CardUpdate = new CardUpdate(
      newRevision.textFront,
      newRevision.textBack,
      newRevision.imageFront,
      newRevision.imageBack,
      'Revert to ' + newRevision.message
    );
    this.cardService.editCard(this.cardId, card).subscribe((simpleCard) => {
      if (simpleCard.id === this.cardId) {
        this.ngOnInit();
      }
    });
  }
}
