import { Component, Output, EventEmitter, Input } from '@angular/core';
import { Globals } from 'src/app/global/globals';

@Component({
  selector: 'app-comment-form',
  templateUrl: './comment-form.component.html',
  styleUrls: ['./comment-form.component.css']
})
export class CommentFormComponent {

  @Input() message: string
  @Input() action: string
  @Output() commentSubmit: EventEmitter<string> = new EventEmitter();
  constructor(public globals: Globals) { }

  onSubmit() {
    this.commentSubmit.emit(this.message || null)
  }
}
