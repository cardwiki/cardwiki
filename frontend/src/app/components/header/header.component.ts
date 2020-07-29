import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import { Router } from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DeckCreateModalComponent} from '../deck/deck-create-modal/deck-create-modal.component';
import {Observable} from 'rxjs';
import {DeckDetails} from '../../dtos/deckDetails';
import { SearchQueryParams } from 'src/app/interfaces/search-query-params';
import { NotificationService } from 'src/app/services/notification.service';
import {ClipboardService} from '../../services/clipboard.service';
import {UiStyleToggleService} from '../../services/ui-style-toggle.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  public searchTerm = '';
  public username$: Observable<string>;
  public clipboardSize: number;

  constructor(private authService: AuthService, private router: Router, private modalService: NgbModal,
              private notificationService: NotificationService, private clipboardService: ClipboardService,
              private uiStyleToggleService: UiStyleToggleService) {

    this.username$ = authService.userName$;
  }

  ngOnInit(): void {
    this.clipboardService.clipboard$.subscribe(clipboard => this.clipboardSize = clipboard.length);
  }

  onSubmit(): void {
    console.log('search', this.searchTerm);
    const queryParams: SearchQueryParams = {
      name: this.searchTerm
    };
    this.router.navigate(['/search'], {
      queryParams
    });
  }

  openDeckModal(): void {
    const modalRef = this.modalService.open(DeckCreateModalComponent);

    modalRef.result.then(
      (res: Observable<DeckDetails>) => res.subscribe(
        (deck: DeckDetails) => {
          this.notificationService.success('Created Deck');
          this.router.navigate(['decks', deck.id]);
        }
      )
    ).catch();
  }

  logout(): void {
    this.authService.logoutUser();
  }

  toggleDarkMode(): void {
    this.uiStyleToggleService.toggle();
  }
}
