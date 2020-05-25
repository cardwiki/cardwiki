import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DeckService } from 'src/app/services/deck.service';
import { Deck } from 'src/app/dtos/deck';
import { SearchQueryParams } from 'src/app/interfaces/search-query-params';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  private queryParams: SearchQueryParams = {}
  private newQueryParams: SearchQueryParams = {}
  private page: number = 0
  private readonly limit = 10

  private decks: Deck[] = []
  private canLoadMore: boolean = false
  private loading: boolean = false
  
  constructor(private deckService: DeckService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(paramMap => {
      this.queryParams = {
        name: paramMap.get('name') || '',
      }
      this.newQueryParams = Object.assign({}, this.queryParams)
      this.page = 0

      // Don't fetch when visited without search term
      if (paramMap.has('name'))
        this.fetchSearchResults() 
    })
  }

  /**
   * Fetch and load search results
   */
  fetchSearchResults(): void {
    const { name } = this.newQueryParams
    this.loading = true

    this.deckService.searchByName(name, this.page, this.limit)
      .subscribe(decks => {
        if (this.page === 0)
          this.decks = []
        this.decks.push(...decks)
        this.canLoadMore = decks.length === this.limit
      }, error => {
        console.error('Could not search for decks', error)
        alert('Error while searching for decks')
      }, () => this.loading = false)
  }

  /**
   * Update query url which triggers a search
   */
  updateQueryUrl(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: this.newQueryParams,
    })
  }

  loadMore(): void {
    // Search for the same term, but next page
    this.newQueryParams = Object.assign({}, this.queryParams)
    this.page++
    this.fetchSearchResults()
  }

  onSubmit(): void {
    this.updateQueryUrl()
  }
}
