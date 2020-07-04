import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CardUpdate } from 'src/app/dtos/cardUpdate';
import { Globals } from '../../../global/globals';
import {ImageService} from '../../../services/image.service';

@Component({
  selector: 'app-card-form',
  templateUrl: './card-form.component.html',
  styleUrls: ['./card-form.component.css']
})
export class CardFormComponent implements OnInit {

  originalFilenameFront: string = 'Choose file';
  originalFilenameBack: string = 'Choose file';

  @Input() action: string;
  @Input() card: CardUpdate;
  @Output() cardSubmit: EventEmitter<CardUpdate> = new EventEmitter();
  @Output() cancel: EventEmitter<void> = new EventEmitter();
  constructor(public globals: Globals, public imageService: ImageService) { }

  ngOnInit(): void {
  }

  autoSizeTextarea({ target: textArea }: any) {
    textArea.style.height = Math.max(textArea.offsetHeight, textArea.scrollHeight) + 'px';
  }

  onSubmit() {
    console.log('submitting card form', this.card);
    if (this.card.textFront === '') {
      this.card.textFront = null;
    }
    if (this.card.textBack === '') {
      this.card.textBack = null;
    }
    this.card.message = this.card.message || null;
    console.log('submitting card form', this.card);
    this.cardSubmit.emit(this.card);
  }

  onCancel() {
    console.log('cancelling card form');
    this.cancel.emit();
  }

  async onFileChange(event: any, side: string) {
    if (event.target.files && event.target.files.length) {
      const file = <File>event.target.files[0];
      const formData = new FormData();
      formData.append('file', file, file.name);

      if (side === 'front') {
        this.card.imageFront = await this.imageService.upload(formData).toPromise();
        this.originalFilenameFront = file.name;
      } else {
        this.card.imageBack = await this.imageService.upload(formData).toPromise();
        this.originalFilenameBack = file.name;
      }
    }
  }

  removeImage(side: string) {
    if (side === 'front') {
      this.card.imageFront = null;
      this.originalFilenameFront = '';
    } else {
      this.card.imageBack = null;
      this.originalFilenameBack = '';
    }
  }
}
