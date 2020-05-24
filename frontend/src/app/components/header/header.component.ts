import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import { Router } from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DeckCreateModalComponent} from '../deck/deck-create-modal/deck-create-modal.component';
import {Observable} from 'rxjs';
import {Deck} from '../../dtos/deck';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  private searchTerm = ''

  constructor(public authService: AuthService, private router: Router, private modalService: NgbModal) { }

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

  openDeckModal() {
    const modalRef = this.modalService.open(DeckCreateModalComponent);

    modalRef.result.then(
      (res: Observable<Deck>) => res.subscribe(
        (deck: Deck) => this.router.navigate(['decks', deck.id])
      )
    ).catch(() => {});
  }
}
