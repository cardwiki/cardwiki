import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.css']
})
export class ErrorComponent implements OnInit {
  @Input() errorMessage: string;
  @Output() buttonClick: EventEmitter<any> = new EventEmitter();

  constructor() { }

  onDismiss() {
    this.buttonClick.emit();
  }

  ngOnInit(): void {
  }

}
