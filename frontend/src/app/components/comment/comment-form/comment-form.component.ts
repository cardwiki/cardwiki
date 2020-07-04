import { Component, Output, EventEmitter, Input, ViewChild, OnInit } from '@angular/core';
import { Globals } from 'src/app/global/globals';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-comment-form',
  templateUrl: './comment-form.component.html',
  styleUrls: ['./comment-form.component.css']
})
export class CommentFormComponent implements OnInit {

  @Input() message: string;
  @Input() action: string;
  @Output() private commentSubmit: EventEmitter<string> = new EventEmitter();
  // cancel button will only be shown when parent subscribes to event
  @Output() private cancel: EventEmitter<string> = new EventEmitter();
  showCancelButton = false;

  @ViewChild('commentForm') private commentForm: NgForm;

  constructor(public globals: Globals) { }

  ngOnInit() {
    this.showCancelButton = this.cancel.observers.length > 0;
  }

  onSubmit() {
    this.commentSubmit.emit(this.message || null);
  }

  onCancel() {
    this.cancel.emit();
  }

  // Can be called from outside via @ViewChild
  reset() {
    this.commentForm.resetForm();
  }
}
