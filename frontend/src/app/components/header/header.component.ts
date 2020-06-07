import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import { Router } from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DeckCreateModalComponent} from '../deck/deck-create-modal/deck-create-modal.component';
import {Observable} from 'rxjs';
import {DeckDetails} from '../../dtos/deckDetails';
import { SearchQueryParams } from 'src/app/interfaces/search-query-params';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  public searchTerm = ''

  constructor(public authService: AuthService, private router: Router, private modalService: NgbModal, private notificationService: NotificationService) { }

  ngOnInit() {
  }

  onSubmit() {
    console.log('search', this.searchTerm)
    const queryParams: SearchQueryParams = {
      name: this.searchTerm
    }
    this.router.navigate(['/search'], {
      queryParams
    })
  }

  openDeckModal() {
    const modalRef = this.modalService.open(DeckCreateModalComponent);

    modalRef.result.then(
      (res: Observable<DeckDetails>) => res.subscribe(
        (deck: DeckDetails) => {
          this.notificationService.success('Created Deck')
          this.router.navigate(['decks', deck.id])
        }
      )
    ).catch(() => {});
  }
}
