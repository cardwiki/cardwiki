import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators, ValidationErrors} from '@angular/forms';
import {CategoryDetails} from '../../../dtos/categoryDetails';
import {CategoryService} from '../../../services/category.service';
import { Location } from '@angular/common';
import { NotificationService } from 'src/app/services/notification.service';
import { Router } from '@angular/router';
import { CategoryUpdate } from 'src/app/dtos/categoryUpdate';
import { CategorySimple } from 'src/app/dtos/categorySimple';

@Component({
  selector: 'app-category-form',
  templateUrl: './category-form.component.html',
  styleUrls: ['./category-form.component.css']
})
export class CategoryFormComponent implements OnInit {

  @Input() mode: 'Create' | 'Update';
  @Input() category: CategoryDetails;
  @Input() messages: { header: string, success: string, error: string };
  @ViewChild('dismissModal') dismissModalButton: ElementRef;
  categoryForm: FormGroup;

  categories: CategorySimple[];
  result: CategoryDetails = new CategoryDetails;

  constructor(private formBuilder: FormBuilder, private categoryService: CategoryService,
              private location: Location, private router: Router, private notificationService: NotificationService) {
    this.categoryForm = this.formBuilder.group({

      name: ['', [Validators.required, Validators.maxLength(200), this.nameIsBlank.bind(this)]],
        parentCategory: ['',
          {
            validators: [Validators.maxLength(200), this.validateCategoryName.bind(this)], updateOn: 'change'
          }]
    });
  }

  ngOnInit(): void {
    this.categoryForm.reset();
    this.fetchCategories();
    this.setDefaults();
  }

  /**
   * Submits form data to createCategory() or editCategory() function
   */
  submitCategoryForm() {
    this.dismissModalButton.nativeElement.click();
    console.log('submitted form values:', this.categoryForm.value);
    const parent = this.categories.find(category => category.name === this.categoryForm.value.parentCategory) || null

    if (this.mode === 'Update') {
      const payload = new CategoryUpdate(this.categoryForm.value.name, parent);
      this.categoryService.editCategory(this.category.id, payload).subscribe(
        (category) => {
          this.notificationService.success('Updated Category')
          this.router.navigate(['categories', category.id])
      });
    } else {
      this.categoryService.createCategory(new CategoryUpdate(this.categoryForm.value.name, parent))
        .subscribe((category) => {
          this.notificationService.success('Created Category')
          this.router.navigate(['categories', category.id])
      });
    }
  }

  /**
   * Validates the value of the form field 'name'
   */
  checkNameErrors() {
    if (this.categoryForm.controls.name.errors) {
      const errors = this.categoryForm.controls.name.errors;
      if (errors.required) {
        return 'Field is required.';
      }
      if (errors.nameIsBlank) {
        return 'Name must not be blank.';
      }
      const result = this.checkCommonErrors(errors);
      return result ? result : null;
    }
    return null;
  }

  /**
   * Validates the value of the form field 'parentCategory'
   */

  checkCategoryErrors() {
    if (this.categoryForm.controls.parentCategory.errors) {
      const errors = this.categoryForm.controls.parentCategory.errors;
      const result = this.checkCommonErrors(errors);
      if (result && result !== null) {
        return result;
      }
      if (errors.categoryNotFound) {
        return 'Category not found.';
      }
    }
    return null;
  }

  checkCommonErrors(errors: ValidationErrors) {
    if (errors.maxlength) {
      return 'Maximum length exceeded.';
    }
    return null;
  }

  /**
   * Checks if the selected category exists in the category list
   */
  validateCategoryName() {
    if (this.categories && this.categoryForm && this.categoryForm.controls) {
      const value = this.categoryForm.controls.parentCategory.value;
      const valid = (value && value !== '') ? this.categories.map(category => category.name).includes(value) : true;
      if (!valid) {
        return {'categoryNotFound': true};
      }
      return null;
    }
    return null;
  }

  nameIsBlank() {
    if (this.categories && this.categoryForm && this.categoryForm.controls && this.categoryForm.controls.name.dirty) {
      return this.categoryForm.controls.name.value && this.categoryForm.controls.name.value.trim() === '' ? {'nameIsBlank': true} : null;
    }
    return null;
  }

  onRefresh(): void {
    this.fetchCategories();
    this.categoryForm.controls['parentCategory'].setValue('');
  }

  onCancel(): void {
    this.location.back()
  }

  fetchCategories() {
    this.categoryService.getCategories().subscribe(
      (categories) => {
        console.log('Fetched categories', categories);
        this.categories = categories;
      });
  }

  setDefaults(): void {
    if (this.categoryForm) {
      this.categoryForm.controls['name'].setValue(this.category.name);
      if (this.category.parent) {
        console.log('parent name:', this.category.parent.name);
        this.categoryForm.controls['parentCategory'].setValue(this.category.parent.name);
      }
    }
  }
}
