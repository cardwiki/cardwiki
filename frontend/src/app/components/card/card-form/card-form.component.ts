import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CardContent } from 'src/app/dtos/cardContent';
import { Globals } from '../../../global/globals';
import {ImageService} from '../../../services/image.service';

@Component({
  selector: 'app-card-form',
  templateUrl: './card-form.component.html',
  styleUrls: ['./card-form.component.css']
})
export class CardFormComponent implements OnInit {

  imageDataFront: ArrayBuffer;
  imageDataBack: ArrayBuffer;
  imageTypeFront: string;
  imageTypeBack: string;

  @Input() card: CardContent;
  @Output() cardSubmit: EventEmitter<CardContent> = new EventEmitter();
  @Output() cancel: EventEmitter<void> = new EventEmitter();
  constructor(public globals: Globals, public imageService: ImageService) { }

  ngOnInit(): void {
  }

  async onSubmit() {
    console.log('submitting card form', this.card);
    if (this.imageDataFront && this.imageTypeFront) {
      this.card.imageFront = await this.imageService.upload(this.imageDataFront, this.imageTypeFront).toPromise();
    }
    if (this.imageDataBack && this.imageDataBack) {
      this.card.imageBack = await this.imageService.upload(this.imageDataBack, this.imageTypeBack).toPromise();
    }
    this.cardSubmit.emit(this.card);
  }

  onCancel() {
    console.log('cancelling card form');
    this.cancel.emit();
  }

  onFileChange(event: any, side: string) {
    const reader = new FileReader();
    if (event.target.files && event.target.files.length) {
      const [file] = event.target.files;
      reader.readAsArrayBuffer(file);
      reader.onload = () => {
        if (side === 'front') {
          this.imageDataFront = reader.result as ArrayBuffer;
          this.imageTypeFront = file.type;
          console.log(this.imageTypeFront);
        } else {
          this.imageDataBack = reader.result as ArrayBuffer;
          this.imageTypeBack = file.type;
        }
      };
    }
  }
}
