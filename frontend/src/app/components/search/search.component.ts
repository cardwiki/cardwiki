import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DeckService } from 'src/app/services/deck.service';
import { DeckDetails } from 'src/app/dtos/deckDetails';
import { SearchQueryParams } from 'src/app/interfaces/search-query-params';
import { Page } from 'src/app/dtos/page';
import { Pageable } from 'src/app/dtos/pageable';
import { TitleService } from 'src/app/services/title.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
})
export class SearchComponent implements OnInit {
  private queryParams: SearchQueryParams = {};
  public newQueryParams: SearchQueryParams = {};
  private readonly limit = 10;

  public page: Page<DeckDetails>;
  public decks: DeckDetails[];
  public loading: boolean;

  constructor(
    private deckService: DeckService,
    private route: ActivatedRoute,
    private router: Router,
    private titleService: TitleService
  ) {}

  ngOnInit(): void {
    this.titleService.setTitle('Decks', 'Deck search');
    this.route.queryParamMap.subscribe((paramMap) => {
      this.queryParams = {
        name: paramMap.get('name') || '',
      };
      this.newQueryParams = Object.assign({}, this.queryParams);
      this.page = null;
      this.decks = [];

      this.fetchSearchResults();
    });
  }

  /**
   * Fetch and load search results
   */
  fetchSearchResults(): void {
    const { name } = this.newQueryParams;
    this.loading = true;
    const pageNumber = this.page ? this.page.pageable.pageNumber + 1 : 0;

    this.deckService
      .searchByName(name, new Pageable(pageNumber, this.limit))
      .subscribe((deckPage) => {
        this.page = deckPage;
        this.decks.push(...deckPage.content);
      })
      .add(() => (this.loading = false));
  }

  /**
   * Update query url which triggers a search
   */
  updateQueryUrl(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: this.newQueryParams,
    });
  }

  loadMore(): void {
    // Search for the same term
    this.newQueryParams = Object.assign({}, this.queryParams);
    this.fetchSearchResults();
  }

  onSubmit(): void {
    this.updateQueryUrl();
  }

  deleteDeck(event: any, deck: DeckDetails): void {
    event.stopPropagation();
    event.preventDefault();
    if (
      confirm(
        `Are you sure you want to permanently delete deck '${deck.name}'?`
      )
    ) {
      this.deckService.delete(deck.id).subscribe((_) => {
        this.decks = this.decks.filter((d) => d !== deck);
      });
    }
  }
}
