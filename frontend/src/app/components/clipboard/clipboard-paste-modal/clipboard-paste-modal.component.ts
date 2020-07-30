import { Component, OnInit } from '@angular/core';
import { CardUpdate } from '../../../dtos/cardUpdate';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ClipboardService } from '../../../services/clipboard.service';
import { Globals } from '../../../global/globals';

@Component({
  selector: 'app-clipboard-paste-modal',
  templateUrl: './clipboard-paste-modal.component.html',
  styleUrls: ['./clipboard-paste-modal.component.css'],
})
export class ClipboardPasteModalComponent implements OnInit {
  clipboard: CardUpdate[];
  selection: CardUpdate[] = [];

  constructor(
    public activeModal: NgbActiveModal,
    private clipboardService: ClipboardService,
    public globals: Globals
  ) {}

  ngOnInit(): void {
    this.clipboardService.clipboard$.subscribe(
      (clipboard) => (this.clipboard = clipboard)
    );
  }

  select(card: CardUpdate): void {
    if (this.selection.includes(card)) {
      this.selection = this.selection.filter((c) => c !== card);
    } else {
      this.selection.push(card);
    }
  }

  selectAll(): void {
    this.selection = this.selection === this.clipboard ? [] : this.clipboard;
  }

  paste(): void {
    this.activeModal.close(this.selection);
  }
}
