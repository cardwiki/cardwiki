import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../../services/auth.service';
import {Router} from '@angular/router';
import {Category} from '../../../dtos/category';
import {CategoryService} from '../../../services/category.service';
import * as $ from 'jquery';

@Component({
  selector: 'app-category-form',
  templateUrl: './category-form.component.html',
  styleUrls: ['./category-form.component.scss']
})
export class CategoryFormComponent implements OnInit {

  @Input() mode: String;
  @Input() category: Category = new Category(null, new Category(null));
  categoryForm: FormGroup;
  submitted: boolean;
  error: boolean;
  errorMessage: string = '';

  categories: Category[];
  result: Category = new Category(null);
  parentId: number = null;

  constructor(private formBuilder: FormBuilder, private authService: AuthService,
              private router: Router, private categoryService: CategoryService) {
    this.categoryForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.pattern('^[a-zA-Z0-9]+[a-zA-Z0-9 \\/\\-\\.\\,]+$'), Validators.maxLength(200)]],
      parentCategory: ['',
        {
          validators: [Validators.maxLength(200),
            Validators.pattern('^[a-zA-Z0-9]+[a-zA-Z0-9 \\/\\-\\.\\,]+$'),
            this.validateCategoryName.bind(this)], updateOn: 'change'
        }]
    });
  }

  submitCategoryForm() {
    $('#modal').hide();
    $('.modal-backdrop').remove();
    console.log('submitted form values:', this.categoryForm.value);

    for (let i = 0; i < this.categories.length; i++) {
      if (this.categories[i].name === this.categoryForm.value.parentCategory) {
        this.parentId = this.categories[i].id;
        break;
      }
    }

    if (this.mode === 'Update') {
      this.category = new Category(this.categoryForm.value.name, new Category(null, null, this.parentId), this.category.id);
      this.categoryService.editCategory(this.category).subscribe(
        (categoryResult) => {
          console.log(categoryResult);
          this.result = categoryResult;
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
      const parent = this.parentId ? new Category(null, null, this.parentId) : null;
      this.categoryService.createCategory(new Category(this.categoryForm.value.name, parent, null))
        .subscribe((categoryResult) => {
            console.log(categoryResult);
            this.result = categoryResult;
            this.submitted = true;
            this.fetchCategories();
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
      return 'First character not alphanumeric or String contains at least one illegal character';
    }
    if (errors.maxlength) {
      return 'Maximum length exceeded.';
    }
    return null;
  }

  validateCategoryName() {
    if (this.categories && this.categoryForm && this.categoryForm.controls) {
      const value = this.categoryForm.controls.parentCategory.value;
      const valid = (value !== null && value !== '') ? this.categories.map(category => category.name).includes(value) : true;
      if (!valid) {
        return {'categoryNotFound': true};
      }
      return null;
    }
    return null;
  }

  createSubcategory() {
    if (this.result) {
      this.category.name = null;
      this.category.parent.name = this.result.name;
    }
    this.vanishResult();
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishResult() {
    this.error = false;
    this.submitted = false;
    this.categoryForm.reset();
    this.fetchCategories();
    this.setDefaults();
  }

  ngOnInit(): void {
    this.vanishResult();
  }

  onRefresh(): void {
    this.fetchCategories();
    this.categoryForm.controls['parentCategory'].setValue('');
  }

  fetchCategories() {
    this.categoryService.getCategories().subscribe(
      (categories) => {
        console.log('Getting categories.');
        this.categories = categories;
      },
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

  setDefaults(): void {
    if (this.categoryForm) {
      this.categoryForm.value.name = this.category.name;
      if (this.category.parent && this.category.parent.name) {
        this.categoryForm.controls['parentCategory'].setValue(this.category.parent.name);
      }
    }
  }

}
