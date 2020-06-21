import { Component, Output, EventEmitter, Input, ViewChild } from '@angular/core';
import { Globals } from 'src/app/global/globals';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-comment-form',
  templateUrl: './comment-form.component.html',
  styleUrls: ['./comment-form.component.css']
})
export class CommentFormComponent {

  @Input() message: string
  @Input() action: string
  @Output() private commentSubmit: EventEmitter<string> = new EventEmitter();

  @ViewChild('commentForm') private commentForm: NgForm

  constructor(public globals: Globals) { }

  onSubmit() {
    this.commentSubmit.emit(this.message || null)
  }

  // Can be called from outside via @ViewChild
  reset() {
    this.commentForm.resetForm()
  }
}
