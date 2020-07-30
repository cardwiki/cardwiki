import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CardUpdate } from 'src/app/dtos/cardUpdate';
import { Globals } from '../../../global/globals';
import { ImageService } from '../../../services/image.service';

@Component({
  selector: 'app-card-form',
  templateUrl: './card-form.component.html',
  styleUrls: ['./card-form.component.css'],
})
export class CardFormComponent {
  originalFilenameFront = 'Choose file';
  originalFilenameBack = 'Choose file';

  @Input() action: string;
  @Input() card: CardUpdate;
  @Output() cardSubmit: EventEmitter<CardUpdate> = new EventEmitter();
  @Output() cancel: EventEmitter<void> = new EventEmitter();
  constructor(public globals: Globals, public imageService: ImageService) {}

  autoSizeTextarea(event: Event): void {
    const textArea = event.target as HTMLElement;
    textArea.style.height =
      Math.max(textArea.offsetHeight, textArea.scrollHeight) + 'px';
  }

  onSubmit(): void {
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

  onCancel(): void {
    console.log('cancelling card form');
    this.cancel.emit();
  }

  async onFileChange(event: Event, side: 'front' | 'back'): Promise<void> {
    console.log('card form onFileChange', event);
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length) {
      const file = target.files[0];
      const formData = new FormData();
      formData.append('file', file, file.name);

      if (side === 'front') {
        this.card.imageFront = await this.imageService
          .upload(formData)
          .toPromise();
        this.originalFilenameFront = file.name;
      } else {
        this.card.imageBack = await this.imageService
          .upload(formData)
          .toPromise();
        this.originalFilenameBack = file.name;
      }
    }
  }

  removeImage(side: string): void {
    if (side === 'front') {
      this.card.imageFront = null;
      this.originalFilenameFront = '';
    } else {
      this.card.imageBack = null;
      this.originalFilenameBack = '';
    }
  }
}
