import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, AbstractControl} from '@angular/forms';
import {CategoryDetails} from '../../../dtos/categoryDetails';
import {CategoryService} from '../../../services/category.service';
import { Location } from '@angular/common';
import { NotificationService } from 'src/app/services/notification.service';
import { Router } from '@angular/router';
import { CategoryUpdate } from 'src/app/dtos/categoryUpdate';
import { CategorySimple } from 'src/app/dtos/categorySimple';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CategoryPickerModalComponent } from '../category-picker-modal/category-picker-modal.component';

@Component({
  selector: 'app-category-form',
  templateUrl: './category-form.component.html',
  styleUrls: ['./category-form.component.css']
})
export class CategoryFormComponent implements OnInit {

  @Input() mode: 'Create' | 'Update';
  @Input() category: CategoryDetails;
  @Input() title: string;
  categoryForm: FormGroup;
  parent: CategorySimple;
  nameErrors: string;

  constructor(private formBuilder: FormBuilder, private categoryService: CategoryService, private modalService: NgbModal,
              private location: Location, private router: Router, private notificationService: NotificationService) {
    this.categoryForm = this.formBuilder.group({
      name: ['', [this.notBlankValidator]],
    });
  }

  ngOnInit(): void {
    this.categoryForm.reset();
    this.setDefaults();
    this.nameErrors = null;
    this.categoryForm.statusChanges.subscribe(() => this.nameErrors = this.checkNameErrors());
  }

  /**
   * Submits form data to createCategory() or editCategory() function
   */
  submitCategoryForm(): void {
    console.log('submitted form values:', this.categoryForm.value);
    const payload = new CategoryUpdate(this.categoryForm.value.name, this.parent);

    if (this.mode === 'Update') {
      this.categoryService.editCategory(this.category.id, payload).subscribe(
        (category) => {
          this.notificationService.success('Updated Category');
          this.router.navigate(['categories', category.id]);
      });
    } else {
      this.categoryService.createCategory(payload)
        .subscribe((category) => {
          this.notificationService.success('Created Category');
          this.router.navigate(['categories', category.id]);
      });
    }
  }

  openParentModal(): void {
    const categoryPickerModal = this.modalService.open(CategoryPickerModalComponent);
    categoryPickerModal.componentInstance.title = 'Select parent category';
    categoryPickerModal.result
      .then((category: CategorySimple) => this.parent = category)
      .catch(err => console.log('Parent picker cancelled', err));
  }

  removeParent(): void {
    this.parent = null;
  }

  /**
   * Validates the value of the form field 'name'
   */
  checkNameErrors(): string {
    if (this.categoryForm.controls.name.errors) {
      const errors = this.categoryForm.controls.name.errors;
      if (errors.required) {
        return 'Field is required.';
      }
      if (errors.nameIsBlank) {
        return 'Name must not be blank.';
      }
      if (errors.maxlength) {
        return 'Maximum length exceeded.';
      }
    }
    return null;
  }

  notBlankValidator(control: AbstractControl): { nameIsBlank: boolean } {
    const isValid = (control.value || '').trim().length > 0;
    return isValid ? null : { nameIsBlank: true };
  }

  onCancel(): void {
    this.location.back();
  }

  setDefaults(): void {
    if (this.categoryForm) {
      this.categoryForm.controls['name'].setValue(this.category.name);
      if (this.category.parent) {
        this.parent = this.category.parent;
      }
    }
  }
}
