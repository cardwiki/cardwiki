import { Component, OnInit } from '@angular/core';
import { TitleService } from 'src/app/services/title.service';
import { Observable } from 'rxjs';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-title',
  templateUrl: './title.component.html',
  styleUrls: ['./title.component.css'],
})
export class TitleComponent implements OnInit {
  header$: Observable<string>;

  constructor(titleService: TitleService, title: Title) {
    this.header$ = titleService.header$;
    titleService.title$.subscribe((t) => title.setTitle(t));
  }

  ngOnInit(): void {}
}
