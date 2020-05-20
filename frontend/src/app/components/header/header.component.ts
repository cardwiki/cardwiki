import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  private searchTerm = ''

  constructor(public authService: AuthService, private router: Router) { }

  ngOnInit() {
  }

  onSubmit() {
    console.log('search', this.searchTerm)
    this.router.navigate(['/search'], {
      queryParams: {
        name: this.searchTerm
      }
    })
  }
}
