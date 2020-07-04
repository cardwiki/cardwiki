import { Component, OnInit } from '@angular/core';
import {Globals} from 'src/app/global/globals';
import {CardUpdate} from '../../../dtos/cardUpdate';
import {DeckCreateModalComponent} from '../../deck/deck-create-modal/deck-create-modal.component';
import {Observable} from 'rxjs';
import {DeckDetails} from '../../../dtos/deckDetails';
import {Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {NotificationService} from '../../../services/notification.service';
import {ClipboardService} from '../../../services/clipboard.service';
import {TitleService} from '../../../services/title.service';

@Component({
  selector: 'app-clipboard',
  templateUrl: './clipboard-view.component.html',
  styleUrls: ['./clipboard-view.component.css']
})
export class ClipboardViewComponent implements OnInit {

  clipboard: CardUpdate[];

  constructor(public globals: Globals, private router: Router, private modalService: NgbModal,
              private notificationService: NotificationService,
              private clipboardService: ClipboardService, private titleService: TitleService) { }

  ngOnInit(): void {
    this.titleService.setTitle('Clipboard');
    this.clipboardService.clipboard$.subscribe(clipboard => this.clipboard = clipboard);
  }

  remove(card: CardUpdate) {
    this.clipboardService.remove(card);
  }

  openDeckModal() {
    const modalRef = this.modalService.open(DeckCreateModalComponent);
    modalRef.result.then(
      (res: Observable<DeckDetails>) => res.subscribe(
        (deck: DeckDetails) => {
          this.clipboardService.paste(deck.id, this.clipboard).subscribe( () => {
            this.notificationService.success('Created Deck from Clipboard');
            this.router.navigate(['decks', deck.id]);
          });
        }
      )
    ).catch(() => {});
  }

  clear() {
    this.clipboardService.clear();
  }
}
