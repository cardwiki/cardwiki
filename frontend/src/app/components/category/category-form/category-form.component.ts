import { Component, Input, OnInit } from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { Category } from '../../../dtos/category';
import { CategoryService } from '../../../services/category.service';

@Component({
  selector: 'app-category-form',
  templateUrl: './category-form.component.html',
  styleUrls: ['./category-form.component.css']
})
export class CategoryFormComponent implements OnInit {

  @Input() mode: String;
  @Input() category: Category;
  categoryForm: FormGroup;
  submitted: boolean = false;
  error: boolean = false;
  errorMessage: string = '';

  categories: Category[];
  result: { name: string, parentCategory: string };
  parentId: number = null;

  constructor(private formBuilder: FormBuilder, private authService: AuthService,
              private router: Router, private categoryService: CategoryService) {
    this.categoryForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.pattern('^[a-zA-Z0-9]+[a-zA-Z0-9 \\/\\-\\.\\,]+$'), Validators.maxLength(200)]],
      parentCategory: ['', [Validators.maxLength(200), Validators.pattern('^[a-zA-Z0-9]+[a-zA-Z0-9 \\/\\-\\.\\,]+$'),
        this.validateCategoryName.bind(this)]]
    });
  }

  submitCategoryForm() {
    console.log('submitted form values:', this.categoryForm.value);
    this.result = this.categoryForm.value;

    for (let i = 0; i < this.categories.length; i++) {
      if (this.categories[i].name === this.result.parentCategory) {
        this.parentId = this.categories[i].id;
        break;
      }
    }

    if (this.mode === 'Update') {
      this.category = new Category(this.result.name, new Category(null, null,  this.parentId),  this.category.id);
      this.categoryService.editCategory(this.category).subscribe(
        (categoryResult) => {
          console.log(categoryResult);
          this.submitted = true;
        },
        (error) => {
          console.log('Updating category failed:');
          console.log(error);
          this.error = true;
          if (typeof error.error === 'object') {
            this.errorMessage = error.error.error;
          } else {
            this.errorMessage = error.error;
          }
          this.submitted = true;
        }
      );
    } else {
      this.category = new Category(this.result.name, new Category(null, null, this.parentId), null);
      this.categoryService.createCategory(this.category).subscribe(
        (categoryResult) => {
          console.log(categoryResult);
          this.submitted = true;
        },
        (error) => {
          console.log('Creating category failed:');
          console.log(error);
          this.error = true;
          if (typeof error.error === 'object') {
            this.errorMessage = error.error.error;
          } else {
            this.errorMessage = error.error;
          }
          this.submitted = true;
        }
      );
    }
  }

  checkNameErrors() {
      if (this.categoryForm.controls.name.errors) {
        const errors = this.categoryForm.controls.name.errors;
      if (errors.required) {
        return 'Field is required.';
      }
      const result = this.checkCommonErrors(errors);
        return result ? result : null;
    }
      return null;
  }

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

  checkCommonErrors(errors) {
    if (errors.pattern) {
      return 'String contains at least one illegal character';
    }
    if (errors.maxlength) {
      return 'Maximum length exceeded.';
    }
    return null;
  }

  validateCategoryName() {
    if (this.categories && this.categoryForm && this.categoryForm.controls) {
      const value = this.categoryForm.controls.parentCategory.value;
      const valid = this.categories.map(category => category.name).includes(value);
      if (!valid) {
        return {'categoryNotFound': true};
      }
      return null;
    }
    return null;
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  ngOnInit(): void {
    this.categoryService.getCategories().subscribe(
      (categories) => {
        console.log('Getting categories.');
        this.categories = categories; },
    error => {
      console.log('Could not get categories:');
      console.log(error);
      this.error = true;
      if (typeof error.error === 'object') {
        this.errorMessage = error.error.error;
      } else {
        this.errorMessage = error.error;
      }
    }
    );
  }

}
