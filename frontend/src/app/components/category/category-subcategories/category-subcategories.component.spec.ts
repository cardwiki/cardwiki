import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CategorySubcategoriesComponent } from './category-subcategories.component';

describe('CategorySubcategoriesComponent', () => {
  let component: CategorySubcategoriesComponent;
  let fixture: ComponentFixture<CategorySubcategoriesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CategorySubcategoriesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CategorySubcategoriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
