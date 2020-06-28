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
import {ClipboardService} from '../../services/clipboard.service';

@Component({
  selector: 'app-clipboard',
  templateUrl: './clipboard.component.html',
  styleUrls: ['./clipboard.component.css']
})
export class ClipboardComponent implements OnInit {

  clipboard: CardUpdate[];

  constructor(public globals: Globals, private router: Router, private modalService: NgbModal,
              private notificationService: NotificationService, private cardService: CardService,
              private clipboardService: ClipboardService) { }

  ngOnInit(): void {
    this.clipboardService.clipboard$.asObservable().subscribe(clipboard => this.clipboard = clipboard);
  }

  remove(card: CardUpdate) {
    this.clipboardService.remove(card);
  }

  openDeckModal() {
    const modalRef = this.modalService.open(DeckCreateModalComponent);
    modalRef.result.then(
      (res: Observable<DeckDetails>) => res.subscribe(
        (deck: DeckDetails) => {
          this.clipboardService.paste(deck.id).subscribe( () => {
            this.notificationService.success('Created Deck from Clipboard');
            this.router.navigate(['decks', deck.id]);
          });
        }
      )
    ).catch(() => {});
  }
}
