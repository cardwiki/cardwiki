import { Component, OnInit } from '@angular/core';
import {Globals} from 'src/app/global/globals';
import {CardUpdate} from '../../dtos/cardUpdate';
import {DeckCreateModalComponent} from '../deck/deck-create-modal/deck-create-modal.component';
import {Observable} from 'rxjs';
import {DeckDetails} from '../../dtos/deckDetails';
import {Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {NotificationService} from '../../services/notification.service';
import {CardService} from '../../services/card.service';

@Component({
  selector: 'app-clipboard',
  templateUrl: './clipboard.component.html',
  styleUrls: ['./clipboard.component.css']
})
export class ClipboardComponent implements OnInit {

  clipboard: CardUpdate[];

  constructor(public globals: Globals, private router: Router, private modalService: NgbModal,
              private notificationService: NotificationService, private cardService: CardService) { }

  ngOnInit(): void {
    this.clipboard = JSON.parse(localStorage.getItem('clipboard'));
  }

  remove(card: CardUpdate) {
    this.clipboard = this.clipboard.filter(c => c !== card);
    if (this.clipboard.length === 0) {
      this.clipboard = null;
      localStorage.removeItem('clipboard');
    } else {
      localStorage.setItem('clipboard', JSON.stringify(this.clipboard));
    }
  }

  openDeckModal() {
    const modalRef = this.modalService.open(DeckCreateModalComponent);

    modalRef.result.then(
      (res: Observable<DeckDetails>) => res.subscribe(
        (deck: DeckDetails) => {
          for (const newCard of this.clipboard) {
            this.cardService.createCard(deck.id, newCard).subscribe();
          }
          localStorage.removeItem('clipboard');
          this.notificationService.success('Created Deck from Clipboard');
          this.router.navigate(['decks', deck.id]);
        }
      )
    ).catch(() => {});
  }
}
