import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../../services/auth.service';
import {Router} from '@angular/router';
import {Category} from '../../../dtos/category';
import { CategoryService } from '../../../services/category.service';

@Component({
  selector: 'app-category-form',
  templateUrl: './category-form.component.html',
  styleUrls: ['./category-form.component.css']
})
export class CategoryFormComponent implements OnInit {

  @Input() mode: String;
  @Input() id: number;
  categoryForm: FormGroup;
  submitted: boolean = false;
  error: boolean = false;
  errorMessage: string = '';
  categories: Category[];
  category: Category;

  constructor(private formBuilder: FormBuilder, private authService: AuthService,
              private router: Router, private categoryService: CategoryService) {
    this.categoryForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.pattern('^[a-zA-Z0-9]+[a-zA-Z0-9 \\/\\-\\.\\,]+$'), Validators.maxLength(200)]],
      parentCategory: ['', [Validators.pattern('^[a-zA-Z0-9]+[a-zA-Z0-9 \\/\\-\\.\\,]+$')]]
    });
  }

  submitCategoryForm() {
    this.submitted = true;
    if (!this.id) { this.id = 0; }
    const value = this.categoryForm.value;
    this.category = new Category(this.id, value.name, value.parentCategory, 0, null, null);
    if (this.mode === 'Update') {
      this.categoryService.editCategory(this.category).subscribe(
        (categoryResult) => {
          console.log(categoryResult);
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
        }
      );
    } else {
      this.categoryService.createCategory(this.category).subscribe(
        (categoryResult) => {
          console.log(categoryResult);
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
        }
      );
    }
  }

  checkNameErrors() {
    console.log(this.categoryForm.controls.name.errors);
      if (this.categoryForm.controls.name.errors) {
        const errors = this.categoryForm.controls.name.errors;
      if (errors.required) {
        return 'Field is required.';
      }
      if (errors.pattern) {
        return 'String contains at least one illegal character';
      }
      if (errors.maxlength) {
        return 'Maximum length exceeded.';
      }
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
    this.categoryService.getCategory().subscribe(
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
