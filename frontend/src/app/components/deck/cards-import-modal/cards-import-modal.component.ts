import { Component, OnInit } from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {DeckService} from '../../../services/deck.service';
import {FormBuilder, Validators, FormGroup} from '@angular/forms';
import {DeckSimple} from '../../../dtos/deckSimple';
import {Router} from '@angular/router';
import {NotificationService} from '../../../services/notification.service';


@Component({
  selector: 'app-cards-import-modal',
  templateUrl: './cards-import-modal.component.html',
  styleUrls: ['./cards-import-modal.component.css']
})
export class CardsImportModalComponent implements OnInit {
  deck: DeckSimple;
  fileForm: FormGroup;
  file: File;

  constructor(
    public activeModal: NgbActiveModal,
    private deckService: DeckService,
    private formBuilder: FormBuilder,
    private router: Router,
    private notificationService: NotificationService,
  ) {
    this.fileForm = this.formBuilder.group({
      fileSelect: ['', { validators : [Validators.required, this.extensionInvalid.bind(this)], updateOn: 'change' }],
    });
  }

  ngOnInit(): void {
  }

  async onFileChange(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.file = <File>event.target.files[0];
    }
  }

 extensionInvalid() {
    if (
      this.fileForm
      && this.fileForm.controls
      && this.fileForm.controls.fileSelect
      && this.fileForm.controls.fileSelect.touched
      && this.fileForm.controls.fileSelect.dirty
    ) {
      // tslint:disable-next-line:max-line-length
      return this.fileForm.controls.fileSelect.value.substr(this.fileForm.controls.fileSelect.value.lastIndexOf('.') + 1).toLocaleLowerCase() === 'csv'
        ? null
        : {'extensionInvalid': true};
    }
  }

  checkErrors() {
    if (this.fileForm.controls.fileSelect.errors) {
      const errors = this.fileForm.controls.fileSelect.errors;
      if (errors.extensionInvalid) {
        this.fileForm.controls['fileSelect'].setValue('');
        return 'File import is only supported for .csv files.';
      }
    }
  }

  importCards(): void {
    const formData = new FormData();
    formData.append('file', this.file, this.file.name);
    this.deckService.import(this.deck.id, formData).subscribe((deckResult) => {
      this.deck = deckResult;
      this.notificationService.success('Imported cards.');
      this.router.navigate(['decks', this.deck.id]);
      window.location.reload();
    });
  }
}