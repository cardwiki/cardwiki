import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  private deckName: string
  private categories: string[]

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.deckName = this.route.snapshot.queryParamMap.get('name') || ''
    this.categories = this.route.snapshot.queryParamMap.getAll('category')
  }

}
