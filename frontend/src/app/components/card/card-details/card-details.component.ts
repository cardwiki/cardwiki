import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Pageable } from 'src/app/dtos/pageable';
import { RevisionDetailed } from 'src/app/dtos/revisionDetailed';
import { CardService } from 'src/app/services/card.service';
import { TitleService } from 'src/app/services/title.service';

@Component({
  selector: 'app-card-details',
  templateUrl: './card-details.component.html',
  styleUrls: ['./card-details.component.scss'],
})
export class CardDetailsComponent implements OnInit {
  public cardRevision: RevisionDetailed;
  public cardRevisionOld: RevisionDetailed;
  public cardRevisionNew: RevisionDetailed;
  public showDiff = false;
  public showRevision = false;

  public deckId: number;
  public cardId: number;

  constructor(
    private cardService: CardService,
    private titleService: TitleService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.cardId = Number(this.route.snapshot.paramMap.get('cardId'));
    this.deckId = Number(this.route.snapshot.paramMap.get('deckId'));
    this.route.queryParams.subscribe((params) => {
      const revision = params['revision'];
      const diff = params['diff'];

      if ('revision' in params && 'diff' in params) {
        this.titleService.setTitle('Card difference');
        this.loadDiff(revision, diff);
      } else if ('revision' in params) {
        this.titleService.setTitle('Card revision');
        this.loadRevision(revision);
      } else {
        this.titleService.setTitle('Card');
        this.loadNewestRevision();
      }
    });
  }

  loadDiff(oldId: number, newId: number): void {
    console.log('loadDiff', oldId, newId);
    this.cardService
      .fetchRevisionsById([oldId, newId])
      .subscribe((cardRevisions) => {
        console.log('fetched revisions', cardRevisions);
        this.cardRevisionOld = cardRevisions[oldId];
        this.cardRevisionNew = cardRevisions[newId];
        this.showDiff = true;
      });
  }

  loadRevision(revisionId: number): void {
    console.log('loadRevision', revisionId);
    this.cardService
      .fetchRevisionsById([revisionId])
      .subscribe((cardRevisions) =>
        this.displaySingleRevision(cardRevisions[revisionId])
      );
  }

  loadNewestRevision(): void {
    console.log('loadNewestRevision');
    this.cardService
      .fetchRevisions(this.cardId, new Pageable(0, 1))
      .subscribe((revisions) =>
        this.displaySingleRevision(revisions.content[0])
      );
  }

  displaySingleRevision(revision: RevisionDetailed): void {
    this.cardRevision = revision;
    this.showRevision = true;
  }
}
