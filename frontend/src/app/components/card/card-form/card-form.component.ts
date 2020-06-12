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

  filenameFront: string = 'Choose file';
  filenameBack: string = 'Choose file';
  @Input() card: CardContent;
  @Output() cardSubmit: EventEmitter<CardContent> = new EventEmitter();
  @Output() cancel: EventEmitter<void> = new EventEmitter();
  constructor(public globals: Globals, public imageService: ImageService) { }

  ngOnInit(): void {
  }

  onSubmit() {
    console.log('submitting card form', this.card);
    this.cardSubmit.emit(this.card)
  }

  onCancel() {
    console.log('cancelling card form');
    this.cancel.emit()
  }

  async onFileChange(event: any, side: string) {
    if (event.target.files && event.target.files.length) {
      const file = <File>event.target.files[0];
      const formData = new FormData();
      formData.append('file', file, file.name);

      if (side === 'front') {
        this.card.imageFront = await this.imageService.upload(formData).toPromise();
        this.filenameFront = file.name;
      } else {
        this.card.imageBack = await this.imageService.upload(formData).toPromise();
        this.filenameBack = file.name;
      }
    }
  }
}
