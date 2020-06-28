import { Component, OnInit } from '@angular/core';
import { TitleService } from 'src/app/services/title.service';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  constructor(private titleService: TitleService) { }

  ngOnInit(): void {
    this.titleService.setTitle(null);
  }

}
